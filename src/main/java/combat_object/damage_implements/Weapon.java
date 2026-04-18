package combat_object.damage_implements;

import combat_object.combatant.info.AbilityModifier;
import input.Key;
import util.TxtReader;

import java.util.EnumMap;

import static input.Key.*;

public class Weapon extends Implement implements combat_object.CombatObject {

    public Weapon(String name, int numDice, int dieSize, AbilityModifier stat) {
        // this is a disgusting solution to the manual problem, but it works?
        super(name, numDice, dieSize, stat, name.startsWith("Manual"));
    }

    public static Weapon from(EnumMap<Key, Object> params) {
        return new Weapon(
                (String) params.get(NAME),
                TxtReader.getNumDice((String) params.get(DMG)),
                TxtReader.getDieSize((String) params.get(DMG)),
                (AbilityModifier) params.get(STAT)
        );
    }

    public static class ManualWeapon extends Weapon {
        public ManualWeapon(String name) {
            super(name, 1, 100, AbilityModifier.OPTION);
        }
    }
}
