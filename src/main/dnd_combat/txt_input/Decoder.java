package txt_input;

import _global_list.Combatants;
import _global_list.DamageImplements;
import character_info.AbilityModifier;
import character_info.Class5e;
import character_info.Stats;
import character_info.combatant.Combatant;
import character_info.combatant.NPC;
import character_info.combatant.PC;
import damage_implements.Effect;
import damage_implements.Spell;
import damage_implements.Weapon;
import encounter_info.Scenario;
import util.Locators;

import java.util.*;
import java.util.stream.Collectors;

import static txt_input.Key.*;
import static util.TxtReader.*;

public class Decoder {

    public static PC pc(List<String> params) {
        EnumMap<Key, Object> map = toMap(params);

        String name = (String) map.get(NAME);
        int hp = getHp((String) map.get(HP));
        int hpCur = getHpCur((String) map.get(HP));
        int ac = (int) map.get(AC);
        int level = (int) map.get(LEVEL);
        Class5e class5e = (Class5e) map.get(CLASS);

        Stats stats = new Stats(class5e, level);
        stats.put((String) map.get(STATS));

        List<Weapon> weapons = damageImplements((String) map.get(WEAPONS), Weapon.class);
        List<Spell> spells = damageImplements((String) map.get(SPELLS), Spell.class);

        PC pc = new PC(name, hp, ac, stats, weapons, spells);
        pc.setHealth(hpCur);

        return pc;
    }

    public static NPC npc(List<String> params) {
        return npc(params, false);
    }

    public static NPC enemy(List<String> params) {
        return npc(params, true);
    }

    private static NPC npc(List<String> params, boolean isEnemyTeam) {
        EnumMap<Key, Object> map = toMap(params);

        String name = (String) map.get(NAME);
        int hp = getHp((String) map.get(HP));
        int ac = (int) map.get(AC);

        return new NPC(name, hp, ac, isEnemyTeam);
    }

    public static Scenario scenario(List<String> params) {
        EnumMap<Key, Object> map = toMap(params);

        String name = (String) map.get(NAME);

        var npcsByAllegiance = Combatants.toList().stream()
                .filter(NPC.class::isInstance)
                .map(NPC.class::cast)
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

        String name = (String) map.get(NAME);
        int numDice = getNumDice((String) map.get(DMG));
        int dieSize = getDieSize((String) map.get(DMG));

        AbilityModifier stat = (AbilityModifier) map.get(STAT);
        Effect effect = (Effect) map.get(EFFECT);

        if (type.isAssignableFrom(Weapon.class)) {
            return (T) new Weapon(name, numDice, dieSize, stat);
        } else if (type.isAssignableFrom(Spell.class)) {
            return (T) new Spell(name, numDice, dieSize, stat, effect);
        }
        return null;
    }

    private static EnumMap<Key, Object> toMap(List<String> params) {

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

    private static <T> List<T> damageImplements(String value, Class<T> type) {
        if (value == null) {
            return new ArrayList<>();
        }

        ArrayList<T> list = new ArrayList<>();
        String[] arr = listTextAsArray(value);

        for (String name : arr) {
            T obj = DamageImplements.get(name, type);

            if (obj != null) {
                list.add(obj);
            }
        }

        list.removeIf(Objects::isNull);
        return list;
    }

    private static HashMap<Combatant, Integer> getCombatantsFromString(String list, List<NPC> source) {
        HashMap<Combatant, Integer> combatants = new HashMap<>();

        if (list == null) {
            return combatants;
        }

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