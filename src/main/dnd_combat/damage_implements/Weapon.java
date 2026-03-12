package damage_implements;

import character_info.AbilityModifier;

import java.util.ArrayList;
import java.util.Comparator;

import static damage_implements.Weapons.MANUAL;

public record Weapon(String name, int numDice, int dieSize, AbilityModifier stat) {

    public String getDamageString() {
        return numDice + "d" + dieSize;
    }

    public boolean isManual() {
        return equals(MANUAL) || name.equals(MANUAL.name);
    }

    public static Weapon get(String name) {
        for (Weapon weapon : Weapons.get()) {
            if (weapon.name.equalsIgnoreCase(name)) {
                return weapon;
            }
        }
        return null;
    }

    public static ArrayList<Object> getAllAsList() {
        ArrayList<Weapon> list = Weapons.get();
        list.sort(Comparator.comparing(weapon -> weapon.name));
        list.remove(MANUAL);
        return new ArrayList<>(list);
    }

    @Override
    public String toString() {
        return name;
    }

}
