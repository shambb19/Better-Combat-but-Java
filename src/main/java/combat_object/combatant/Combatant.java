package combat_object.combatant;

import __main.manager.EffectManager;
import combat_object.CombatObject;
import combat_object.combatant.info.AbilityModifier;
import combat_object.combatant.info.LifeStatus;
import combat_object.combatant.info.Stats;
import combat_object.damage_implements.Effect;
import combat_object.damage_implements.Implement;
import combat_object.damage_implements.Spell;
import combat_object.damage_implements.Weapon;
import format.ColorStyles;
import lombok.*;
import lombok.experimental.*;

import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

@EqualsAndHashCode(callSuper = true) @SuperBuilder @Data
@FieldDefaults(level = AccessLevel.PROTECTED)
public abstract class Combatant extends CombatObject {

    @Builder.Default final LifeStatus lifeStatus = new LifeStatus();
    @Builder.Default final RollTracker rollTracker = new RollTracker();
    @Builder.Default final Stats stats = Stats.defaultStats();
    @Builder.Default final ArrayList<Weapon> weapons = new ArrayList<>();
    @Builder.Default final ArrayList<Spell> spells = new ArrayList<>();
    int armorClass;
    int maxHp;
    int hp;
    final boolean isEnemy;
    @Builder.Default protected int initiative = 0;
    @Builder.Default protected int numInspirationUsed = 0;

    public void useInspiration() {
        numInspirationUsed++;
    }

    public void logRoll(int roll, int numDice, int dieSize) {
        rollTracker.logRoll(roll, numDice, dieSize);
    }

    public double getLuckScore() {
        return rollTracker.rollStats[0] / rollTracker.rollStats[1];
    }

    public double getHpRatio() {
        return (double) hp / maxHp;
    }

    public void damage(int damage) {
        if (damage <= 0) throw new IndexOutOfBoundsException("Combatant.damage: hp val >= 0 expected");

        hp = Math.max(0, hp - damage);
        if (hp == 0)
            lifeStatus.setDefeated(this);
    }

    public void heal(int healthRegained) {
        if (healthRegained <= 0) throw new IndexOutOfBoundsException("Combatant.heal: hp val >= 0 expected");

        hp = Math.min(maxHp, hp + healthRegained);
    }

    public Color getHealthBarColor() {
        if (isEnemy) return ColorStyles.UNKNOWN;
        if (!lifeStatus.isConscious()) return Color.BLACK;

        double ratio = getHpRatio();
        if (ratio > 0.6) return ColorStyles.HEALTHY;
        else if (ratio > 0.25) return ColorStyles.WARNING;
        else return ColorStyles.CRITICAL;
    }

    public abstract Color getCombatantColor();

    public String getHealthBarString() {
        return switch (lifeStatus.getStatus()) {
            case LifeStatus.ALIVE -> {
                if (hp == 0) yield "Knocked Out";
                if (isEnemy()) yield "? / ?";
                yield String.format("%d / %d", hp, maxHp);
            }
            case LifeStatus.UNCONSCIOUS -> lifeStatus.toString();
            case LifeStatus.DEAD -> "Dead";
            default ->
                    throw new ClassCastException("Combatant.getHealthBarString: unexpected int lifeStatus.getStatus");
        };
    }

    public int getAttackRoll(int roll, Implement implement) {
        if (EffectManager.hasEffect(this, Effect.PENALTY_ATTACK)) {
            roll -= new Random().nextInt(7);
        }
        return roll + attackBonus(implement);
    }

    public int getSaveThrow(int roll, Implement implement) {
        if (EffectManager.hasEffect(this, Effect.STUNNED)
                && implement.getStat().equals(AbilityModifier.STR) || implement.getStat().equals(AbilityModifier.DEX)) {
            return 0;
        }
        if (EffectManager.hasEffect(this, Effect.PENALTY_SAVE)) {
            roll -= new Random().nextInt(5);
        }
        return switch (implement) {
            case Weapon ignored -> roll;
            case Spell s -> roll + mod(s.getStat());
            default -> throw new ClassCastException("Combatant.getSaveThrow: somehow not Weapon or Spell");
        };
    }

    public int mod(AbilityModifier stat) {
        return stats.mod(stat);
    }

    public int attackBonus(Implement implement) {
        return switch (implement) {
            case Weapon w -> stats.getProficiencyBonus() + stats.mod(w.getStat());
            case Spell ignored -> stats.spellAttackBonus();
            default -> throw new ClassCastException("Combatant.attackBonus: somehow not Weapon or Spell");
        };
    }

    public ArrayList<String> toTxt() {
        ArrayList<String> txt = new ArrayList<>();
        switch (this) {
            case PC ignored -> txt.add(".party");
            case NPC npc when npc.isEnemy -> txt.add(".enemy");
            case NPC ignored -> txt.add(".npc");
            default -> throw new ClassCastException("toTxt in Combatant: not instance of PC or NPC somehow");
        }
        txt.add("name: " + name);
        txt.add("hp: " + hp + "/" + maxHp);
        txt.add("ac: " + armorClass);
        return txt;
    }

    public static class RollTracker {

        final double[] rollStats = new double[]{0.0, 5.0};

        public void logRoll(int roll, int diceCount, int dieSize) {
            double expectedAverage = diceCount * ((dieSize + 1) / 2.0);

            double netLuck = roll - expectedAverage;

            rollStats[0] += netLuck;
            rollStats[1]++;
        }
    }

    @Override
    public String toString() {
        return name;
    }

}