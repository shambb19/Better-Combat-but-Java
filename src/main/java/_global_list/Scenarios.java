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

    public static void init(URL file) {
        INSTANCE.list.clear();
        INSTANCE.init(file, Scenario.class, ScenarioStarter::finalized);
    }

    public static List<Scenario> toList() {
        return Filterable.of(INSTANCE.list).castToAsList(Scenario.class);
    }

}
