package scenarios;

import combatants.Combatant;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class Battle {

    private final ArrayList<Combatant> friendlies;
    private final ArrayList<Combatant> enemies;

    public Battle(ArrayList<Combatant> friendlies, ArrayList<Combatant> enemies) {
        this.friendlies = friendlies;
        this.enemies = enemies;
    }

    public ArrayList<Combatant> getFriendlies() {
        return friendlies;
    }

    public ArrayList<Combatant> getEnemies() {
        return enemies;
    }

    public boolean areAllEnemiesDefeated() {
        for (Combatant combatant : getEnemies()) {
            if (combatant.lifeStatus().isConscious()) {
                return false;
            }
        }
        return true;
    }

    public boolean areAllFriendliesDefeated() {
        for (Combatant combatant : getFriendlies()) {
            if (combatant.lifeStatus().isConscious()) {
                return false;
            }
        }
        return true;
    }

    public String percentToVictory() {
        int healthSumMax = 0;
        int healthSumFinal = 0;
        for (Combatant enemy : getEnemies()) {
            healthSumMax += enemy.maxHp();
            healthSumFinal += enemy.hp();
        }
        double percentDecimal = (double) (healthSumMax - healthSumFinal) / healthSumMax;
        return new DecimalFormat("##").format(100 * percentDecimal) + "%";
    }

    public String getFinalHealths() {
        StringBuilder string = new StringBuilder();
        for (Combatant partyMember : getFriendlies()) {
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
