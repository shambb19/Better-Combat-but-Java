package combat_object.damage_implements;

import _global_list.DamageImplements;
import combat_object.CombatObject;
import combat_object.combatant.AbilityModifier;
import txt_input.Key;
import util.TxtReader;

import java.util.EnumMap;

import static txt_input.Key.*;

public class Weapon extends Implement implements CombatObject {

    public Weapon(String name, int numDice, int dieSize, AbilityModifier stat) {
        super(name, numDice, dieSize, stat, DamageImplements.isManual(name));
    }

    public static Weapon from(EnumMap<Key, Object> params) {
        return new Weapon(
                (String) params.get(NAME),
                TxtReader.getNumDice((String) params.get(DMG)),
                TxtReader.getDieSize((String) params.get(DMG)),
                (AbilityModifier) params.get(STAT)
        );
    }

}
