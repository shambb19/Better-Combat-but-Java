package combat_object.scenario;

import _global_list.Combatants;
import combat_object.CombatObject;
import combat_object.combatant.Combatant;
import combat_object.combatant.NPC;
import exception.InvalidParameterException;
import input.Key;
import input.TextReader;
import lombok.*;
import lombok.experimental.*;
import util.Filterable;
import util.Locators;
import util.Message;

import java.util.*;

import static input.Key.*;

@EqualsAndHashCode(callSuper = true) @Value
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@SuperBuilder
public class Scenario extends CombatObject {

    HashMap<String, Integer> with, against;

    public static Scenario create(String name, HashMap<String, Integer> with, HashMap<String, Integer> against) {
        return builder()
                .name(name)
                .with(with)
                .against(against)
                .build();
    }

    public static Scenario from(EnumMap<Key, Object> params) {
        params.forEach((key, value) -> {
            if (!key.isValid(value)) throw new InvalidParameterException("Scenario", key, value);
        });

        String name = (String) params.get(NAME);

        class Helper {
            HashMap<String, Integer> namesFromString(String list) {
                HashMap<String, Integer> result = new HashMap<>();
                if (list == null) return result;
                for (String str : TextReader.listTextAsArray(list))
                    result.put(TextReader.getName(str), TextReader.getQty(str));

                return result;
            }
        }
        Helper $Helper = new Helper();

        var with = $Helper.namesFromString((String) params.get(WITH));
        var against = $Helper.namesFromString((String) params.get(AGAINST));

        return create(name, with, against);
    }

    public ArrayList<NPC> list(boolean isFriendlies, boolean isSingleOccurrences) {
        HashMap<String, Integer> team = isFriendlies ? with : against;

        Filterable<Combatant> sourceFilterable = isFriendlies
                ? Filterable.of(Combatants.getFriendlies())
                : Filterable.of(Combatants.getEnemies());
        List<NPC> source = sourceFilterable.castToAsList(NPC.class);

        ArrayList<NPC> list = new ArrayList<>();
        team.forEach((npcName, qty) -> {
            NPC template = Locators.getWithNameFromDirectory(source, npcName);
            if (template == null) {
                Message.showAsErrorMessage("Scenario.list: could not find NPC '" + npcName + "'");
                return;
            }
            if (isSingleOccurrences || qty == 1) {
                list.add(template);
            } else {
                for (int i = 0; i < qty; i++)
                    list.add(NPC.create(template + " " + (i + 1), template));
            }
        });

        return list;
    }

    public ArrayList<String> toTxt() {
        @lombok.experimental.Helper class Helper {
            static String formattedLine(String key, HashMap<String, Integer> source) {
                StringJoiner joiner = new StringJoiner(", ", key + ": [", "]");
                source.forEach((name, qty) ->
                        joiner.add(qty > 1 ? name + "_" + qty : name));
                return joiner.toString();
            }
        }

        ArrayList<String> txt = new ArrayList<>();
        txt.add(".scenario");
        txt.add("name: " + name);
        if (!with.isEmpty()) txt.add(Helper.formattedLine("with", with));
        txt.add(Helper.formattedLine("against", against));
        txt.add("");
        return txt;
    }

    @Override
    public String toString() {
        return name;
    }
}