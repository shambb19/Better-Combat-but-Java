package combat_object.combatant.info;

import __main.manager.ConcentrationManager;
import combat_object.combatant.Combatant;
import lombok.*;
import org.intellij.lang.annotations.MagicConstant;

public class LifeStatus {

    public static final int ALIVE = 0, UNCONSCIOUS = 1, DEAD = 2;

    @Getter @MagicConstant(valuesFromClass = LifeStatus.class) private int status = ALIVE;
    private int successes = 0, fails = 0;

    public void rollDeathSave(int d20Roll) {
        if (d20Roll > 20 || d20Roll < 1) throw new IndexOutOfBoundsException();

        if (d20Roll >= 10) successes++;
        else fails++;

        if (successes == 3) status = ALIVE;
        else if (fails == 3) status = DEAD;

        __main.Main.refreshUI();
    }

    public void setDefeated(Combatant combatant) {
        if (combatant.isEnemy())
            status = DEAD;
        else
            status = UNCONSCIOUS;
        ConcentrationManager.breakConcentration(combatant);
    }

    public boolean isConscious() {
        return status == ALIVE;
    }

    public boolean isAlive() {
        return status != DEAD;
    }

    @Override
    public String toString() {
        return String.format("%d - %d Saves", successes, fails);
    }

}