package combat_object.combatant;

import _manager.EffectManager;
import _manager.EncounterManager;
import combat_object.CombatObject;
import combat_object.combatant.info.AbilityModifier;
import combat_object.combatant.info.LifeStatus;
import combat_object.combatant.info.Stats;
import combat_object.implement.*;
import exception.InvalidParameterException;
import exception.UnexpectedClassException;
import input.CodeWriter;
import lombok.*;
import lombok.experimental.*;
import swing.ColorStyles;
import util.Filterable;
import util.Roll;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@EqualsAndHashCode(callSuper = true) @SuperBuilder @Getter @Setter
@FieldDefaults(makeFinal = true, level = AccessLevel.PROTECTED)
public abstract class Combatant extends CombatObject {

    @Builder.Default LifeStatus lifeStatus = new LifeStatus();
    @Builder.Default RollTracker rollTracker = new RollTracker();
    @Builder.Default Stats stats = Stats.defaultStats();
    @Builder.Default Set<Implement> implementList = new HashSet<>();
    int armorClass;
    boolean isEnemy;
    boolean isArmored;
    @NonFinal int maxHp, hp;
    @NonFinal protected int initiative;
    @NonFinal protected int numInspirationUsed;

    /**
     * Returns whether this combatant is the current combatant in the player queue
     * @return true if this is the current combatant, else false
     */
    public boolean isCurrentCombatant() {
        return this == EncounterManager.currentCombatant();
    }

    /**
     * Increments the number of inspiration used by this combatant.
     */
    public void useInspiration() {
        numInspirationUsed++;
    }

    /**
     * Logs the given roll in the combatant's roll tracker
     * @param result the value rolled
     * @param roll the type of roll required (i.e. 1d20, 3d6)
     */
    public void logRoll(int result, Roll roll) {
        rollTracker.logRoll(result, roll);
    }

    /**
     * Returns the combatant's luck score for the encounter (luck is a positive number
     * relating the average difference of rolls from the expected roll, and weighted
     * towards the center. I'm not sure exactly how it works because I wrote this months
     * ago and documented nothing)
     * @return the calculated luck score
     */
    public double getLuckScore() {
        return rollTracker.rollStats[0] / rollTracker.rollStats[1];
    }

    /**
     * Returns the double ratio of the combatant's hp / max hp
     * @return the calculated ratio
     */
    public double getHpRatio() {
        return (double) hp / maxHp;
    }

    /**
     * Returns the list of all damage implements of the given type
     * possessed by this combatant if they are available to use.
     * Currently, the only implement that can be made unavailable
     * is guns with the heavy attribute, access to which is lost
     * when hp <= 2
     * @param type the implement type to be returned
     * @return the given implement list
     * @param <T> the subclass of Implement to be returned
     */
    public <T extends Implement> List<T> getImplements(Class<T> type) {
        return Filterable.of(implementList, type).filteredBy(i -> {
            if (i instanceof Gun g && g.isHeavy()) return hp > 2;
            return true;
        }).toList();
    }

    /**
     * Damages the combatant by the given amount and logs them. If the
     * damage is greater than the combatant's current hp, the combatant
     * is logged as defeated
     * @param damage the damage dealt (precondition: >= 0)
     */
    public void damage(int damage) {
        if (damage < 0) throw new InvalidParameterException("Combatant.damage", "hp", damage, ">= 0");

        if (isArmored) damage = Math.max(0, damage - 2);

        hp = Math.max(0, hp - damage);
        if (hp == 0) lifeStatus.setDefeated(this);
    }

    /**
     * Heals the combatant by the given amount up to a maximum of the
     * combatant's maximum hp
     * @param healthRegained the amount to be healed (precondition: >= 0)
     */
    public void heal(int healthRegained) {
        if (healthRegained < 0) throw new InvalidParameterException("Combatant.heal", "heal", healthRegained, ">= 0");

        hp = Math.min(maxHp, hp + healthRegained);
    }

    /**
     * Returns the color of the combatant's health bar color. This is based
     * on their current hp and whether they are an enemy. Enemy health bars
     * are UNKNOWN if alive. Allies show HEALTHY above 60% hp, WARNING above
     * 25% hp, CRITICAL above 0 hp. All combatants show Color.BLACK if unconscious
     * @return the determined health bar color
     */
    public Color getHealthBarColor() {
        if (isEnemy) return ColorStyles.UNKNOWN;
        if (isUnconscious()) return Color.BLACK;

        double ratio = getHpRatio();
        if (ratio > 0.6) return ColorStyles.HEALTHY;
        else if (ratio > 0.25) return ColorStyles.WARNING;
        else return ColorStyles.CRITICAL;
    }

