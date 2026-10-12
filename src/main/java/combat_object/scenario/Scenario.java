package combat_object.scenario;

import _global_list.Combatants;
import combat_object.CombatObject;
import combat_object.combatant.Combatant;
import combat_object.combatant.NPC;
import input.syntax.Key;
import lombok.*;
import lombok.experimental.*;
import util.Filterable;
import util.StringUtil;

import java.util.*;
import java.util.function.BiFunction;

import static input.syntax.Key.*;

@EqualsAndHashCode(callSuper = true)
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@ExtensionMethod({input.TextReader.class, StringUtil.class})
@SuperBuilder
public class Scenario extends CombatObject {

    HashMap<NPC, Integer> with, against;

    public static Scenario from(EnumMap<Key, Object> params) {
        validateAll(params, "Scenario");

        BiFunction<String, List<Combatant>, HashMap<NPC, Integer>> mapGetter = (list, source) -> {
            HashMap<NPC, Integer> result = new HashMap<>();
            if (list == null) return result;
            for (String str : list.listTextAsArray()) {
                String name = str.getNameScenario();
                int qty = str.getQty();
                NPC npc = Filterable.of(source, NPC.class).firstOrElseThrow(NPC::getName, name);
                result.put(npc, qty);
            }
            return result;
        };

        String name = (String) params.get(NAME);
        var with = mapGetter.apply((String) params.get(WITH), Combatants.getFriendlies());
        var against = mapGetter.apply((String) params.get(AGAINST), Combatants.getEnemies());

        return builder().name(name).with(with).against(against).build();
    }

    public static Scenario emptyScenario() {
        return Scenario.builder().name("Empty Scenario").with(new HashMap<>()).against(new HashMap<>()).build();
    }

    public ArrayList<NPC> npcList(boolean isFriendlies, boolean isSingleOccurrences) {
        HashMap<NPC, Integer> team = isFriendlies ? with : against;
        if (isSingleOccurrences) return new ArrayList<>(team.keySet());

        ArrayList<NPC> list = new ArrayList<>();
        team.forEach((npc, qty) -> {
            if (qty == 1) list.add(npc);
            else list.addAll(NPC.createBulk(npc, qty));
        });

        return list;
    }

    public ArrayList<NPC> npcListAll(boolean isSingleOccurrences) {
        ArrayList<NPC> friendlies = npcList(true, isSingleOccurrences);
        ArrayList<NPC> enemies = npcList(false, isSingleOccurrences);
        return new ArrayList<>(Filterable.ofLists(friendlies, enemies).toList());
    }

    public ArrayList<String> toTxt() {
        BiFunction<String, HashMap<NPC, Integer>, String> formatter = (key, source) -> {
            StringJoiner joiner = new StringJoiner(", ", ": [", "]");
            source.forEach((npc, qty) -> {
                String txt = npc.getName() + ("_" + qty).stringIfElseBlank(qty > 1);
                joiner.add(txt);
            });
            return joiner.toString();
        };

        ArrayList<String> txt = new ArrayList<>();
        txt.add(".scenario");
        txt.add("name: " + name);
        if (!with.isEmpty()) txt.add(formatter.apply("with", with));
        txt.add(formatter.apply("against", against));
        txt.add("");
        return txt;
    }

    @Override
    public String toString() {
        return name;
    }
}