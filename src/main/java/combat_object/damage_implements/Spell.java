package combat_object.damage_implements;

import combat_object.combatant.info.AbilityModifier;
import input.Key;
import lombok.*;
import util.TxtReader;

import java.util.EnumMap;
import java.util.Objects;

import static input.Key.*;

@Getter
public class Spell extends Implement implements combat_object.CombatObject {

    private final Effect effect;

    public Spell(String name, int numDice, int dieSize, AbilityModifier savingThrow, Effect effect) {
        super(name, numDice, dieSize, savingThrow, name.startsWith("Manual"));

        this.effect = Objects.requireNonNullElse(effect, Effect.NONE);
    }

    @Override public boolean effectEquals(Effect o) {
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

    public static class ManualSpell extends Spell {
        public ManualSpell(String name) {
            super(name, 1, 100, null, Effect.NONE);
        }
    }

}
