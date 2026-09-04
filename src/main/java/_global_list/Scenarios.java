package _global_list;

import combat_object.scenario.Scenario;
import combat_object.scenario.ScenarioStarter;
import lombok.NoArgsConstructor;
import util.Filterable;

import java.net.URL;
import java.util.List;

@NoArgsConstructor
public class Scenarios extends GlobalList<Scenario> {

    private static final Scenarios INSTANCE = new Scenarios();

    // absolutely disgusting but I don't have a better idea
    public static void init(URL file) {
        INSTANCE.list.clear();
        INSTANCE.init(file, Scenario.class);
        List<Scenario> scenarios = ScenarioStarter.finalized(INSTANCE.list);
        INSTANCE.list.clear();
        INSTANCE.list.addAll(scenarios);
    }

    public static List<Scenario> toList() {
        return Filterable.of(INSTANCE.list).castToAsList(Scenario.class);
    }

}
