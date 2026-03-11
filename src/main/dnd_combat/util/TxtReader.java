package util;

import character_info.combatant.Combatant;
import character_info.combatant.NPC;
import character_info.Stat;
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
            String key = key(params.getFirst());
            String value = value(params.removeFirst());
            switch (key) {
                case "name" -> name = value;
                case "hp" -> hp = getHp(value);
                case "ac" -> ac = Integer.parseInt(value);
            }
        }
        return new NPC(name, hp, ac, isEnemyTeam);
    }

    /**
     * @param params the separated parameters of the weapon object
     *               (for example: {"Fireball", "8d6", "dex", "HALF_DAMAGE"}) to be decoded.
     * @return a completed Spell object based on the given parameters
     */
    public static Spell decodeSpell(String[] params) {
        return new Spell(
                params[0],
                getNumDice(params[1]),
                getDieSize(params[1]),
                Stat.get(params[2]),
                Effect.withRawName(params[3])
        );
    }

    /**
     * @param line the line of .txt code presented (i.e. "Fireball,8d6,dex,HALF_DAMAGE")
     * @return a completed Spell object based on the given parameters
     */
    public static Spell decodeSpell(String line) {
        String[] params = line.split(",");
        return decodeSpell(params);
    }

    /**
     * @param params the separated parameters of the weapon object
     *               (for example: {"Dagger", "1d4", "both"}) to be decoded.
     * @return a completed Weapon object based on the given parameters
     */
    public static Weapon decodeWeapon(String[] params) {
        return new Weapon(
            params[0],
            getNumDice(params[1]),
            getDieSize(params[1]),
            Stat.get(params[2])
        );
    }

    /**
     * @param line the line of .txt code presented (i.e. "Dagger,1d4,both")
     * @return a completed Weapon object based on the given parameters
     */
    public static Weapon decodeWeapon(String line) {
        String[] params = line.split(",");
        return decodeWeapon(params);
    }

    /**
     * @param line any line of .txt code (name=Frodo, level=6, effect=NONE, etc.)
     * @return only the identifier for that line without the '=' (for example,
     * "name=Frodo" would return "Frodo")
     */
    public static String key(String line) {
        return line.split(" <= ")[0];
    }

    /**
     * @param line any line of .txt code (name=Frodo, level=6, effect=NONE, etc.)
     * @return only the value for that line (for example, "name=Frodo" would return "Frodo")
     */
    public static String value(String line) {
        return line.split(" <= ")[1];
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
