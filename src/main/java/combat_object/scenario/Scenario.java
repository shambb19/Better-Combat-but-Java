package combat_object.scenario;

import _global_list.Combatants;
import combat_object.combatant.NPC;
import input.Key;
import lombok.*;
import lombok.experimental.*;
import util.Filter;
import util.Locators;
import util.Message;
import util.TxtReader;

import java.util.*;

import static input.Key.*;

@Value
@ExtensionMethod(Filter.class)
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class Scenario implements combat_object.CombatObject {

    @ToString.Include String name;
    HashMap<String, Integer> with, against;

    public ArrayList<NPC> list(boolean isFriendlies, boolean isSingleOccurrences) {
        HashMap<String, Integer> team = isFriendlies ? with : against;
        List<NPC> source = isFriendlies
                ? Combatants.getFriendlies().castTo(NPC.class)
                : Combatants.getEnemies().castTo(NPC.class);

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

    public static Scenario from(EnumMap<Key, Object> params) {
        String name = (String) params.get(NAME);

        @lombok.experimental.Helper class Helper {
            static HashMap<String, Integer> namesFromString(String list) {
                HashMap<String, Integer> result = new HashMap<>();
                if (list == null) return result;
                for (String str : TxtReader.listTextAsArray(list))
                    result.put(TxtReader.getName(str), TxtReader.getQty(str));
                return result;
            }
        }

        var with = Helper.namesFromString((String) params.get(WITH));
        var against = Helper.namesFromString((String) params.get(AGAINST));

        return new Scenario(name, with, against);
    }

    @Override
    public String toString() {
        return name;
    }
}