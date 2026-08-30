package _manager;

import combat_object.combatant.Combatant;
import combat_object.implement.Gun;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

public class MisfireManager {

    @Getter private static final List<Misfire> activeMisfires = new ArrayList<>();
    @Getter private static final List<Misfire> discardedWeapons = new ArrayList<>();

    public static void logMisfire(Combatant combatant, Gun gun) {
        activeMisfires.add(new Misfire(combatant, gun));
        combatant.getImplementList().remove(gun);
    }

    public static void logRepair(Misfire misfire) {
        activeMisfires.removeIf(misfire::equals);
        misfire.combatant.getImplementList().add(misfire.gun);
    }

    public static void logDiscard(Misfire misfire) {
        activeMisfires.remove(misfire);
        discardedWeapons.add(misfire);
        misfire.combatant.getImplementList().remove(misfire.gun);
    }

    public static List<Misfire> getCurrentCombatantMisfires() {
        return activeMisfires.stream().filter(
                m -> m.combatant.equals(EncounterManager.getCurrentCombatant())
        ).toList();
    }

    public record Misfire(Combatant combatant, Gun gun) {}

}