package _global_list;

import combat_object.combatant.Combatant;
import encounter_info.Encounter;
import lombok.*;
import lombok.experimental.*;
import util.Filter;

import java.net.URL;
import java.util.List;

@NoArgsConstructor
@ExtensionMethod(Filter.class)
public class Combatants extends GlobalList<Combatant> {

    private static final Combatants INSTANCE = new Combatants();

    public static void init(URL file) {
        INSTANCE.list.clear();
        INSTANCE.init(file, Combatant.class);
    }

    public static Encounter toBattle() {
        return new Encounter(
                Scenarios.toList(),
                getFriendlies(),
                getEnemies()
        );
    }

    public static List<Combatant> getFriendlies() {
        return INSTANCE.list.stream().filter(c -> !c.isEnemy()).toList();
    }

    public static List<Combatant> getEnemies() {
        return INSTANCE.list.stream().filter(Combatant::isEnemy).toList();
    }

}
