package util;

import lombok.experimental.*;

import java.util.stream.Stream;

@ExtensionMethod(StringUtils.class)
public class TxtReader {

    /**
     * Returns "key" from any "key: value" code line
     */
    public static String key(String line) {
        if (!line.contains(": ")) return line;
        return line.substring(0, line.indexOf(": "));
    }

    /**
     * Returns "value" from any "key: value" code line
     */
    public static String value(String line) {
        return line.substring(line.indexOf(": ") + 2);
    }

    /**
     * Returns the values of code value array as String[]
     * <p>
     * i.e. "key: [a, b, c, d]"
     * <p>
     * returns
     * <p>
     * {"a", "b", "c", "d"}
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
        int firstCommentIndex = Stream.of("//", "~", "#")
                .filter(line::contains).map(line::indexOf).sorted().findFirst().orElse(line.length());

        return line.substring(0, firstCommentIndex).trim();
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
