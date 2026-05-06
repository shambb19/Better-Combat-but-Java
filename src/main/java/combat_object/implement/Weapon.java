package combat_object.implement;

import combat_object.combatant.info.AbilityModifier;
import exception.InvalidParameterException;
import input.Key;
import input.TextReader;
import lombok.experimental.*;

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
                .name((String) params.get(NAME))
                .numDice(TextReader.getNumDice((String) params.get(DMG)))
                .dieSize(TextReader.getDieSize((String) params.get(DMG)))
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