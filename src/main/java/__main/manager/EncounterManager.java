package __main.manager;

import combat_object.combatant.Combatant;
import combat_object.combatant.PC;
import encounter_info.Encounter;
import encounter_info.PlayerQueue;
import lombok.*;
import lombok.experimental.*;
import util.Filter;

import java.util.List;

@UtilityClass
public class EncounterManager {

    @Getter @Setter private static Encounter encounter = null;

    @Getter private static PlayerQueue queue = null;

    public void confirmQueueFinalized() {
        queue = new PlayerQueue(encounter.getFriendlies(), encounter.getEnemies());
    }

    public List<PC> getParty() {
        return Filter.matchingClass(encounter.getFriendlies(), PC.class);
    }

    public List<Combatant> getFriendlies() {
        return encounter.getFriendlies();
    }

    public List<Combatant> getEnemies() {
        return encounter.getEnemies();
    }

    public Combatant getCurrentCombatant() {
        return queue.getCurrentCombatant();
    }

}