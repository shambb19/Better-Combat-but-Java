package combat_object.damage_implements;

import _global_list.DamageImplements;
import combat_object.CombatObject;
import combat_object.combatant.AbilityModifier;
import lombok.*;
import txt_input.Key;
import util.TxtReader;

import java.util.EnumMap;
import java.util.Objects;

import static txt_input.Key.*;

@Getter
public class Spell extends Implement implements CombatObject {

    private final Effect effect;

    public Spell(String name, int numDice, int dieSize, AbilityModifier savingThrow, Effect effect) {
        super(name, numDice, dieSize, savingThrow, DamageImplements.isManual(name));

        this.effect = Objects.requireNonNullElse(effect, Effect.NONE);
    }

    @Override
    public boolean effectEquals(Effect o) {
        return o.equals(effect);
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
        return new Spell(
                (String) params.get(NAME),
                TxtReader.getNumDice((String) params.get(DMG)),
                TxtReader.getDieSize((String) params.get(DMG)),
                (AbilityModifier) params.get(STAT),
                Objects.requireNonNullElse((Effect) params.get(EFFECT), Effect.NONE)
        );
    }

}
