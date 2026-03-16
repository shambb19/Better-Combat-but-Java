package _global_list;

import character_info.combatant.Combatant;
import encounter_info.Battle;
import encounter_info.Scenario;
import txt_input.Reader5e;
import util.Message;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Combatants {

    private static final ArrayList<Combatant> COMBATANTS = new ArrayList<>();

    public static void init(URL url) {
        List<Combatant> inputs = Reader5e.getInstancesFromCode(url, Combatant.class);
        COMBATANTS.addAll(inputs);
    }

    public static void init(File file) {
        try {
            init(file.toURI().toURL());
        } catch (MalformedURLException e) {
            Message.fileError(e);
        }
    }

    public static Combatant get(String name) {
        for (Combatant combatant : COMBATANTS) {
            String combatantName = combatant.name().trim();
            if (name.equalsIgnoreCase(combatantName)) {
                return combatant;
            }
        }
        return null;
    }

    public static ArrayList<Combatant> toList() {
        return new ArrayList<>(COMBATANTS);
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
        COMBATANTS.add(obj);
    }

    private static Map<Boolean, List<Combatant>> partitionedList() {
        return COMBATANTS.stream()
                .collect(Collectors.partitioningBy(Combatant::isEnemy));
    }

}
