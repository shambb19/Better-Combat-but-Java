package txt_input;

import character_info.AbilityModifier;
import character_info.Class5e;
import character_info.Stats;
import character_info.combatant.Combatant;
import character_info.combatant.NPC;
import character_info.combatant.PC;
import damage_implements.DamageImplements;
import damage_implements.Effect;
import damage_implements.Spell;
import damage_implements.Weapon;
import scenario_info.Scenario;
import util.Locators;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import static util.TxtReader.*;

public class Decoder {

    public static PC pc(ArrayList<String> params) {
        HashMap<String, String> map = toMap(params);

        String name = map.get("name");
        int hp = getHp(map.get("hp"));
        int hpCur = getHpCur(map.get("hp"));
        int ac = Integer.parseInt(map.get("ac"));
        int level = Integer.parseInt(map.get("level"));
        Class5e class5e = Locators.enumNameSearch(map.get("class"), Class5e.class);

        Stats stats = new Stats(class5e, level);
        stats.put(map.get("stats"));

        ArrayList<Weapon> weapons = new ArrayList<>();
        String weaponsVal = map.get("weapons");
        if (weaponsVal != null) {
            weapons = damageImplements(weaponsVal, Weapon.class);
        }

        ArrayList<Spell> spells = new ArrayList<>();
        String spellsVal = map.get("spells");
        if (spellsVal != null) {
            spells = damageImplements(spellsVal, Spell.class);
        }

        PC pc = new PC(name, hp, ac, stats, weapons, spells);
        pc.setHealth(hpCur);

        return pc;
    }

    public static NPC npc(ArrayList<String> params, boolean isEnemyTeam) {
        HashMap<String, String> map = toMap(params);

        String name = map.get("name");
        int hp = getHp(map.get("hp"));
        int ac = Integer.parseInt(map.get("ac"));

        return new NPC(name, hp, ac, isEnemyTeam);
    }

    public static Scenario scenario(ArrayList<String> params, ArrayList<Object> readItems) {
        HashMap<String, String> map = toMap(params);

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

        String name = map.get("name");

        HashMap<Combatant, Integer> with = new HashMap<>();
        String withVal = map.get("with");
        if (withVal != null) {
            with = getCombatantsFromString(withVal, readFriendlies);
        }

        HashMap<Combatant, Integer> against = new HashMap<>();
        String againstVal = map.get("against");
        if (againstVal != null) {
            against = getCombatantsFromString(againstVal, readEnemies);
        }

        return new Scenario(name, with, against);
    }

    @SuppressWarnings("unchecked")
    public static <T> T implement(ArrayList<String> params, Class<T> type) {
        HashMap<String, String> map = toMap(params);

        String name = map.get("name");
        int numDice = getNumDice(map.get("dmg"));
        int dieSize = getDieSize(map.get("dmg"));
        AbilityModifier stat = Locators.enumNameSearch(map.get("stat"), AbilityModifier.class);
        Effect effect = Locators.enumNameSearch(map.get("effect"), Effect.class);

        if (type.isAssignableFrom(Weapon.class)) {
            return (T) new Weapon(name, numDice, dieSize, stat);
        } else if (type.isAssignableFrom(Spell.class)) {
            return (T) new Spell(name, numDice, dieSize, stat, effect);
        }
        return null;
    }

    private static HashMap<String, String> toMap(ArrayList<String> params) {
        params.removeFirst();

        HashMap<String, String> map = new HashMap<>();

        while (!params.isEmpty()) {
            String key = key(params.getFirst());
            String value = value(params.removeFirst());
            map.put(key, value);
        }
        return map;
    }

    @SuppressWarnings("unchecked")
    private static <T> ArrayList<T> damageImplements(String value, Class<T> type) {
        ArrayList<T> list = new ArrayList<>();
        String[] arr = stripped(value).split(", ");

        for (String name : arr) {
            if (type.isAssignableFrom(Weapon.class)) {
                list.add((T) DamageImplements.get(name, Weapon.class));
            } else if (type.isAssignableFrom(Spell.class)) {
                list.add((T) DamageImplements.get(name, Spell.class));
            }
        }

        list.removeIf(Objects::isNull);
        return list;
    }

    private static HashMap<Combatant, Integer> getCombatantsFromString(String list, List<NPC> source) {
        String[] names = stripped(list).split(", ");
        HashMap<Combatant, Integer> combatants = new HashMap<>();

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