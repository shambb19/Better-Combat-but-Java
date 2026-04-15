package combat_object.combatant;

import __main.Main;
import lombok.*;

public class LifeStatus {

    @Getter private Status status = Status.ALIVE;
    private int deathSuccesses = 0, deathFails = 0;

    /**
     * Takes the param death save roll. Checks for values > 20 or < 1 and
     * increments either death successes or fails according to the roll.
     */
    public void rollDeathSave(int d20Roll) {
        if (d20Roll > 20 || d20Roll < 1) throw new IndexOutOfBoundsException();

        if (d20Roll >= 10)
            deathSuccesses++;
        else
            deathFails++;

        if (deathSuccesses == 3) {
            status = Status.ALIVE;
            deathSuccesses = 0;
            deathFails = 0;
        } else if (deathFails == 3) {
            status = Status.DEAD;
        }

        Main.refreshUI();
    }

    public boolean isConscious() {
        return status == Status.ALIVE;
    }

    public void setUnconscious() {
        status = Status.UNCONSCIOUS;
    }

    public boolean isAlive() {
        return status != Status.DEAD;
    }

    @Override
    public String toString() {
        return String.format("%d - %d Saves", deathSuccesses, deathFails);
    }

    public enum Status {ALIVE, UNCONSCIOUS, DEAD}
}