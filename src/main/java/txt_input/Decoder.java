package txt_input;

import _global_list.Combatants;
import _global_list.DamageImplements;
import character_info.Class5e;
import character_info.Stats;
import character_info.combatant.Combatant;
import character_info.combatant.NPC;
import character_info.combatant.PC;
import damage_implements.Implement;
import damage_implements.Spell;
import damage_implements.Weapon;
import encounter_info.Scenario;
import util.Filter;
import util.Locators;

import java.util.*;
import java.util.stream.Collectors;

import static txt_input.Key.*;
import static util.TxtReader.*;

public class Decoder {

    public static PC pc(List<String> params) {
        EnumMap<Key, Object> map = toMap(params);

        int level = (int) map.get(LEVEL);
        Class5e class5e = (Class5e) map.get(CLASS);

        Stats stats = new Stats(class5e, level);
        stats.put((String) map.get(STATS));

        map.put(STATS, stats);

        return new PC(map);
    }

    public static NPC npc(List<String> params) {
        return npc(params, false);
    }

    public static NPC enemy(List<String> params) {
        return npc(params, true);
    }

    private static NPC npc(List<String> params, boolean isEnemyTeam) {
        EnumMap<Key, Object> map = toMap(params);
        return new NPC(map, isEnemyTeam);
    }

    public static Scenario scenario(List<String> params) {
        EnumMap<Key, Object> map = toMap(params);

        String name = (String) map.get(NAME);

        var npcsByAllegiance = Filter.matchingClass(Combatants.toList(), NPC.class).stream()
                .collect(Collectors.partitioningBy(NPC::isAlly));

        List<NPC> allies = npcsByAllegiance.get(true);
        List<NPC> enemies = npcsByAllegiance.get(false);

        var with = getCombatantsFromString((String) map.get(WITH), allies);
        var against = getCombatantsFromString((String) map.get(AGAINST), enemies);

        return new Scenario(name, with, against);
    }

    public static Weapon weapon(List<String> params) {
        return implement(params, Weapon.class);
    }

    public static Spell spell(List<String> params) {
        return implement(params, Spell.class);
    }

    @SuppressWarnings("unchecked")
    private static <T> T implement(List<String> params, Class<T> type) {
        EnumMap<Key, Object> map = toMap(params);

        if (type.isAssignableFrom(Weapon.class))
            return (T) new Weapon(map);
        else if (type.isAssignableFrom(Spell.class))
            return (T) new Spell(map);

        return null;
    }

    static EnumMap<Key, Object> toMap(List<String> params) {

        EnumMap<Key, Object> map = new EnumMap<>(Key.class);

        params.stream()
                .skip(1)
                .forEach(param -> {
                    Key key = Key.get(param);

                    if (key != null) {
                        map.put(key, Key.value(param));
                    }
                });

        return map;
    }

    public static List<Weapon> weapons(String value) {
        return damageImplements(value, Weapon.class);
    }

    public static List<Spell> spells(String value) {
        return damageImplements(value, Spell.class);
    }

    public static <T extends Implement> List<T> damageImplements(String value, Class<T> type) {
        if (value == null) return new ArrayList<>();

        ArrayList<T> list = new ArrayList<>();
        String[] arr = listTextAsArray(value);

        for (String name : arr) {
            T obj = DamageImplements.get(name, type);

            if (obj != null)
                list.add(obj);
        }

        list.removeIf(Objects::isNull);
        return list;
    }

    private static HashMap<Combatant, Integer> getCombatantsFromString(String list, List<NPC> source) {
        HashMap<Combatant, Integer> combatants = new HashMap<>();

        if (list == null) return combatants;

        String[] names = listTextAsArray(list);

        for (String str : names) {
            String name = getName(str);
            int qty = getQty(str);

            Combatant selected = Locators.getWithNameFromDirectory(source, name);

            assert selected != null;
            combatants.put(selected, qty);
        }

        return combatants;
    }

}