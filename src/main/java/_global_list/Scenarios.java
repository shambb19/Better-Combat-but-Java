package _global_list;

import combat_object.scenario.Scenario;

@lombok.NoArgsConstructor
@lombok.experimental.ExtensionMethod(util.Filter.class)
public class Scenarios extends GlobalList<Scenario> {

    private static final Scenarios INSTANCE = new Scenarios();

    public static void init(java.net.URL file) {
        INSTANCE.list.clear();
        INSTANCE.init(file, Scenario.class);
    }

    public static java.util.List<Scenario> toList() {
        return INSTANCE.list.castTo(Scenario.class);
    }

}
