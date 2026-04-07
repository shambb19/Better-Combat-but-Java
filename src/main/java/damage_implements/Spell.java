package damage_implements;

import character_info.AbilityModifier;
import txt_input.Key;
import util.TxtReader;

import java.util.EnumMap;
import java.util.Objects;

import static _global_list.DamageImplements.MANUAL_HIT;
import static _global_list.DamageImplements.MANUAL_SAVE;
import static txt_input.Key.*;

public class Spell extends Implement {

    private final Effect effect;

    public Spell(String name, int numDice, int dieSize, AbilityModifier savingThrow, Effect effect) {
        super(name, numDice, dieSize, savingThrow);

        this.effect = Objects.requireNonNullElse(effect, Effect.NONE);
    }

    public Spell(EnumMap<Key, Object> values) {
        super(
                (String) values.get(NAME),
                TxtReader.getNumDice((String) values.get(DMG)),
                TxtReader.getDieSize((String) values.get(DMG)),
                (AbilityModifier) values.get(STAT)
        );

        Effect effectTemp = (Effect) values.get(EFFECT);

        this.effect = Objects.requireNonNullElse(effectTemp, Effect.NONE);
    }

    public boolean isManual() {
        return equals(MANUAL_SAVE) || equals(MANUAL_HIT);
    }

    public boolean hasSave() {
        return stat != null;
    }

    public boolean dealsHalfDamageAnyways() {
        return effect.equals(Effect.HALF_DAMAGE);
    }

    public Effect effect() {
        return effect;
    }

}
