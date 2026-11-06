package _manager;

import combat_object.combatant.Combatant;
import combat_object.combatant.PC;
import config.Config;
import config.queue.PlayerQueue;
import encounter.Encounter;
import exception.ProgramRunError;
import lombok.*;
import lombok.experimental.*;
import util.Filterable;

import java.util.Set;

@UtilityClass
public class EncounterManager {

    @Getter @Setter private static Encounter encounter = null;

    @Getter private static PlayerQueue queue = null;

    /**
     * Sets the encounter queue type based on the file input's
     * specifications. If a Queue override exists in the source file,
     * that queue type is initialized regardless of the requested Ruleset.
     * Otherwise, the Queue type native to the requested Ruleset will be
     * used
     */
    public void confirmQueueFinalized() {
        var override = Config.getOverrideQueueType();

        if (override == null) {
            queue = Config.getRuleset().getPlayerQueue(friendlies(), enemies());
            return;
        }
        try {
            queue = override.getConstructor(Set.class, Set.class).newInstance(friendlies(), enemies());
        } catch (Exception e) {
            throw new ProgramRunError("failed to instantiate override queue");
        }
    }

    /**
     * Returns a Set of all party members in the encounter
     * @return the new Set
     */
    public Set<PC> party() {
        return Filterable.of(encounter.getFriendlies(), PC.class).toSet();
    }

    /**
     * Returns a Set of all friendly Combatants (both PC and NPC) in the
     * encounter
     * @return the new Set
     */
    public Set<Combatant> friendlies() {
        return encounter.getFriendlies();
    }

    /**
     * Returns a Set of all enemy Combatants in the encounter
     * @return the new Set
     */
    public Set<Combatant> enemies() {
        return encounter.getEnemies();
    }

    /**
     * Returns the Combatant currently taking their turn in the encounter Queue
     * @return the Combatant taking their turn
     */
    public Combatant currentCombatant() {
        return queue.getCurrentCombatant();
    }

    /**
     * Immediately and without further action ends the turn of the encounter
     * Queue's current Combatant;
     */
    public void endCurrentTurn() {
        queue.endCurrentTurn();
    }
}