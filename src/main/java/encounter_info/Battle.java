package encounter_info;

import character_info.combatant.Combatant;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public record Battle(ArrayList<Combatant> friendlies, ArrayList<Combatant> enemies, List<Scenario> scenarios) {

    public Battle(List<Combatant> friendlies, List<Combatant> enemies, List<Scenario> scenarios) {
        this(new ArrayList<>(friendlies), new ArrayList<>(enemies), new ArrayList<>(scenarios));
    }

    public boolean isEncounterOver() {
        enemies.forEach(e -> {
            if (!e.lifeStatus().isConscious()) System.out.println(e.lifeStatus().status());
        });
        return isTeamDefeated(friendlies) || isTeamDefeated(enemies);
    }

    public boolean isVictory() {
        return isTeamDefeated(enemies);
    }

    public boolean isTeamDefeated(List<Combatant> source) {
        return source.stream().noneMatch(combatant -> combatant.lifeStatus().isConscious());
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
