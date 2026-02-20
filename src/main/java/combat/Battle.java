package combat;

import combatants.Combatant;

import java.text.DecimalFormat;
import java.util.ArrayList;

public record Battle(ArrayList<Combatant> friendlies, ArrayList<Combatant> enemies) {

    public boolean areAllEnemiesDefeated() {
        for (Combatant combatant : enemies()) {
            if (combatant.lifeStatus().isConscious()) {
                return false;
            }
        }
        return true;
    }

    public boolean areAllFriendliesDefeated() {
        for (Combatant combatant : friendlies()) {
            if (combatant.lifeStatus().isConscious()) {
                return false;
            }
        }
        return true;
    }

    public String percentToVictory() {
        int healthSumMax = 0;
        int healthSumFinal = 0;
        for (Combatant enemy : enemies()) {
            healthSumMax += enemy.maxHp();
            healthSumFinal += enemy.hp();
        }
        double percentDecimal = (double) (healthSumMax - healthSumFinal) / healthSumMax;
        return new DecimalFormat("##").format(100 * percentDecimal) + "%";
    }

}
