package combatants;

import java.awt.*;

public class Combatant {

    protected String name;

    protected int initiative;

    protected int hpMax;
    protected int hpCurrent;
    protected LifeStatus lifeStatus = new LifeStatus();

    protected int armorClass;

    protected boolean isEnemy;

    protected int inspiration;

    public Combatant(String name, int hpMax, int armorClass, boolean isEnemy) {
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

    public void setInitiative(int initiative) {
        this.initiative = initiative;
    }

    public int getCurrentHealth() {
        return hpCurrent;
    }

    public int getMaximumHealth() {
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

    public int getInitiative() {
        return initiative;
    }

    public LifeStatus getLifeStatus() {
        return lifeStatus;
    }

    public boolean isEnemy() {
        return isEnemy;
    }

    public int getArmorClass() {
        return armorClass;
    }

    @Override
    public String toString() {
        String toString = name + "\n";
        if (lifeStatus.isConscious()) {
            if (isEnemy) {
                toString += "Health: ?\n";
            } else {
                toString += "Health: " + getHealthString() + "\n";
            }
        } else if (lifeStatus.isAlive()) {
            toString += "Unconscious (" + lifeStatus.getSuccesses() + " life saves, " + lifeStatus.getFails() + " fails)\n";
        }
        toString += "Initiative: " + initiative + "\n";
        toString += "Inspirations Used: " + inspiration + "/2";

        return toString;
    }

    public String getName() {
        return name;
    }

}
