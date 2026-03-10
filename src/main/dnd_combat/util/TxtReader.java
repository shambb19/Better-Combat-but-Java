package util;

import character_info.Combatant;
import character_info.Stats;
import damage_implements.Effect;
import damage_implements.Spell;
import damage_implements.Weapon;

import java.util.ArrayList;

public class TxtReader {

    /**
     * @param params the lines of .txt code presented (i.e. name=Frodo or ac=12)
     * @return A completed Combatant object based on the provided parameters
     */
    public static Combatant decodeNPC(ArrayList<String> params, boolean isEnemyTeam) {
        String name = "name";
        int hp = 20, ac = 10;
        while (!params.isEmpty()) {
            String key = identifier(params.getFirst());
            String value = withoutIdentifier(params.removeFirst());
            switch (key) {
                case "name" -> name = value;
                case "hp" -> hp = Integer.parseInt(value);
                case "ac" -> ac = Integer.parseInt(value);
            }
        }
        return new Combatant(name, hp, ac, isEnemyTeam);
    }

    /**
     * @param params the lines of .txt code presented (i.e. name=Fireball or effect=BONUS_DAMAGE)
     * @return a completed Spell object based on the given parameters
     */
    public static Spell decodeSpell(ArrayList<String> params) {
        String name = "name";
        int numDice = 0, dieSize = 0;
        Stats.stat saveThrow = null;
        Effect effect = null;

        while (!params.isEmpty()) {
            String key = identifier(params.getFirst());
            String value = withoutIdentifier(params.removeFirst());

            switch (key) {
                case "name" -> name = value;
                case "dmg" -> {
                    numDice = getNumDice(value);
                    dieSize = getDieSize(value);
                }
                case "numDice" -> numDice = Integer.parseInt(value);
                case "dieSize" -> dieSize = Integer.parseInt(value);
                case "save" -> saveThrow = mod(value);
                case "effect" -> effect = Effect.withRawName(value);
            }
        }

        return new Spell(name, numDice, dieSize, saveThrow, effect);
    }

    /**
     * @param params the lines of .txt code presented (i.e. name=Crossbow or dieSize=10)
     * @return a completed Weapon object based on the given parameters
     */
    public static Weapon decodeWeapon(ArrayList<String> params) {
        String name = "name";
        int numDice = 0, dieSize = 0;
        Stats.stat stat = null;

        while (!params.isEmpty()) {
            String key = identifier(params.getFirst());
            String value = withoutIdentifier(params.removeFirst());

            switch (key) {
                case "name" -> name = value;
                case "dmg" -> {
                    numDice = getNumDice(value);
                    dieSize = getDieSize(value);
                }
                case "numDice" -> numDice = Integer.parseInt(value);
                case "dieSize" -> dieSize = Integer.parseInt(value);
                case "stat" -> stat = mod(value);
            }
        }

        return new Weapon(name, numDice, dieSize, stat);
    }

    /**
     * @param line any line of .txt code (name=Frodo, level=6, effect=NONE, etc.)
     * @return only the identifier for that line without the '=' (for example,
     * "name=Frodo" would return "Frodo")
     */
    public static String identifier(String line) {
        return line.substring(0, line.indexOf("="));
    }

    /**
     * @param line any line of .txt code (name=Frodo, level=6, effect=NONE, etc.)
     * @return only the value for that line (for example, "name=Frodo" would return "Frodo")
     */
    public static String withoutIdentifier(String line) {
        return line.substring(line.indexOf("=") + 1);
    }

    /**
     * @param key the three-letter abbreviation for a DnD main stat
     * @return the Stats.stat enum value associated with that abbreviation (for example, "str"
     * would return the Strength enum value)
     */
    public static Stats.stat mod(String key) {
        return switch (key.toLowerCase()) {
            case "str" -> Stats.stat.STR;
            case "dex" -> Stats.stat.DEX;
            case "con" -> Stats.stat.CON;
            case "int" -> Stats.stat.INT;
            case "wis" -> Stats.stat.WIS;
            case "cha" -> Stats.stat.CHA;
            default -> null;
        };
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

}
