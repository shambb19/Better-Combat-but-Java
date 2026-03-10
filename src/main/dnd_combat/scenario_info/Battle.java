package scenario_info;

import character_info.combatant.Combatant;

import java.text.DecimalFormat;
import java.util.ArrayList;

public record Battle(ArrayList<Combatant> friendlies, ArrayList<Combatant> friendliesOriginal,
                     ArrayList<Combatant> enemies, ArrayList<Combatant> enemiesOriginal,
                     ArrayList<Scenario> scenarios) {

    public void reset() {
        friendlies.clear();
        friendlies.addAll(friendliesOriginal);

        enemies.clear();
        enemies.addAll(enemiesOriginal);
    }

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

    /**
     * @return the percent, rounded to the nearest whole number, of enemy hp successfully taken by the friendly team.
     */
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
