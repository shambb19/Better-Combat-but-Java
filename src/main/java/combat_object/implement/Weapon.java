package combat_object.implement;

import combat_object.combatant.info.AbilityModifier;
import input.syntax.Key;
import lombok.experimental.*;
import util.Roll;

import java.util.EnumMap;

import static input.syntax.Key.*;

@SuperBuilder
public class Weapon extends Implement {

    public static Weapon from(EnumMap<Key, Object> params) {
        validateAll(params, "Weapon");

        String name = (String) params.get(NAME);
        return Weapon.builder()
                .name((String) params.get(NAME))
                .roll(Roll.ofString((String) params.get(DMG)))
                .stat((AbilityModifier) params.get(STAT))
                .isManual(name != null && name.startsWith("Manual"))
                .build();
    }

    public static Weapon createManual(String name) {
        return Weapon.builder()
                .name(name)
                .roll(Roll.implementDefault())
                .stat(AbilityModifier.OPTION)
                .isManual(true)
                .build();
    }
}