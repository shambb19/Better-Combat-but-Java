package _global_list;

import scenario_info.Scenario;
import txt_input.Reader5e;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Scenarios {

    private static final ArrayList<Scenario> SCENARIOS = new ArrayList<>();

    public static void init(File file) {
        List<Scenario> inputs = Reader5e.getInstancesFromCode(file, Scenario.class);
        SCENARIOS.addAll(inputs);
    }

    public static Scenario get(String name) {
        for (Scenario combatant : SCENARIOS) {
            String combatantName = combatant.name().trim();
            if (name.equalsIgnoreCase(combatantName)) {
                return combatant;
            }
        }
        return null;
    }

    public static ArrayList<Scenario> toList() {
        return new ArrayList<>(SCENARIOS);
    }

    public static void add(Scenario obj) {
        SCENARIOS.add(obj);
    }

}
