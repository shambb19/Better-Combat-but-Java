package txt_input_2;

import character_info.AbilityModifier;
import character_info.Class5e;
import character_info.Stats;
import character_info.combatant.Combatant;
import character_info.combatant.NPC;
import character_info.combatant.PC;
import damage_implements.Effect;
import damage_implements.Spell;
import damage_implements.Weapon;
import scenario_info.Scenario;
import util.Locators;

import java.util.*;

import static util.TxtReader.*;

public class Decoder {

    public static PC pc(ArrayList<String> params) {
        params.removeFirst();

        String name = "name";
        int hp = 0, hpCur = 0, ac = 0, level = 0;
        Class5e class5e = null;
        Stats stats = null;
        ArrayList<Weapon> weapons = new ArrayList<>();
        ArrayList<Spell> spells = new ArrayList<>();

        while (!params.isEmpty()) {
            String line = params.removeFirst();
            String key = key(line);
            String value = value(line);

            switch (key) {
                case "name" -> name = value;
                case "hp" -> {
                    hp = getHp(value);
                    hpCur = getHpCur(value);
                }
                case "ac" -> ac = Integer.parseInt(value);
                case "level" -> level = Integer.parseInt(value);
                case "class" -> class5e = Class5e.withName(value);
                case "stats" -> {
                    stats = new Stats(class5e, level);
                    stats.put(line);
                }
                case "weapons" -> weapons = damageImplements(value, Weapon.class);
                case "spells" -> spells = damageImplements(value, Spell.class);
            }
        }

        PC pc = new PC(name, hp, ac, stats, weapons, spells);
        pc.setHealth(hpCur);
        return pc;
    }

    public static NPC npc(ArrayList<String> params, boolean isEnemyTeam) {
        params.removeFirst();

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

    public static Scenario scenario(ArrayList<String> params, ArrayList<Object> readItems) {
        params.removeFirst();

        List<NPC> readFriendlies = readItems.stream()
                .filter(NPC.class::isInstance)
                .map(NPC.class::cast)
                .filter(NPC::isAlly)
                .toList();

        List<NPC> readEnemies = readItems.stream()
                .filter(NPC.class::isInstance)
                .map(NPC.class::cast)
                .filter(NPC::isEnemy)
                .toList();

        String name = "name";
        HashMap<Combatant, Integer> with = new HashMap<>();
        HashMap<Combatant, Integer> against = new HashMap<>();

        while (!params.isEmpty()) {
            String key = key(params.getFirst());
            String value = value(params.removeFirst());

            switch (key) {
                case "name" -> name = value;
                case "with" -> with = getCombatantsFromString(value, readFriendlies);
                case "against" -> against = getCombatantsFromString(value, readEnemies);
            }
        }

        return new Scenario(name, with, against);
    }

    public static Weapon weapon(ArrayList<String> params) {
        params.removeFirst();

        String name = "name";
        int numDice = 0, dieSize = 0;
        AbilityModifier stat = null;

        while (!params.isEmpty()) {
            String key = key(params.getFirst());
            String value = value(params.removeFirst());

            switch (key) {
                case "name" -> name = value;
                case "dmg" -> {
                    numDice = getNumDice(value);
                    dieSize = getDieSize(value);
                }
                case "stat" -> stat = AbilityModifier.get(value);
            }
        }

        return new Weapon(name, numDice, dieSize, stat);
    }

    public static Spell spell(ArrayList<String> params) {
        params.removeFirst();

        String name = "name";
        int numDice = 0, dieSize = 0;
        AbilityModifier save = null;
        Effect effect = null;

        while (!params.isEmpty()) {
            String key = key(params.getFirst());
            String value = value(params.removeFirst());

            switch (key) {
                case "name" -> name = value;
                case "dmg" -> {
                    numDice = getNumDice(value);
                    dieSize = getDieSize(value);
                }
                case "save" -> save = AbilityModifier.get(value);
                case "effect" -> effect = Effect.withRawName(value);
            }
        }

        return new Spell(name, numDice, dieSize, save, effect);
    }

    @SuppressWarnings("unchecked")
    private static <T> ArrayList<T> damageImplements(String value, Class<T> type) {
        ArrayList<T> list = new ArrayList<>();
        String[] arr = stripped(value).split(", ");

        for (String name : arr) {
            if (type.isAssignableFrom(Weapon.class)) {
                list.add((T) Weapon.get(name));
            } else if (type.isAssignableFrom(Spell.class)) {
                list.add((T) Spell.get(name));
            }
        }

        list.removeIf(Objects::isNull);
        return list;
    }

    private static HashMap<Combatant, Integer> getCombatantsFromString(String list, List<NPC> source) {
        String[] names = list.split(",");
        HashMap<Combatant, Integer> combatants = new HashMap<>();

        for (String str : names) {
            String name = getName(str);
            int qty = getQty(str);

            Combatant selected = Locators.getNpcWithNameFrom(source, name);

            assert selected != null;
            combatants.put(selected, qty);
        }

        return combatants;
    }

}
