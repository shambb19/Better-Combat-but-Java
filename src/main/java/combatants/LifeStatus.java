package combatants;

import combat.Main;

public class LifeStatus {

    private boolean isConscious;
    private boolean isAlive;

    private int successes;
    private int fails;

    /**
     * Logs consciousness, life, and death saving throws of the root combatant.
     */
    public LifeStatus() {
        isConscious = true;
        isAlive = true;
        successes = 0;
        fails = 0;
    }

    /**
     * takes the param death save roll. Checks for values > 20 or < 1 and
     * increments either death successes or fails according to the roll.
     * Calls updateLifeStatus() to handle death/resurrection
     */
    public void rollDeathSave(int d20Roll) {
        if (d20Roll > 20 || d20Roll < 1) {
            throw new IndexOutOfBoundsException();
        }
        if (d20Roll > 10) {
            successes++;
        } else {
            fails++;
        }
        updateLifeStatus();
    }

    public int getSuccesses() {
        return successes;
    }

    public int getFails() {
        return fails;
    }

    public boolean isConscious() {
        return isConscious;
    }

    public void setUnconscious() {
        isConscious = false;
    }

    public boolean isAlive() {
        return isAlive;
    }

    /**
     * Returns player to consciousness or death on 3 respective death successes
     * or fails, then updates the menu (easier than locating the specific combatant's
     * health bar).
     */
    private void updateLifeStatus() {
        if (!isAlive || isConscious) {
            return;
        }
        if (successes == 3) {
            isConscious = true;
        } else if (fails == 3) {
            isAlive = false;
        }
        Main.menu.update();
    }

}
