package combat_object.combatant.info;

import _manager.ConcentrationManager;
import combat_object.combatant.Combatant;
import lombok.*;
import org.intellij.lang.annotations.MagicConstant;

public class LifeStatus {

    public static final int ALIVE = 0, UNCONSCIOUS = 1, DEAD = 2;

    @Getter @MagicConstant(valuesFromClass = LifeStatus.class) private int status = ALIVE;
    private int successes = 0, fails = 0;

    /**
     * Logs the given death save roll in the life status
     * @param d20Roll the death save throw rolled
     */
    public void rollDeathSave(int d20Roll) {
        if (d20Roll > 20 || d20Roll < 1) throw new IndexOutOfBoundsException();

        if (d20Roll == 20) successes = 3;
        else if (d20Roll >= 10) successes++;
        else if (d20Roll > 1) fails++;
        else fails += 2;

        if (successes == 3) status = ALIVE;
        else if (fails == 3) status = DEAD;

        __main.Main.refreshUI();
    }

    /**
     * Logs the given combatant as defeated. If the combatant is an enemy
     * they are set immediately as dead, else death saving throws are started.
     * setDefeated also breaks any concentrations had by the given combatant
     * @param combatant the defeated combatant
     */
    public void setDefeated(Combatant combatant) {
        if (combatant.isEnemy()) status = DEAD;
        else status = UNCONSCIOUS;

        ConcentrationManager.breakConcentration(combatant);
    }

    /**
     * Returns whether the associated combatant is dead
     * @return true if dead, else false
     */
    public boolean isDead() {
        return status == DEAD;
    }

    /**
     * Returns whether the associated combatant is conscious
     * @return true if conscious, else false
     */
    public boolean isConscious() {
        return status == ALIVE;
    }

    @Override
    public String toString() {
        return String.format("%d - %d Saves", successes, fails);
    }

}