package encounter;

import combat_object.combatant.Combatant;
import combat_object.combatant.NPC;
import combat_object.scenario.Scenario;
import lombok.*;
import lombok.experimental.*;
import util.Filterable;

import java.util.List;
import java.util.stream.Stream;

@Value @AllArgsConstructor public class Encounter {

    List<Scenario> scenarios;
    @NonFinal @Setter List<Combatant> friendlies, enemies;

    public List<NPC> getAllNpcs() {
        return Filterable.of(Stream.concat(friendlies.stream(), enemies.stream())).castToAsList(NPC.class);
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