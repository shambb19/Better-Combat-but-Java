package _global_list;

import combat_object.combatant.Combatant;
import lombok.*;
import util.Filterable;

import java.net.URL;
import java.util.Set;

@NoArgsConstructor
public class Combatants extends GlobalList<Combatant> {

    private static final Combatants INSTANCE = new Combatants();

    public static void init(URL file) {
        INSTANCE.set.clear();
        INSTANCE.init(file, Combatant.class);
    }

    public static Set<Combatant> toSet() {
        return INSTANCE.set;
    }

    public static Set<Combatant> getFriendlies() {
        return Filterable.of(INSTANCE.set).filteredBy(Combatant::isEnemy, false).toSet();
    }

    public static Set<Combatant> getEnemies() {
        return Filterable.of(INSTANCE.set).filteredBy(Combatant::isEnemy).toSet();
    }

}
