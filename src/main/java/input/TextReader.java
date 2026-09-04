package input;

import config.Config;
import config.ruleset.Ruleset;
import exception.InvalidSyntaxError;
import input.syntax.Tag;
import lombok.experimental.*;
import util.Locators;

import java.util.*;
import java.util.function.Consumer;

@ExtensionMethod(util.StringUtil.class)
public class TextReader {

    private static final String[] COMMENT_INITIALIZERS = {"//", "~", "#"};

    /**
     * Returns "key" from any "key: value" code line
     */
    public static String key(String line) {
        if (line.startsWith("+")) return line.substring(1).trim();

        if (!line.contains(":")) throw new InvalidSyntaxError(false, "no delimiter found in " + line);

        int startIndex = line.startsWith("+") ? 1 : 0;
        return line.substring(startIndex, line.indexOf(":"));
    }

    /**
     * Returns "value" from any "key: value" code line
     */
    public static String value(String line) {
        if (line.startsWith("+")) return "true";

        if (!line.contains(":")) throw new InvalidSyntaxError(false, "no delimiter found");

        int valueStartIdx = line.contains(": ") ? 2 : 1;
        return line.substring(line.indexOf(":") + valueStartIdx);
    }

    /**
     * Returns the values of code value array as String[]
     * <p>i.e. "key: [a, b, c, d]"<p>returns<p>{"a", "b", "c", "d"}
     */
    public static String[] listTextAsArray(String line) {
        String str;
        if (!line.contains("["))
            str = line;
        else
            str = line.substring(line.indexOf("[") + 1, line.indexOf("]"));

        return withoutComments(str).split(", ");
    }

    public static boolean isComments(String line) {
        return Arrays.stream(COMMENT_INITIALIZERS).anyMatch(line.trim()::startsWith);
    }

    /**
     * @param line Any line of .txt code
     * @return The same line without comments (using the keys "//", "~", and "#")
     */
    public static String withoutComments(String line) {
        int firstCommentIndex = Arrays.stream(COMMENT_INITIALIZERS)
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
     * @param param A hp value in traditional hp notation cur/max (i.e. 6/10)
     * @return the maximum hp value as int (for example, "6/10" returns 10)
     */
    public static int beforeSlash(Object param) {
        if (!(param instanceof String fullString))
            throw new ClassCastException("TxtReader.beforeSlash: String expected");

        if (!fullString.contains("/"))
            return fullString.toInt();
        else
            return fullString.split("/")[1].toInt();
    }

    /**
     * @param param A hp value in traditional hp notation cur/max (i.e. 6/10)
     * @return the current hp value as int (for example, "6/10" returns 6)
     */
    public static int afterSlash(Object param) {
        if (!(param instanceof String fullString))
            throw new ClassCastException("TxtReader.afterSlash: String expected");

        if (!fullString.contains("/"))
            return fullString.toInt();
        else
            return fullString.split("/")[0].toInt();
    }

    public static int getHp(Object param, boolean isMaximum) {
        if (param == null) {
            if (Config.getRuleset().equals(Ruleset.STANDARD_RULESET))
                throw new InvalidSyntaxError(true, "absent hp line");
            return 8;
        }

        if (!(param instanceof String line)) throw new ClassCastException("TxtReader.getHp: String expected");

        if (!line.contains("/")) return line.toInt();

        String[] hpStrings = line.split("/");
        String hp = isMaximum ? hpStrings[1] : hpStrings[0];
        return hp.toInt();
    }

    /**
     * @param fullString A name and optional quantity value (i.e. "Orc" or "Orc_6")
     * @return the name of the combatant (for example, "Orc_6" returns "Orc")
     */
    public static String getNameScenario(String fullString) {
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

    public static boolean booleanFromOptionalPresence(Object value) {
        if (value == null) return false;
        return (boolean) value;
    }

    public static ArrayList<String> extractConfigBlock(List<String> lines, Consumer<ArrayList<String>> onExtracted) {
        ArrayList<String> blockLines = new ArrayList<>();
        ArrayList<String> remaining = new ArrayList<>();
        boolean inBlock = false;

        for (String line : lines) {
            if (line.trim().equals(Config.CONFIG_OPEN_TOKEN)) {
                inBlock = true;
                blockLines.add(line);
            } else if (inBlock) {
                blockLines.add(line);
                if (line.trim().equals(Config.CONFIG_CLOSE_TOKEN))
                    inBlock = false;
            } else {
                remaining.add(line);
            }
        }

        if (!blockLines.isEmpty())
            onExtracted.accept(blockLines);

        return remaining;
    }

    public static String getHeader(String line) {
        int spaceIdx = line.indexOf(" ");
        int chevronIdx = line.indexOf("<");

        int delimiterIdx;
        if (spaceIdx == -1 && chevronIdx == -1) return withoutComments(line);
        else if (spaceIdx == -1) delimiterIdx = chevronIdx;
        else if (chevronIdx == -1) delimiterIdx = spaceIdx;
        else delimiterIdx = Math.min(spaceIdx, chevronIdx);

        return withoutComments(line).substring(0, delimiterIdx);
    }

    public static String getTagString(String line) {
        if (!line.contains("<")) return "";
        int start = line.indexOf("<");
        int end = line.contains(" ") ? line.indexOf(" ") : line.length();
        return line.substring(start, end);
    }

    public static String getName(String line) {
        String eMessage = "Valid name on header line expected";

        int spaceIdx = withoutComments(line).indexOf(" ");
        if (spaceIdx == -1) throw new InvalidSyntaxError(true, eMessage);

        String name = withoutComments(line).substring(spaceIdx).trim();
        if (name.isBlank()) throw new InvalidSyntaxError(true, eMessage);

        return name;
    }

    public static Set<Tag> getTags(String line) {
        String tagString = getTagString(withoutComments(line));
        if (tagString.isBlank()) return Set.of();

        Set<Tag> tags = new HashSet<>();
        for (String part : tagString.split("<")) {
            if (part.isBlank()) continue;
            Tag tag = Locators.enumNameSearch(part, Tag.class);
            if (tag == null) throw new InvalidSyntaxError(true, "Unrecognized tag: " + part);
            tags.add(tag);
        }
        return tags;
    }

}
