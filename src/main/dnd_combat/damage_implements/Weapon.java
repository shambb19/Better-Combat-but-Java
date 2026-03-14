package damage_implements;

import character_info.AbilityModifier;

import static damage_implements.DamageImplements.MANUAL_WEAPON;

public class Weapon extends Implement {

    public Weapon(String name, int numDice, int dieSize, AbilityModifier stat) {
        super(name, numDice, dieSize, stat);
    }

    public boolean isManual() {
        return equals(MANUAL_WEAPON);
    }

}
