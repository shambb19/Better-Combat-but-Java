package util;

import org.jetbrains.annotations.NotNull;
import lombok.experimental.ExtensionMethod;

@ExtensionMethod(input.TextReader.class)
public record Roll(int numDice, int dieSize) {

    /**
     * Returns the Roll object decoded from the given String
     * @param rollString the standard DnD roll String (i.e. 1d20, 3d6)
     * @return the new Roll object
     */
    public static Roll ofString(String rollString) {
        return new Roll(
                rollString.getNumDice(),
                rollString.getDieSize()
        );
    }

    /**
     * Returns a hollow object to allow any die roll
     * @return a Roll object representing 1d100
     */
    public static Roll implementDefault() {
        return new Roll(1, 100);
    }

    /**
     * Returns a Roll object representing one roll of the given die
     * @param num the size of the die
     * @return the new Roll object
     */
    public static Roll d(int num) {
        return new Roll(1, num);
    }

    /**
     * Returns a roll object representing a d20 roll
     * @return the new Roll object
     */
    public static Roll d20() {
        return new Roll(1, 20);
    }

    @NotNull @Override
    public String toString() {
        return numDice + "d" + dieSize;
    }
}