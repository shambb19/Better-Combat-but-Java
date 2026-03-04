package damage_implements;

import character_info.Stats;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

public enum Weapon {

    DAGGER("Dagger", "dagger", 1, 4, Stats.stat.DEX),
    CROSSBOW_LIGHT("Light Crossbow", "crossbow_light", 1, 10, Stats.stat.DEX),

    LONGSWORD("Longsword", "longsword", 1, 8, Stats.stat.STR),
    QUARTERSTAFF("Quarterstaff", "quarterstaff", 1, 6, Stats.stat.STR),
    JAVELIN("Javelin", "javelin", 1, 6, Stats.stat.DEX),
    LANCE("Lance", "lance", 1, 10, Stats.stat.STR),

    MANUAL("Manual Entry", null, -1, -1, null);
    // longsword, quarterstaff, javelin, lance

    private final String name;
    private final String nameRoot;
    private final int numDamageDice;
    private final int dieSize;
    private final Stats.stat damageType;

    Weapon(String name, String nameRoot, int numDamageDice, int dieSize, Stats.stat damageType) {
        this.name = name;
        this.nameRoot = nameRoot;
        this.numDamageDice = numDamageDice;
        this.dieSize = dieSize;
        this.damageType = damageType;
    }

    public String getName() {
        return name;
    }

    public String getNameRoot() {
        return nameRoot;
    }

    public String getDamageString() {
        return numDamageDice + "d" + dieSize;
    }

    public int getNumDice() {
        return numDamageDice;
    }

    public int getDieSize() {
        return dieSize;
    }

    public Stats.stat getMod() {
        return damageType;
    }

    public boolean isManual() {
        return equals(Weapon.MANUAL);
    }

    public static Weapon get(String name) {
        for (Weapon weapon : values()) {
            if (weapon.getName().equalsIgnoreCase(name)) {
                return weapon;
            }
        }
        return null;
    }

    public static ArrayList<Object> getAllAsList() {
        ArrayList<Weapon> list = new ArrayList<>(Arrays.stream(values()).toList());
        list.sort(Comparator.comparing(weapon -> weapon.name));
        list.remove(MANUAL);
        return new ArrayList<>(list);
    }

    @Override
    public String toString() {
        return name;
    }

}