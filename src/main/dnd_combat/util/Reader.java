package util;

import character_info.Stats;

public class Reader {

    public static String identifier(String line) {
        return line.substring(0, line.indexOf("="));
    }

    public static String withoutIdentifier(String line) {
        return line.substring(line.indexOf("=") + 1);
    }

    public static Stats.stat mod(String key) {
        return switch (key) {
            case "str" -> Stats.stat.STR;
            case "dex" -> Stats.stat.DEX;
            case "con" -> Stats.stat.CON;
            case "int" -> Stats.stat.INT;
            case "wis" -> Stats.stat.WIS;
            case "cha" -> Stats.stat.CHA;
            default -> null;
        };
    }

    public static int getNumDice(String fullString) {
        return Integer.parseInt(fullString.split("d")[0]);
    }

    public static int getDieSize(String fullString) {
        return Integer.parseInt(fullString.split("d")[1]);
    }

}
