package combat_object.combatant.info;

import java.util.Arrays;

public enum AbilityModifier {
    STR, DEX, CON, INT, WIS, CHA, OPTION;

    /**
     * Returns an array of all values in AbilityModifier except OPTION.
     * @return the values array
     */
    public static AbilityModifier[] stats() {
        return Arrays.copyOf(values(), values().length - 1);
    }
}
