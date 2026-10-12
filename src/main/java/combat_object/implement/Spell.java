package combat_object.implement;

import combat_object.combatant.info.AbilityModifier;
import input.TextReader;
import input.syntax.Key;
import lombok.*;
import lombok.experimental.*;
import util.Roll;

import java.util.EnumMap;
import java.util.Objects;

import static input.syntax.Key.*;

@Getter @SuperBuilder
@ExtensionMethod(TextReader.class)
@FieldDefaults(makeFinal = true)
public class Spell extends Implement {

    public static Spell HIT_MANUAL = createManual("Manual Hit");
    public static Spell SAVE_MANUAL = createManual("Manual Save");
    public static Spell HEADER = createManual("── Spell ──");

    private final Effect effect;
    private final boolean concentrationRequired;

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
        validateAll(params, "Spell");

        String name = (String) params.get(NAME);
        return Spell.builder()
                .name(name)
                .roll(Roll.ofString((String) params.get(DMG)))
                .stat((AbilityModifier) params.get(STAT))
                .effect((Effect) params.get(EFFECT))
                .concentrationRequired(params.get(CONCENTRATION).booleanFromOptionalPresence())
                .isManual(name != null && name.startsWith("Manual"))
                .build();
    }

    public static Spell createManual(String name) {
        return Spell.builder()
                .name(name)
                .roll(Roll.implementDefault())
                .stat(null)
                .effect(Effect.NONE)
                .isManual(true)
                .build();
    }
}