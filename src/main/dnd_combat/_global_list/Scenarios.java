package _global_list;

import encounter_info.Scenario;

import java.net.URL;
import java.util.ArrayList;

public class Scenarios extends GlobalList<Scenario> {

    private static final Scenarios INSTANCE = new Scenarios();

    private Scenarios() {
    }

    public static void init(URL file) {
        INSTANCE.list.clear();
        INSTANCE.init(file, Scenario.class);
    }

    public static Scenario get(String name) {
        return INSTANCE.getItem(name, Scenario.class);
    }

    public static ArrayList<Scenario> toList() {
        return INSTANCE.castToList(Scenario.class);
    }

    public static void add(Scenario obj) {
        INSTANCE.addItem(obj);
    }

}
