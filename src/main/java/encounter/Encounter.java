package encounter;

import combat_object.combatant.Combatant;
import combat_object.scenario.Scenario;
import lombok.*;
import lombok.experimental.*;

import java.util.List;
import java.util.Map;

@Value public class Encounter {

    List<Scenario> scenarios;
    List<Combatant> friendlies, enemies;

    public Encounter(List<Scenario> scenarios, Map<Boolean, List<Combatant>> partitionedList) {
        this.scenarios = scenarios;
        friendlies = partitionedList.get(false);
        enemies = partitionedList.get(true);
    }

    public boolean isEncounterOver() {
        return isTeamDefeated(friendlies) || isTeamDefeated(enemies);
    }

    private boolean isTeamDefeated(List<Combatant> source) {
        return source.stream().noneMatch(Combatant::isConscious);
    }

    public boolean isVictory() {
        return isTeamDefeated(enemies);
    }

    public String percentToVictory() {
        int healthSumMax = enemies.stream().mapToInt(Combatant::getMaxHp).sum();
        int healthSumFinal = enemies.stream().mapToInt(Combatant::getHp).sum();

        return (int) (100 * (1.0 - (double) healthSumFinal / healthSumMax)) + "%";
    }

}