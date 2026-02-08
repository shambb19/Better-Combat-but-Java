package scenarios;

import combatants.Combatant;

import java.util.ArrayList;
import java.util.List;

public class Scenario {

    private final ArrayList<Combatant> friendlies;
    private final ArrayList<Combatant> enemies;

    public Scenario(List<Combatant> partyCombatants, List<Combatant> enemyCombatants) {
        this.friendlies = new ArrayList<>(partyCombatants);
        this.enemies = new ArrayList<>(enemyCombatants);
    }

    public ArrayList<Combatant> getFriendlies() {
        return friendlies;
    }

    public ArrayList<Combatant> getEnemies() {
        return enemies;
    }

}
