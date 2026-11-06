package combat_object.scenario;

import _global_list.Combatants;
import com.google.common.collect.Maps;
import combat_object.combatant.Combatant;
import combat_object.combatant.NPC;
import input.syntax.Key;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.experimental.ExtensionMethod;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import util.Filterable;
import util.Locators;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;

import static input.syntax.Key.*;
import static input.syntax.Key.AGAINST;

@EqualsAndHashCode(callSuper = true)
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@ExtensionMethod(input.TextReader.class)
@SuperBuilder
public class ScenarioStarter extends Scenario {

    HashMap<String, Integer> withStarter, againstStarter;

    /**
     * Returns a ScenarioStarter object from the given parameters
     * @param params the ScenarioStarter instance variables,
     *               encoded as Objects and decoded in the Key
     *               class
     * @return the new ScenarioStarter object
     */
    public static ScenarioStarter from(EnumMap<Key, Object> params) {
        validateAll(params, "Scenario");

        Function<String, HashMap<String, Integer>> mapper = list -> {
            HashMap<String, Integer> result = new HashMap<>();
            if (list == null) return result;
            for (String str : list.listTextAsArray()) result.put(str.getNameScenario(), str.getQty());
            return result;
        };

        String name = (String) params.get(NAME);
        var with = mapper.apply((String) params.get(WITH));
        var against = mapper.apply((String) params.get(AGAINST));

        return builder().name(name).withStarter(with).againstStarter(against).build();
    }

    /**
     * Returns the Scenario object created by converting the HashMap instance
     * variables with key String to HashMaps with key NPC. Both version of
     * Scenario refer to the same information, but Scenario houses NPCs directly
     * for ease of access.
     * @return the new ScenarioObject.
     */
    public Scenario finalized() {
        @SuppressWarnings("all")
        BiFunction<HashMap<String, Integer>, Set<Combatant>, Map<NPC, Integer>> converter = (starter, source) -> {
            Set<NPC> keys = Filterable.of(starter.keySet())
                    .mapped(name -> Locators.firstMatching(source, NPC.class, NPC::getName, name).orElseThrow()).toSet();
            return Maps.asMap(keys, npc -> starter.get(npc.toString()));
        };

        Map<NPC, Integer> with = converter.apply(withStarter, Combatants.getFriendlies());
        Map<NPC, Integer> against = converter.apply(againstStarter, Combatants.getEnemies());

        return Scenario.builder().name(name).with(with).against(against).build();
    }

    /**
     * Returns a list of Scenarios created from the provided list. For ease of
     * access, the parameter list has type Scenario but all objects within that
     * are not instantiated as ScenarioStarter will be ignored while converting.
     * @param source the list of ScenarioStarters to be converted
     *               (precondition: all in source instanceof
     *               ScenarioStarter)
     * @return the list of converted Scenarios
     */
    public static List<Scenario> finalized(List<Scenario> source) {
        return Filterable.of(source, ScenarioStarter.class).toList().stream().map(ScenarioStarter::finalized).toList();
    }

}
