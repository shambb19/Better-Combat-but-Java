package combat;

import combatants.Combatant;

import java.util.ArrayList;
import java.util.List;

public class PlayerQueue {

    private final TeamQueue friendlies;
    private final TeamQueue enemies;
    private Combatant currentCombatant;

    public PlayerQueue(ArrayList<Combatant> friendlies, ArrayList<Combatant> enemies) {
        sortList(friendlies);
        sortList(enemies);

        this.friendlies = new TeamQueue(friendlies);
        this.enemies = new TeamQueue(enemies);

        if (friendlies.getFirst().getInitiative() >= enemies.getFirst().getInitiative()) {
            currentCombatant = friendlies.getFirst();
        } else {
            currentCombatant = enemies.getFirst();
        }
    }

    public Combatant endTurnAndGetNext() {
        if (currentCombatant.isEnemy()) {
            if (enemies.isTurnOver()) {
                currentCombatant = friendlies.getNext();
            } else {
                currentCombatant = enemies.getNext();
            }
        } else {
            currentCombatant = enemies.getNext();
        }
        if (!currentCombatant.getLifeStatus().isAlive()) {
            endTurnAndGetNext();
        }
        return currentCombatant;
    }

    public Combatant getCurrentCombatant() {
        return currentCombatant;
    }

    private void sortList(ArrayList<Combatant> combatants) {
        combatants.sort((o1, o2) -> -1 * Integer.compare(o1.getInitiative(), o2.getInitiative()));
    }

    static class TeamQueue extends ArrayList<Combatant> {

        private Combatant currentCombatant;
        private int currentIndex;

        public TeamQueue(List<Combatant> combatants) {
            addAll(combatants);
            currentCombatant = getFirst();
            currentIndex = 0;
        }

        public Combatant getNext() {
            if (currentCombatant.equals(getLast())) {
                currentCombatant = getFirst();
                currentIndex = 0;
            } else {
                currentIndex++;
                currentCombatant = get(currentIndex);
            }
            return currentCombatant;
        }

        public boolean isTurnOver() {
            return currentCombatant.equals(getLast());
        }

    }

}
