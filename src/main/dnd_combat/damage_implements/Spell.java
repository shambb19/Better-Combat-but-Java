package damage_implements;

import character_info.AbilityModifier;

import static damage_implements.DamageImplements.MANUAL_HIT;
import static damage_implements.DamageImplements.MANUAL_SAVE;

public class Spell extends Implement {

    private final Effect effect;

    public Spell(String name, int numDice, int dieSize, AbilityModifier savingThrow, Effect effect) {
        super(name, numDice, dieSize, savingThrow);

        if (effect == null) {
            effect = Effect.NONE;
        }
        this.effect = effect;
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
