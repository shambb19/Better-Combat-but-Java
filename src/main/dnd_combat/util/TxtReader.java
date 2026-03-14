package util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TxtReader {

    /**
     * @param line any line of .txt code (name: Frodo, level: 6, effect: NONE, etc.)
     * @return only the identifier for that line without the '=' (for example,
     * "name: Frodo" would return "Frodo")
     */
    public static String key(String line) {
        return line.split(": ")[0].trim();
    }

    /**
     * @param line any line of .txt code (name: Frodo, level: 6, effect: NONE, etc.)
     * @return only the value for that line (for example, "name: Frodo" would return "Frodo")
     */
    public static String value(String line) {
        if (line.indexOf(": ") != line.lastIndexOf(": ")) {
            return line.substring(line.indexOf(": ") + 2).trim();
        }

        return line.split(": ")[1].trim();
    }

    /**
     * @param line The line of .txt code for weapons, spells, or stats
     * @return The same line without the opening and closing brackets
     */
    public static String stripped(String line) {
        if (!line.contains("[")) {
            return line;
        }
        return withoutComments(line.substring(line.indexOf("[") + 1, line.indexOf("]")));
    }

    /**
     * @param line Any line of .txt code
     * @return The same line without comments (using the keys "//", "~", and "#")
     */
    public static String withoutComments(String line) {
        ArrayList<Integer> commentIndexes = new ArrayList<>(List.of(
                line.indexOf("//"), line.indexOf("~"), line.indexOf("#")
        ));
        commentIndexes.removeIf(index -> index < 0);

        if (!commentIndexes.isEmpty()) {
            int stopIdx = Collections.min(commentIndexes);
            return line.substring(0, stopIdx).trim();
        }

        return line.trim();
    }

    /**
     * @param fullString A damage amount in the traditional DnD notation(i.e. 1d6, 2d8)
     * @return the number of dice from that damage roll as int (for example, "1d6" returns 1)
     */
    public static int getNumDice(String fullString) {
        return Integer.parseInt(fullString.split("d")[0]);
    }

    /**
     * @param fullString A damage amount in the traditional DnD notation (i.e. 1d6, 2d8)
     * @return the die size from that damage roll as int (for example, "1d6" returns 6)
     */
    public static int getDieSize(String fullString) {
        return Integer.parseInt(fullString.split("d")[1]);
    }

    /**
     * @param fullString A hp value in traditional hp notation cur/max (i.e. 6/10)
     * @return the maximum hp value as int (for example, "6/10" returns 10)
     */
    public static int getHp(String fullString) {
        if (!fullString.contains("/")) {
            return Integer.parseInt(fullString);
        }
        return Integer.parseInt(fullString.split("/")[1]);
    }

    /**
     * @param fullString A hp value in traditional hp notation cur/max (i.e. 6/10)
     * @return the current hp value as int (for example, "6/10" returns 6)
     */
    public static int getHpCur(String fullString) {
        if (!fullString.contains("/")) {
            return Integer.parseInt(fullString);
        }
        return Integer.parseInt(fullString.split("/")[0]);
    }

    /**
     * @param fullString A name and optional quantity value (i.e. "Orc" or "Orc_6")
     * @return the name of the combatant (for example, "Orc_6" returns "Orc")
     */
    public static String getName(String fullString) {
        if (!fullString.contains("_")) {
            return fullString;
        }
        return fullString.substring(0, fullString.indexOf("_"));
    }

    /**
     * @param fullString A name and optional quantity value (i.e. "Orc" or "Orc_6)
     * @return the quantity of the combatant, and 1 if none is specified
     * (for example, "Orc_6" returns 6 and "Orc" returns 1)
     */
    public static int getQty(String fullString) {
        if (!fullString.contains("_")) {
            return 1;
        }
        String num = fullString.substring(fullString.indexOf("_") + 1);
        return Integer.parseInt(num);
    }

}
