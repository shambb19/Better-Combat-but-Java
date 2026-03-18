package encounter_info;

import _global_list.Combatants;
import _global_list.Scenarios;
import character_info.combatant.Combatant;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public record Battle(List<Combatant> friendlies, List<Combatant> enemies, ArrayList<Scenario> scenarios) {

    public Battle create() {
        var combatantsByAllegiance = Combatants.toList().stream()
                .collect(Collectors.partitioningBy(Combatant::isEnemy));

        return new Battle(
                combatantsByAllegiance.get(false),
                combatantsByAllegiance.get(true),
                Scenarios.toList()
        );
    }

    public void reset() {
        friendlies.clear();
        friendlies.addAll(create().friendlies);

        enemies.clear();
        enemies.addAll(create().enemies);
    }

    public boolean isEncounterOver() {
        return isTeamDefeated(friendlies) || isTeamDefeated(enemies);
    }

    public boolean isVictory() {
        return isTeamDefeated(enemies);
    }

    public boolean isTeamDefeated(List<Combatant> source) {
        for (Combatant combatant : source) {
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
