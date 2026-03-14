package damage_implements;

import character_info.AbilityModifier;

public class Implement {

    protected final String name;
    protected final int numDice;
    protected final int dieSize;
    protected final AbilityModifier stat;

    public Implement(String name, int numDice, int dieSize, AbilityModifier stat) {
        this.name = name;
        this.numDice = numDice;
        this.dieSize = dieSize;
        this.stat = stat;
    }

    public String damageString() {
        if (isManual()) {
            return "";
        }
        return numDice + "d" + dieSize;
    }

    public boolean isManual() {
        return false;
    }

    public boolean isHalfDamage() {
        return false;
    }

    @Override
    public String toString() {
        return name;
    }

    public String name() {
        return name;
    }

    public int numDice() {
        return numDice;
    }

    public int dieSize() {
        return dieSize;
    }

    public AbilityModifier stat() {
        return stat;
    }

}
