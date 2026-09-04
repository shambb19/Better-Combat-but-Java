package combat_object.scenario;

import _global_list.Combatants;
import combat_object.combatant.Combatant;
import combat_object.combatant.NPC;
import input.syntax.Key;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.experimental.ExtensionMethod;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import util.Filterable;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static input.syntax.Key.*;
import static input.syntax.Key.AGAINST;

@EqualsAndHashCode(callSuper = true)
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@ExtensionMethod(input.TextReader.class)
@SuperBuilder
public class ScenarioStarter extends Scenario {

    HashMap<String, Integer> withStarter, againstStarter;

    public static ScenarioStarter from(EnumMap<Key, Object> params) {
        validateAll(params, "Scenario");

        Function<String, HashMap<String, Integer>> mapper = list -> {
            HashMap<String, Integer> result = new HashMap<>();
            if (list == null) return result;
            for (String str : list.listTextAsArray())
                result.put(str.getNameScenario(), str.getQty());
            return result;
        };

        String name = (String) params.get(NAME);
        var with = mapper.apply((String) params.get(WITH));
        var against = mapper.apply((String) params.get(AGAINST));

        return builder().name(name).withStarter(with).againstStarter(against).build();
    }

    public Scenario finalized() {
        HashMap<NPC, Integer> with = new HashMap<>();
        HashMap<NPC, Integer> against = new HashMap<>();

        interface Helper {
            void putNpc(HashMap<NPC, Integer> dest, List<Combatant> source, Map.Entry<String, Integer> entry);
        }
        Helper h = (dest, source, entry) -> {
            NPC npc = Filterable.of(source).castTo(NPC.class).firstWithToStringEquals(entry.getKey());
            dest.put(npc, entry.getValue());
        };

        withStarter.entrySet().forEach(e -> h.putNpc(with, Combatants.getFriendlies(), e));
        againstStarter.entrySet().forEach(e -> h.putNpc(against, Combatants.getEnemies(), e));

        return Scenario.builder().name(name).with(with).against(against).build();
    }

    public static List<Scenario> finalized(List<Scenario> source) {
        return Filterable.of(source).castToAsList(ScenarioStarter.class)
                .stream().map(ScenarioStarter::finalized).toList();
    }

}
