package util;

import lombok.experimental.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@ExtensionMethod(StringUtils.class)
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
        if (line.indexOf(": ") != line.lastIndexOf(": "))
            return line.substring(line.indexOf(": ") + 2).trim();
        else
            return line.split(": ")[1].trim();
    }

    /**
     * @param line A line of .txt code with the precondition that the value for its
     *             key is a list (weapons, spells, stats, scenarios, etc.)
     * @return A list of the elements of the line (with regex ", ") and with brackets
     * removed (for example, "[Frodo, Samwise, Aragorn]" returns
     * ["Frodo", "Samwise", "Aragorn"])
     */
    public static String[] listTextAsArray(String line) {
        String str;
        if (!line.contains("["))
            str = line;
        else
            str = line.substring(line.indexOf("[") + 1, line.indexOf("]"));

        return withoutComments(str).split(", ");
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
        return fullString.split("d")[0].toInt();
    }

    /**
     * @param fullString A damage amount in the traditional DnD notation (i.e. 1d6, 2d8)
     * @return the die size from that damage roll as int (for example, "1d6" returns 6)
     */
    public static int getDieSize(String fullString) {
        return fullString.split("d")[1].toInt();
    }

    /**
     * @param fullString A hp value in traditional hp notation cur/max (i.e. 6/10)
     * @return the maximum hp value as int (for example, "6/10" returns 10)
     */
    public static int getHp(String fullString) {
        if (!fullString.contains("/"))
            return fullString.toInt();
        else
            return fullString.split("/")[1].toInt();
    }

    /**
     * @param fullString A hp value in traditional hp notation cur/max (i.e. 6/10)
     * @return the current hp value as int (for example, "6/10" returns 6)
     */
    public static int getHpCur(String fullString) {
        if (!fullString.contains("/"))
            return fullString.toInt();
        else
            return fullString.split("/")[0].toInt();
    }

    /**
     * @param fullString A name and optional quantity value (i.e. "Orc" or "Orc_6")
     * @return the name of the combatant (for example, "Orc_6" returns "Orc")
     */
    public static String getName(String fullString) {
        if (!fullString.contains("_"))
            return fullString;
        else
            return fullString.substring(0, fullString.indexOf("_"));
    }

    /**
     * @param fullString A name and optional quantity value (i.e. "Orc" or "Orc_6)
     * @return the quantity of the combatant, and 1 if none is specified
     * (for example, "Orc_6" returns 6 and "Orc" returns 1)
     */
    public static int getQty(String fullString) {
        if (!fullString.contains("_")) return 1;

        String num = fullString.substring(fullString.indexOf("_") + 1);
        return num.toInt();
    }

}
