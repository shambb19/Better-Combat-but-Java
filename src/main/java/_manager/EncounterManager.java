package _manager;

import combat_object.combatant.Combatant;
import combat_object.combatant.PC;
import config.Config;
import config.queue.PlayerQueue;
import encounter.Encounter;
import lombok.*;
import lombok.experimental.*;
import util.Filterable;

import java.util.List;

@UtilityClass
public class EncounterManager {

    @Getter @Setter private static Encounter encounter = null;

    @Getter private static PlayerQueue queue = null;

    public void confirmQueueFinalized() {
        var override = Config.getOverrideQueueType();

        if (override == null) {
            queue = Config.getRuleset().getPlayerQueue(getFriendlies(), getEnemies());
            return;
        }
        try {
            queue = override.getConstructor(List.class, List.class).newInstance(getFriendlies(), getEnemies());
        } catch (Exception e) {
            throw new RuntimeException("Failed to instantiate override queue", e);
        }
    }

    public List<PC> getParty() {
        return Filterable.of(encounter.getFriendlies()).castToAsList(PC.class);
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

    public void endCurrentTurn() {
        queue.endCurrentTurn();
    }
}