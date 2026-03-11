package scenario_info;

import character_info.combatant.Combatant;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

public record Battle(ArrayList<Combatant> friendlies, ArrayList<Combatant> friendliesOriginal,
                     ArrayList<Combatant> enemies, ArrayList<Combatant> enemiesOriginal,
                     ArrayList<Scenario> scenarios) {

    public HashMap<Combatant, Integer> mapFriendlies() {
        return toMap(friendliesOriginal);
    }

    public HashMap<Combatant, Integer> mapEnemies() {
        return toMap(enemiesOriginal);
    }

    public HashMap<Combatant, Integer> toMap(ArrayList<Combatant> source) {
        HashMap<Combatant, Integer> map = new HashMap<>();
        source.forEach(combatant -> map.put(combatant, 1));
        return map;
    }

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
