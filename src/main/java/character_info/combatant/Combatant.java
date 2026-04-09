package character_info.combatant;

import __main.manager.EffectManager;
import character_info.AbilityModifier;
import character_info.LifeStatus;
import character_info.Stats;
import damage_implements.Effect;
import damage_implements.Spell;
import damage_implements.Weapon;
import format.ColorStyles;

import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.util.ArrayList;
import java.util.List;

public abstract class Combatant {

    public static final DataFlavor COMBATANT_FLAVOR = new DataFlavor(Combatant.class, "Combatant");

    protected final LifeStatus lifeStatus = new LifeStatus();
    protected String name;
    protected int armorClass;
    protected int hpMax, hpCurrent;
    protected int initiative, inspiration;
    protected final RollTracker rollTracker;
    protected double[] rollQualityData;

    /**
     * NPC constructor
     */
    public Combatant(String name, int hpMax, int armorClass) {
        this.name = name;
        this.hpMax = hpMax;
        this.armorClass = armorClass;

        hpCurrent = hpMax;
        inspiration = 0;
        rollTracker = new RollTracker();
        rollQualityData = new double[]{0, 0, 0};
    }

    /**
     * Increments the number of inspirations used
     */
    public void useInspiration() {
        inspiration++;
    }

    public int numInspirationUsed() {
        return inspiration;
    }

    public void logD20Roll(int roll) {
        rollTracker.logRoll(roll, 1, 20);
    }

    public void logRoll(int roll, int numDice, int dieSize) {
        rollTracker.logRoll(roll, numDice, dieSize);
    }

    public double getLuckScore() {
        return rollTracker.getLuckScore();
    }

    /**
     * Deals the param damage. Logs the combatant as unconscious if hp is 0.
     *
     * @param damage The input amount of damage
     */
    public void damage(int damage) {
        hpCurrent = Math.max(0, hpCurrent - damage);
        if (hpCurrent == 0)
            lifeStatus.setUnconscious();
    }

    /**
     * Heals the combatant for the param amount without over-healing
     */
    public void heal(int healthRegained) {
        hpCurrent = Math.min(hpMax, hpCurrent + healthRegained);
    }

    public int hp() {
        return hpCurrent;
    }

    public int maxHp() {
        return hpMax;
    }

    /**
     * @return black if combatant is unconscious, green for safe hp, yellow for low hp, red for critical hp
     */
    public Color getHealthBarColor() {
        if (!lifeStatus.isConscious())
            return Color.BLACK;
        else
            return ColorStyles.getPercentColor(hpCurrent, hpMax);
    }

    public String getHealthBarString() {
        return switch (lifeStatus.status()) {
            case ALIVE -> {
                if (hpCurrent == 0)
                    yield "Knocked Out";
                else if (isEnemy())
                    yield "? / ?";
                yield String.format("%d / %d", hpCurrent, hpMax);
            }
            case UNCONSCIOUS -> lifeStatus.toString();
            case DEAD -> "Dead";
        };
    }

    public void setHealth(int newHealth) {
        hpCurrent = newHealth;
    }

    public int initiative() {
        return initiative;
    }

    public void setInitiative(int initiative) {
        this.initiative = initiative;
    }

    public LifeStatus lifeStatus() {
        return lifeStatus;
    }

    public boolean isEnemy() {
        return false;
    }

    public int ac() {
        return armorClass;
    }

    public int mod(AbilityModifier stat) {
        return 0;
    }

    public int attackBonus(Weapon weapon) {
        return 0;
    }

    public int spellAttackBonus() {
        return 0;
    }

    public int saveDc() {
        return 10;
    }

    public abstract List<Weapon> weapons();

    public abstract List<Spell> spells();

    public abstract Stats stats();

    public boolean canHeal() {
        return !EffectManager.hasEffect(this, Effect.HEAL_BLOCK);
    }

    /**
     * Logs an effect dealt to the target param by the root combatant. Because most effects end
     * on the attacker's next turn, these are handles in the static DealtEffectsList class in
     * Combatant.
     *
     * @param target      the combatant to whom the effect is dealt
     * @param dealtEffect the effect dealt
     */
    public void putEffect(Combatant target, Effect dealtEffect) {
        EffectManager.logEffect(target, this, dealtEffect);
    }

    public boolean isHexedBy(Combatant hexer) {
        return EffectManager.isHexedBy(this, hexer);
    }

    @Override
    public String toString() {
        return name;
    }

    public abstract ArrayList<String> toTxt();

    public String name() {
        return name;
    }

    public static class RollTracker {

        private final double[] rollStats = new double[]{0.0, 5.0};

        /**
         * @param roll      The total result of the dice
         * @param diceCount Number of dice (e.g., 3 for 3d8)
         * @param dieSize   Size of dice (e.g., 8 for 3d8)
         */
        public void logRoll(int roll, int diceCount, int dieSize) {
            double expectedAverage = diceCount * ((dieSize + 1) / 2.0);

            double netLuck = roll - expectedAverage;

            rollStats[0] += netLuck;
            rollStats[1]++;
        }

        public double getLuckScore() {
            if (rollStats[1] == 0) return 0;
            return rollStats[0] / rollStats[1];
        }
    }

}