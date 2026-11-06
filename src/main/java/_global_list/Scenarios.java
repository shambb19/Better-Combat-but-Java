package _global_list;

import combat_object.scenario.Scenario;
import combat_object.scenario.ScenarioStarter;
import lombok.NoArgsConstructor;
import util.Filterable;

import java.net.URL;
import java.util.Set;

@NoArgsConstructor
public class Scenarios extends GlobalList<Scenario> {

    private static final Scenarios INSTANCE = new Scenarios();

    public static void init(URL file) {
        INSTANCE.set.clear();
        INSTANCE.init(file, Scenario.class, ScenarioStarter::finalized);
    }

    public static Set<Scenario> toSet() {
        return Filterable.of(INSTANCE.set, Scenario.class).toSet();
    }

}
