package combat_object.implement;

import combat_object.combatant.info.AbilityModifier;
import input.syntax.Key;
import lombok.experimental.*;
import util.Roll;

import java.util.EnumMap;

import static input.syntax.Key.*;

@SuperBuilder @FieldDefaults(makeFinal = true)
public class Weapon extends Implement {

    public static Weapon MANUAL = createManual("Manual");
    public static Weapon HEADER = createManual("── Weapon ──");

    /**
     * Returns a Weapon object from the given parameters
     * @param params the Weapon instance variables, encoded as
     *               Objects and decoded in the Key class
     * @return the new Weapon object
     */
    public static Weapon from(EnumMap<Key, Object> params) {
        validateAll(params, "Weapon");

        String name = (String) params.get(NAME);
        return Weapon.builder()
                .name(name)
                .roll(Roll.ofString((String) params.get(DMG)))
                .stat((AbilityModifier) params.get(STAT))
                .isManual(name != null && name.startsWith("Manual"))
                .build();
    }

    /**
     * Returns a default Weapon object with generic statistics, to be
     * added for all Combatants as a means of attacking with an Implement
     * not loaded into the Encounter
     * @param name the name of the manual item
     * @return the new Weapon object
     */
    public static Weapon createManual(String name) {
        return Weapon.builder()
                .name(name)
                .roll(Roll.implementDefault())
                .stat(AbilityModifier.OPTION)
                .isManual(true)
                .build();
    }
}