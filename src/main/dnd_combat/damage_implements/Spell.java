package damage_implements;

import character_info.Stat;

import java.util.ArrayList;
import java.util.Comparator;

import static damage_implements.Spells.MANUAL_HIT;
import static damage_implements.Spells.MANUAL_SAVE;

public record Spell(String name, int numDice, int dieSize, Stat savingThrow, Effect effect) {

    public String getDamageString() {
        return numDice + "d" + dieSize;
    }

    public boolean isManual() {
        return equals(MANUAL_SAVE) || equals(MANUAL_HIT);
    }

    public boolean hasSave() {
        return savingThrow != null;
    }

    public boolean dealsHalfDamageAnyways() {
        if (effect == null) {
            return false;
        }
        return effect.equals(Effect.HALF_DAMAGE);
    }

    public static Spell get(String nameString) {
        for (Spell spell : Spells.get()) {
            if (nameString.equals(spell.name)) {
                return spell;
            }
        }
        return null;
    }

    public static ArrayList<Object> getAllAsList() {
        ArrayList<Spell> list = Spells.get();
        list.sort(Comparator.comparing(spell -> spell.name));
        list.remove(MANUAL_HIT);
        list.remove(MANUAL_SAVE);
        return new ArrayList<>(list);
    }

    @Override
    public String toString() {
        return name;
    }

}
