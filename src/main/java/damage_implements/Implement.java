package damage_implements;

import character_info.AbilityModifier;
import org.jetbrains.annotations.NotNull;

public abstract class Implement {

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
        if (isManual())
            return "";
        else
            return numDice + "d" + dieSize;
    }

    public int getMaxDamage() {
        return numDice * dieSize;
    }

    public int numDice() {
        return numDice;
    }

    public int dieSize() {
        return dieSize;
    }

    public abstract boolean isManual();

    @Override
    public String toString() {
        return name;
    }

    public String name() {
        return name;
    }

    @NotNull
    public AbilityModifier stat() {
        return stat;
    }

}
