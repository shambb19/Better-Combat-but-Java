package combatants;

import damage.Spell;
import damage.Weapon;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class Combatant {

    private String name;

    private int initiative;

    private int armorClass;
    private boolean isEnemy;
    private int inspiration;
    private Stats stats;

    private int hpMax;
    private int hpCurrent;
    private final LifeStatus lifeStatus = new LifeStatus();

    private JProgressBar healthBar;

    private ArrayList<Weapon> weapons = null;
    private ArrayList<Spell> spells = null;

    public Combatant(String name, int hpMax, int armorClass, boolean isEnemy) {
        defaultConstructor(name, hpMax, armorClass, isEnemy);
    }

    public Combatant(String name, int hpMax, int armorClass, boolean isEnemy,
                     Stats stats
    ) {
        defaultConstructor(name, hpMax, armorClass, isEnemy);

        this.stats = stats;
    }

    public Combatant(
            String name, int hpMax, int armorClass, boolean isEnemy,
            Stats stats, ArrayList<Weapon> weapons, ArrayList<Spell> spells
    ) {
        defaultConstructor(name, hpMax, armorClass, isEnemy);

        this.stats = stats;

        this.weapons = weapons;
        this.spells = spells;
    }

    private void defaultConstructor(String name, int hpMax, int armorClass, boolean isEnemy) {
        this.name = name;
        this.hpMax = hpMax;
        this.armorClass = armorClass;
        this.isEnemy = isEnemy;

        hpCurrent = hpMax;
        inspiration = 0;
    }

    public boolean useInspirationAndCheckExcess() {
        inspiration++;
        return inspiration > 2;
    }

    public void damage(int damage) {
        hpCurrent = Math.max(0, hpCurrent - damage);
        if (hpCurrent == 0) {
            lifeStatus.setUnconscious();
        }
    }

    public void heal(int healthRegained) {
        hpCurrent = Math.min(hpMax, hpCurrent + healthRegained);
    }

    public int hp() {
        return hpCurrent;
    }

    public int maxHp() {
        return hpMax;
    }

    public double getHealthPercent() {
        return (double) hpCurrent / hpMax;
    }

    public Color getHealthBarColor() {
        if (!lifeStatus.isConscious()) {
            return Color.BLACK;
        }

        if (getHealthPercent() > 0.6) {
            return Color.GREEN;
        } else if (getHealthPercent() > 0.25) {
            return Color.YELLOW;
        } else {
            return Color.RED;
        }
    }

    public String getHealthString() {
        return hpCurrent + "/" + hpMax;
    }

    public void setHealth(int newHealth) {
        hpCurrent = newHealth;
    }

    public JProgressBar getHealthBar() {
        return healthBar;
    }

    public void setHealthBar(JProgressBar healthBar) {
        this.healthBar = healthBar;
    }

    public int getInitiative() {
        return initiative;
    }

    public void setInitiative(int initiative) {
        this.initiative = initiative;
    }

    public LifeStatus lifeStatus() {
        return lifeStatus;
    }

    public boolean isEnemy() {
        return isEnemy;
    }

    public int ac() {
        return armorClass;
    }

    public boolean hasWeapons() {
        return weapons != null;
    }

    public ArrayList<Weapon> weapons() {
        return weapons;
    }

    public boolean hasSpells() {
        return spells != null;
    }

    public ArrayList<Spell> spells() {
        return spells;
    }

    public Stats stats() {
        return stats;
    }

    @Override
    public String toString() {
        String toString = name + "\n";
        toString += "Initiative: " + initiative + "\n";
        toString += "Inspirations Used: " + inspiration + "/2";

        return toString;
    }

    public String name() {
        return name;
    }

}