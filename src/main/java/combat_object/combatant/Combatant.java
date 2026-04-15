package combat_object.combatant;

import combat_object.CombatObject;
import combat_object.damage_implements.Spell;
import combat_object.damage_implements.Weapon;
import format.ColorStyles;
import lombok.*;
import lombok.experimental.*;

import java.awt.*;
import java.util.ArrayList;

@SuperBuilder
@Data
@FieldDefaults(level = AccessLevel.PROTECTED)
public abstract class Combatant implements CombatObject {

    @Builder.Default final LifeStatus lifeStatus = new LifeStatus();
    @Builder.Default final RollTracker rollTracker = new RollTracker();
    @Builder.Default final Stats stats = Stats.defaultStats();
    @Builder.Default final ArrayList<Weapon> weapons = new ArrayList<>();
    @Builder.Default final ArrayList<Spell> spells = new ArrayList<>();
    final String name;
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
        hp = Math.max(0, hp - damage);
        if (hp == 0)
            lifeStatus.setUnconscious();
    }

    public void heal(int healthRegained) {
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

    public String getHealthBarString() {
        return switch (lifeStatus.getStatus()) {
            case ALIVE -> {
                if (hp == 0) yield "Knocked Out";
                if (isEnemy()) yield "? / ?";
                yield String.format("%d / %d", hp, maxHp);
            }
            case UNCONSCIOUS -> lifeStatus.toString();
            case DEAD -> "Dead";
        };
    }

    public int mod(AbilityModifier stat) {
        return stats.mod(stat);
    }

    public int attackBonus(Weapon weapon) {
        return stats.getProficiencyBonus() + stats.mod(weapon.getStat());
    }

    public int spellAttackBonus() {
        return stats.spellAttackBonus();
    }

    public int saveDc() {
        return stats.saveDc();
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