package _global_list;

import character_info.combatant.Combatant;
import encounter_info.Battle;
import encounter_info.Scenario;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Combatants extends GlobalList<Combatant> {

    private static final Combatants INSTANCE = new Combatants();

    private Combatants() {
    }

    public static void init(File file) {
        INSTANCE.init(file, Combatant.class);
    }

    public static Combatant get(String name) {
        return INSTANCE.getItem(name, Combatant.class);
    }

    public static ArrayList<Combatant> toList() {
        return INSTANCE.castToList(Combatant.class);
    }

    public static Scenario toScenario() {
        HashMap<Combatant, Integer> friendlyMap = new HashMap<>();
        partitionedList().get(false).forEach(friendly -> friendlyMap.put(friendly, 1));

        HashMap<Combatant, Integer> enemyMap = new HashMap<>();
        partitionedList().get(true).forEach(enemy -> enemyMap.put(enemy, 1));

        return new Scenario("Full Campaign (All Combatants)", friendlyMap, enemyMap);
    }

    public static Battle toBattle() {
        return new Battle(
                partitionedList().get(false),
                partitionedList().get(true),
                Scenarios.toList()
        );
    }

    public static void add(Combatant obj) {
        INSTANCE.addItem(obj);
    }

    private static Map<Boolean, List<Combatant>> partitionedList() {
        return INSTANCE.list.stream()
                .collect(Collectors.partitioningBy(Combatant::isEnemy));
    }

}