    /**
     * Returns the combatant's color for most GUI features.
     * Party members show PARTY, allied npcs show NPC, and enemies
     * show ENEMY. Pretty intuitive
     * @return the determined color
     */
    public abstract Color getCombatantColor();

    /**
     * Returns the combatant's health bar String. Allies show the
     * ratio "hp / max hp" (i.e. "3 / 12") if alive, or a String
     * representing the number of rolled death save throws if
     * unconscious. Enemies show a String translating to a ballpark
     * of their health, from "Healthy", "Injured", "Bloodied", and
     * "Critical". All combatants show "Dead" if dead
     * @return the determined String
     */
    public String getHealthBarString() {
        if (isUnconscious()) return lifeStatus.toString();
        if (isDead()) return "Dead";
        if (hp == 0) return "Knocked Out";
        return String.format("%d / %d", hp, maxHp);
    }

    /**
     * Returns the given attack roll adjusted for the given implement's
     * attack bonus and any detrimental effects the combatant has
     * @param roll the rolled value
     * @param implement the implement used
     * @return the calculated attack roll
     */
    public int getAttackRoll(int roll, Implement implement) {
        if (EffectManager.hasEffect(this, Effect.PENALTY_ATTACK)) {
            roll -= new Random().nextInt(7);
        }
        return roll + attackBonus(implement);
    }

    /**
     * Returns the combatant's save roll, being the given roll adjusted
     * for save bonus (if the attacker used a spell) and any detrimental
     * effects the combatant has
     * @param roll the rolled value
     * @param implement the implement used
     * @return the calculated save roll
     */
    public int getSaveThrow(int roll, Implement implement) {
        if (EffectManager.hasEffect(this, Effect.STUNNED)
                && implement.getStat().equals(AbilityModifier.STR) || implement.getStat().equals(AbilityModifier.DEX)) {
            return 0;
        }
        if (EffectManager.hasEffect(this, Effect.PENALTY_SAVE)) {
            roll -= new Random().nextInt(5);
        }

        if (implement instanceof Spell s) return roll + mod(s.getStat());
        return roll;
    }

    /**
     * Returns the combatant's skill modifier for the given stat.
     * A boilerplate-reducing version of Stats.mod
     * @param stat the queried stat
     * @return the combatant's skill modifier for the stat parameter
     */
    public int mod(AbilityModifier stat) {
        return stats.mod(stat);
    }

    /**
     * Returns the combatant's attack bonus with the given implement
     * @param implement the implement queried
     * @return the combatant's attack bonus with the implement parameter
     */
    public int attackBonus(Implement implement) {
        return switch (implement) {
            case Weapon w -> stats.getProficiencyBonus() + stats.mod(w.getStat());
            case Spell ignored -> stats.spellAttackBonus();
            case Gun ignored -> 0;
            default -> throw new UnexpectedClassException(implement, Weapon.class, Spell.class, Gun.class);
        };
    }

    /**
     * Returns whether the combatant is conscious
     * @return true if conscious, else false
     */
    public boolean isConscious() {
        return lifeStatus.isConscious();
    }

    /**
     * Returns whether the combatant is unconscious
     * @return true if unconscious, else false
     */
    public boolean isUnconscious() {
        return !isConscious();
    }

    /**
     * Returns whether the combatant is alive
     * @return true if alive, else false
     */
    public boolean isAlive() {
        return !isDead();
    }

    /**
     * Returns whether the combatant is dead
     * @return true if dead, else false
     */
    public boolean isDead() {
        return lifeStatus.isDead();
    }

    /**
     * Returns the lines of code associated with this Combatant object
     * @return the .txt code lines
     */
    public CodeWriter toTxt() {
        String header = CombatantOption.fromCombatant(this).getCodeHeader();
        return new CodeWriter(header)
                .withLine("name", name)
                .withLine("hp", hp + "/" + maxHp)
                .withLine("ac", armorClass);
    }

    /**
     * Returns a partitioned map of the provided iterable, with get(true) returning
     * all enemies in the list and get(false) returning all allies
     * @param list the iterable to partition
     * @return the partitioned list
     */
    public static Map<Boolean, List<Combatant>> enemyPartition(Iterable<Combatant> list) {
        return StreamSupport.stream(list.spliterator(), false).collect(Collectors.partitioningBy(Combatant::isEnemy));
    }

    /**
     * Tracks rolls and relates the average difference of rolls
     * from the expected roll, weighted towards the center
     */
    public static class RollTracker {

        final double[] rollStats = new double[]{0.0, 5.0};

        public void logRoll(int result, Roll roll) {
            double expectedAverage = roll.numDice() * ((roll.dieSize() + 1) / 2.0);

            double netLuck = result - expectedAverage;

            rollStats[0] += netLuck;
            rollStats[1]++;
        }
    }

    @Override
    public String toString() {
        return name;
    }

}