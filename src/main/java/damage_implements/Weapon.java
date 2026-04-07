package damage_implements;

import character_info.AbilityModifier;
import txt_input.Key;
import util.TxtReader;

import java.util.EnumMap;

import static _global_list.DamageImplements.MANUAL_WEAPON;
import static txt_input.Key.*;

public class Weapon extends Implement {

    public Weapon(String name, int numDice, int dieSize, AbilityModifier stat) {
        super(name, numDice, dieSize, stat);
    }

    public Weapon(EnumMap<Key, Object> values) {
        super(
                (String) values.get(NAME),
                TxtReader.getNumDice((String) values.get(DMG)),
                TxtReader.getDieSize((String) values.get(DMG)),
                (AbilityModifier) values.get(STAT)
        );
    }

    public boolean isManual() {
        return equals(MANUAL_WEAPON);
    }

}
