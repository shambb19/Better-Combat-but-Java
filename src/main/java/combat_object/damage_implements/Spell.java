package combat_object.damage_implements;

import __main.exception.InvalidParameterException;
import combat_object.combatant.info.AbilityModifier;
import input.Key;
import lombok.*;
import lombok.experimental.*;
import util.TxtReader;

import java.util.EnumMap;
import java.util.Objects;

import static input.Key.*;

@Getter
@SuperBuilder
public class Spell extends Implement {

    @Builder.Default private final Effect effect = Effect.NONE;
    private final boolean requiresConcentration;

    @Override
    public boolean effectEquals(Effect o) {
        return Objects.equals(this.effect, o);
    }

    public boolean hasSave() {
        return stat != null;
    }

    public boolean dealsHalfDamageAnyways() {
        return effectEquals(Effect.HALF_DAMAGE);
    }

    public boolean doesNotRequireAttackRoll() {
        return effectEquals(Effect.AUTO_HIT);
    }

    public static Spell from(EnumMap<Key, Object> params) {
        params.forEach((key, value) -> {
            if (!key.isValid(value)) throw new InvalidParameterException("Spell", key, value);
        });

        String name = (String) params.get(NAME);

        return Spell.builder()
                .name(name)
                .numDice(TxtReader.getNumDice((String) params.get(DMG)))
                .dieSize(TxtReader.getDieSize((String) params.get(DMG)))
                .stat((AbilityModifier) params.get(STAT))
                .effect((Effect) params.get(EFFECT))
                .requiresConcentration((boolean) params.get(CONCENTRATION))
                .isManual(name != null && name.startsWith("Manual"))
                .build();
    }

    public static Spell createManual(String name) {
        return Spell.builder()
                .name(name)
                .numDice(1)
                .dieSize(100)
                .stat(null)
                .effect(Effect.NONE)
                .isManual(true)
                .build();
    }
}