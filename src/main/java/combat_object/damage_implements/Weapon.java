package combat_object.damage_implements;

import __main.exception.InvalidParameterException;
import combat_object.combatant.info.AbilityModifier;
import input.Key;
import lombok.experimental.*;
import util.TxtReader;

import java.util.EnumMap;

import static input.Key.*;

@SuperBuilder
public class Weapon extends Implement {

    public static Weapon from(EnumMap<Key, Object> params) {
        params.forEach((key, value) -> {
            if (!key.isValid(value)) throw new InvalidParameterException("Weapon", key, value);
        });

        String name = (String) params.get(NAME);

        return Weapon.builder()
                .name(name)
                .numDice(TxtReader.getNumDice((String) params.get(DMG)))
                .dieSize(TxtReader.getDieSize((String) params.get(DMG)))
                .stat((AbilityModifier) params.get(STAT))
                .isManual(name != null && name.startsWith("Manual"))
                .build();
    }

    public static Weapon createManual(String name) {
        return Weapon.builder()
                .name(name)
                .numDice(1)
                .dieSize(100)
                .stat(AbilityModifier.OPTION)
                .isManual(true)
                .build();
    }
}