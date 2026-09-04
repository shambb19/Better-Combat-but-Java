package _global_list;

import combat_object.combatant.Combatant;
import combat_object.combatant.NPC;
import encounter.Encounter;
import lombok.*;
import util.Filterable;

import java.net.URL;
import java.util.List;

@NoArgsConstructor
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

    public static List<Combatant> getAllCombatants() {
        return INSTANCE.list;
    }

    public static List<Combatant> getFriendlies() {
        return Filterable.of(INSTANCE.list).filteredByAsList(c -> !c.isEnemy());
    }

    public static List<NPC> getFriendlyNpcs() {
        return Filterable.of(INSTANCE.list).castTo(NPC.class).filteredByAsList(c -> !c.isEnemy());
    }

    public static List<Combatant> getEnemies() {
        return Filterable.of(INSTANCE.list).filteredByAsList(Combatant::isEnemy);
    }

    public static List<NPC> getEnemyNpcs() {
        return Filterable.of(INSTANCE.list).castTo(NPC.class).filteredByAsList(Combatant::isEnemy);
    }

}
