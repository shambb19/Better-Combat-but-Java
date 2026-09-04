package util;

import input.TextReader;
import org.jetbrains.annotations.NotNull;

public record Roll(int numDice, int dieSize) {

    public static Roll ofString(String rollString) {
        int numDice = TextReader.getNumDice(rollString);
        int dieSize = TextReader.getDieSize(rollString);
        return new Roll(numDice, dieSize);
    }

    public static Roll implementDefault() {
        return new Roll(1, 100);
    }

    public static Roll d(int num) {
        return new Roll(1, num);
    }

    public static Roll d20() {
        return new Roll(1, 20);
    }

    @NotNull @Override
    public String toString() {
        return numDice + "d" + dieSize;
    }
}