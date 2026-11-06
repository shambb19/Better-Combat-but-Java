package _manager;

import combat_object.combatant.Combatant;
import combat_object.implement.Gun;
import lombok.*;
import util.Filterable;

import java.util.HashSet;
import java.util.Set;

public class MisfireManager {

    @Getter private static final Set<Misfire> activeMisfires = new HashSet<>();
    @Getter private static final Set<Misfire> discardedWeapons = new HashSet<>();

    /**
     * Logs that the given Gun in the possession of the Given combatant
     * has had a misfire, then removes the Gun from the Combatant's
     * implement list
     * @param combatant the Combatant who owns the Gun
     * @param gun the Gun that has misfired
     */
    public static void logMisfire(Combatant combatant, Gun gun) {
        activeMisfires.add(new Misfire(combatant, gun));
        combatant.getImplementList().remove(gun);
    }

    /**
     * Given the Combatant and Gun within the given Misfire object,
     * logs that the Combatant has repaired the Gun in question, then
     * re-adds the Gun to the Combatant's implement list
     * @param misfire the Misfire object that includes the Combatant
     *                that has repaired their Gun
     */
    public static void logRepair(Misfire misfire) {
        activeMisfires.removeIf(misfire::equals);
        misfire.combatant.getImplementList().add(misfire.gun);
    }

    /**
     * Given the Combatant and Gun within the given Misfire object,
     * logs that the Combatant has discarded the Gun in question as
     * opposed to attempting a repair. Then it removes the Misfire
     * object from the active misfires Set and confirms that the Gun
     * has been removed from the Combatant's implement list
     * @param misfire the Misfire object that includes the Combatant
     *                that has discarded their Gun
     */
    public static void logDiscard(Misfire misfire) {
        activeMisfires.remove(misfire);
        discardedWeapons.add(misfire);
        misfire.combatant.getImplementList().remove(misfire.gun);
    }

    /**
     * Returns a Set of all Misfires for which its Combatant object
     * is the encounter queue's current combatant
     * @return the filtered Set
     */
    public static Set<Misfire> getCurrentCombatantMisfires() {
        return Filterable.of(activeMisfires).filteredBy(Misfire::combatant, EncounterManager.currentCombatant()).toSet();
    }

    /**
     * A data holder representing a gun misfire and including the
     * Gun that misfired and the Combatant who owns it
     * @param combatant the Combatant who owns the Gun
     * @param gun the Gun that misfired
     */
    public record Misfire(Combatant combatant, Gun gun) {}

}