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

    public String getFinalHealths() {
        StringBuilder string = new StringBuilder();
        for (Combatant partyMember : friendlies()) {
            string.append(partyMember.name()).append(": ");
            if (partyMember.lifeStatus().isConscious()) {
                string.append(partyMember.getHealthString()).append("\n");
            } else if (partyMember.lifeStatus().isAlive()) {
                string.append("Unconscious\n");
            } else {
                string.append("Dead\n");
            }
        }
        return string.toString();
    }

}
