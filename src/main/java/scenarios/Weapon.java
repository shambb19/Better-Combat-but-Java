package scenarios;

import java.util.EnumMap;

public enum Weapon {

    DAGGER("Dagger", 1, 4, null);

    private final String name;
    private final int numDamageDice;
    private final int dieSize;
    private final String savingThrow;

    Weapon(String name, int numDamageDice, int dieSize, String savingThrow) {
        this.name = name;
        this.numDamageDice = numDamageDice;
        this.dieSize = dieSize;
        this.savingThrow = savingThrow;
    }

    public String getDamageString() {
        return numDamageDice + "d" + dieSize;
    }

}
