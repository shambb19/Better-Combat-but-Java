package character_info;

import __main.Main;

public class LifeStatus {

    public enum Status {ALIVE, UNCONSCIOUS, DEAD}

    private Status thisStatus;

    private int deathSuccesses;
    private int deathFails;

    /**
     * Logs consciousness, life, and death saving throws of the root combatant.
     */
    public LifeStatus() {
        thisStatus = Status.ALIVE;
        deathSuccesses = 0;
        deathFails = 0;
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
            deathSuccesses++;
        } else {
            deathFails++;
        }

        if (deathSuccesses == 3) {
            thisStatus = Status.ALIVE;
        } else if (deathFails == 3) {
            thisStatus = Status.DEAD;
        }

        Main.logAction();
    }

    public Status status() {
        return thisStatus;
    }

    public boolean isConscious() {
        return thisStatus.equals(Status.ALIVE);
    }

    public void setUnconscious() {
        thisStatus = Status.UNCONSCIOUS;
    }

    public boolean isAlive() {
        return !thisStatus.equals(Status.DEAD);
    }

    @Override
    public String toString() {
        return String.format("Defeated (%d-%d)", deathSuccesses, deathFails);
    }

}