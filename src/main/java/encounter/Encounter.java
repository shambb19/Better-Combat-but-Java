package encounter;

import combat_object.combatant.Combatant;
import combat_object.scenario.Scenario;
import lombok.*;
import lombok.experimental.*;

import java.util.List;

@Value @AllArgsConstructor public class Encounter {

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
        int healthSumMax = enemies.stream().mapToInt(Combatant::getMaxHp).sum();
        int healthSumFinal = enemies.stream().mapToInt(Combatant::getHp).sum();

        return (100 * (1 - (healthSumFinal / healthSumMax))) + "%";
    }

}