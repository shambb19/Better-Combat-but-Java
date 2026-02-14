package damage;

import combatants.Stats;

public enum Weapon {

    DAGGER("Dagger", 1, 4, Stats.stat.DEX),
    CROSSBOW("Crossbow", 1, 10, Stats.stat.DEX),

    MANUAL("Manual Entry", -1, -1, null);

    private final String name;
    private final int numDamageDice;
    private final int dieSize;
    private final Stats.stat damageType;

    Weapon(String name, int numDamageDice, int dieSize, Stats.stat damageType) {
        this.name = name;
        this.numDamageDice = numDamageDice;
        this.dieSize = dieSize;
        this.damageType = damageType;
    }

    public String getName() {
        return name;
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

    @Override
    public String toString() {
        return name;
    }

}