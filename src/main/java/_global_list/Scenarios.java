package _global_list;

import combat_object.scenario.Scenario;
import lombok.*;
import lombok.experimental.*;
import util.Filter;

import java.net.URL;
import java.util.List;

@NoArgsConstructor
@ExtensionMethod(Filter.class)
public class Scenarios extends GlobalList<Scenario> {

    private static final Scenarios INSTANCE = new Scenarios();

    public static void init(URL file) {
        INSTANCE.list.clear();
        INSTANCE.init(file, Scenario.class);
    }

    public static List<Scenario> toList() {
        return INSTANCE.list.matchingClass(Scenario.class);
    }

}
