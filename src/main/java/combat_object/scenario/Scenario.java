package combat_object.scenario;

import _global_list.Combatants;
import combat_object.CombatObject;
import combat_object.combatant.Combatant;
import combat_object.combatant.NPC;
import input.syntax.Key;
import lombok.*;
import lombok.experimental.*;
import util.Filterable;

import java.util.*;

import static input.syntax.Key.*;

@EqualsAndHashCode(callSuper = true)
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@ExtensionMethod(input.TextReader.class)
@SuperBuilder
public class Scenario extends CombatObject {

    HashMap<NPC, Integer> with, against;

    public static Scenario from(EnumMap<Key, Object> params) {
        validateAll(params, "Scenario");

        interface Helper {
            HashMap<NPC, Integer> getMap(String list, List<Combatant> source);
        }
        Helper h = (list, source) -> {
            HashMap<NPC, Integer> result = new HashMap<>();
            if (list == null) return result;
            for (String str : list.listTextAsArray()) {
                String name = str.getNameScenario();
                int qty = str.getQty();
                NPC npc = Filterable.of(source).castTo(NPC.class).firstOrElseThrow(n -> n.getName().equals(name));
                result.put(npc, qty);
            }
            return result;
        };

        String name = (String) params.get(NAME);
        var with = h.getMap((String) params.get(WITH), Combatants.getFriendlies());
        var against = h.getMap((String) params.get(AGAINST), Combatants.getEnemies());

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
            for (int i = 0; i < qty; i++) list.add(NPC.create(npc + " " + (i + 1), npc));
        });

        return list;
    }

    public ArrayList<NPC> npcListAll(boolean isSingleOccurrences) {
        ArrayList<NPC> friendlies = npcList(true, isSingleOccurrences);
        ArrayList<NPC> enemies = npcList(false, isSingleOccurrences);
        return new ArrayList<>(Filterable.ofLists(friendlies, enemies).toList());
    }

    public ArrayList<String> toTxt() {
        interface Helper {
            String formattedLine(String key, HashMap<NPC, Integer> source);
        }
        Helper h = (key, source) -> {
            StringJoiner joiner = new StringJoiner(", ", ": [", "]");
            source.forEach((npc, qty) -> {
                String txt = npc.getName();
                if (qty > 1) txt += "_" + qty;
                joiner.add(txt);
            });
            return joiner.toString();
        };

        ArrayList<String> txt = new ArrayList<>();
        txt.add(".scenario");
        txt.add("name: " + name);
        if (!with.isEmpty()) txt.add(h.formattedLine("with", with));
        txt.add(h.formattedLine("against", against));
        txt.add("");
        return txt;
    }

    @Override
    public String toString() {
        return name;
    }
}