package combatants;

public class LifeStatus {

    private boolean isConscious;
    private boolean isAlive;

    private int successes;
    private int fails;

    public LifeStatus() {
        isConscious = true;
        isAlive = true;
        successes = 0;
        fails = 0;
    }

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

    private void updateLifeStatus() {
        if (!isAlive || isConscious) {
            return;
        }
        if (successes == 3) {
            isConscious = true;
        } else if (fails == 3) {
            isAlive = false;
        }
    }

}
