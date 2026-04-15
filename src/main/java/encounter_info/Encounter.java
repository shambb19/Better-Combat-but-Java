package encounter_info;

import combat_object.combatant.Combatant;
import combat_object.scenario.Scenario;
import lombok.*;
import lombok.experimental.*;

import java.text.DecimalFormat;
import java.util.List;

@Value public class Encounter {

    List<Scenario> scenarios;
    @NonFinal @Setter List<Combatant> friendlies, enemies;

    public boolean isEncounterOver() {
        return isTeamDefeated(friendlies) || isTeamDefeated(enemies);
    }

    private boolean isTeamDefeated(List<Combatant> source) {
        return source.stream().noneMatch(combatant -> combatant.getLifeStatus().isConscious());
    }

    public boolean isVictory() {
        return isTeamDefeated(enemies);
    }

    public String percentToVictory() {
        int healthSumMax = 0;
        int healthSumFinal = 0;
        for (Combatant enemy : enemies) {
            healthSumMax += enemy.getMaxHp();
            healthSumFinal += enemy.getHp();
        }
        double percentDecimal = (double) (healthSumMax - healthSumFinal) / healthSumMax;
        return new DecimalFormat("##").format(100 * percentDecimal) + "%";
    }

}
