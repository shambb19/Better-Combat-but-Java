package combat_object.scenario;

import combat_object.CombatObject;
import combat_object.combatant.NPC;
import input.CodeWriter;
import lombok.*;
import lombok.experimental.*;
import util.Filterable;
import util.StringUtil;

import java.util.*;
import java.util.function.Function;

@EqualsAndHashCode(callSuper = true)
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@ExtensionMethod({input.TextReader.class, StringUtil.class})
@SuperBuilder
public class Scenario extends CombatObject {

    @Builder.Default Map<NPC, Integer> with = Map.of();
    @Builder.Default Map<NPC, Integer> against = Map.of();

    /**
     * Returns a Scenario object with the with and against Maps both being
     * empty HashMaps
     * @return the empty Scenario object
     */
    public static Scenario emptyScenario() {
        return Scenario.builder().name("Empty Scenario").with(new HashMap<>()).against(new HashMap<>()).build();
    }

    /**
     * Returns an ArrayList of all NPCs in the scenario on the side specified
     * by isFriendlies. If isSingleOccurrences is true, the list only includes
     * one copy of all NPCs, even if its quantity is > 0. Otherwise, the list
     * includes all NPCs on the specified side, including carbon copies
     * @param isFriendlies if true, returns allied NPCs, else all enemy NPCs
     * @param isSingleOccurrences if true, returns all unique NPCs, else all NPCs
     *                            including carbon copies
     * @return the specified ArrayList
     */
    public ArrayList<NPC> npcList(boolean isFriendlies, boolean isSingleOccurrences) {
        Map<NPC, Integer> team = isFriendlies ? with : against;
        if (isSingleOccurrences) return new ArrayList<>(team.keySet());

        ArrayList<NPC> list = new ArrayList<>();
        team.forEach((npc, qty) -> {
            if (qty == 1) list.add(npc);
            else list.addAll(NPC.createBulk(npc, qty));
        });

        return list;
    }

    /**
     * Returns an ArrayList of all NPCs, both friendly and enemy, in the scenario.
     * If isSingleOccurrences is true, the list only includes one copy of all NPCs,
     * even if its quantity is > 0. Otherwise, the list includes all NPCs on the
     * specified side, including carbon copies
     * @param isSingleOccurrences if true, returns all unique NPCs, else all NPCs
     *                            including carbon copies
     * @return the specified ArrayList
     */
    public ArrayList<NPC> npcListAll(boolean isSingleOccurrences) {
        ArrayList<NPC> friendlies = npcList(true, isSingleOccurrences);
        ArrayList<NPC> enemies = npcList(false, isSingleOccurrences);
        return new ArrayList<>(Filterable.ofCollections(friendlies, enemies).toList());
    }

    /**
     * Returns the lines of code associated with this Scenario object
     * @return the .txt code lines
     */
    public CodeWriter toTxt() {
        Function<Map<NPC, Integer>, String> formatter = source -> {
            StringJoiner joiner = new StringJoiner(", ", "[", "]");
            source.entrySet().stream().map(e ->
                    e.getKey().getName() + ("_" + e.getValue()).stringIfElseBlank(e.getValue() > 1)
            ).forEach(joiner::add);
            return joiner.toString();
        };

        CodeWriter writer = new CodeWriter(".scenario").withLine("name", name);
        if (!with.isEmpty()) {
            writer = writer.withLine("with", formatter.apply(with));
        }
        writer = writer.withLine("against", formatter.apply(against));

        return writer;
    }

    @Override
    public String toString() {
        return name;
    }
}