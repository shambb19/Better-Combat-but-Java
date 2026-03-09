package util;

import character_info.Combatant;
import character_info.Stats;
import damage_implements.Effect;
import damage_implements.Spell;
import damage_implements.Weapon;

import java.util.ArrayList;

public class Reader {

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
