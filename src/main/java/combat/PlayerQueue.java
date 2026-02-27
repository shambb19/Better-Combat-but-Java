package combat;

import combatants.Combatant;
import util.Message;

import java.util.ArrayList;
import java.util.List;

public class PlayerQueue {

    private final TeamQueue friendlies;
    private final TeamQueue enemies;
    private Combatant currentCombatant;

    /**
     * Sorts the friendly and enemy lists and begins the queue with the highest initiative combatant.
     * @param friendlies list of friendly combatants
     * @param enemies list of enemy combatants
     */
    public PlayerQueue(ArrayList<Combatant> friendlies, ArrayList<Combatant> enemies) {
        sortList(friendlies);
        sortList(enemies);

        this.friendlies = new TeamQueue(friendlies);
        this.enemies = new TeamQueue(enemies);

        if (friendlies.getFirst().getInitiative() >= enemies.getFirst().getInitiative()) {
            currentCombatant = this.friendlies.getNext();
        } else {
            currentCombatant = this.enemies.getNext();
        }
    }

    /**
     * Sets and returns the next combatant able to take an action, ranked by initiative. The entire enemy roster will have a turn
     * in between each allied player (as per Cath's combat system). Unconscious combatants are prompted for a death save roll and
     * otherwise skipped. The method also ends any status effects applied by the new current combatant.
     * @return the current combatant (after the method run); or, the next combatant able to take an action after
     * the current combatant (before method run).
     */
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
        if (!currentCombatant.lifeStatus().isConscious()) {
            int saveRoll = Message.getDeathSaveRoll();
            currentCombatant.lifeStatus().rollDeathSave(saveRoll);
            endTurnAndGetNext();
        }
        currentCombatant.endDealtEffects();
        Main.menu.update();
        return currentCombatant;
    }

    public Combatant getCurrentCombatant() {
        return currentCombatant;
    }

    /**
     * Sorts the provided list by initiative descending
     * @param combatants unsorted list
     */
    private void sortList(ArrayList<Combatant> combatants) {
        combatants.sort((o1, o2) -> -1 * Integer.compare(o1.getInitiative(), o2.getInitiative()));
    }

    static class TeamQueue extends ArrayList<Combatant> {

        private Combatant currentCombatant;
        private int currentIndex;

        /**
         * ArrayList extension that tracks the index of the current combatant for readability.
         * @param combatants the root list
         */
        public TeamQueue(List<Combatant> combatants) {
            addAll(combatants);
            currentCombatant = getFirst();
            currentIndex = -1;
        }

        /**
         * Provides the next combatant without checking for effects, consciousness, or any other factors
         * @return next combatant
         */
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

        /**
         * Used only for the enemy team.
         * @return true when the entire enemy team has had a turn. Prompts a return to the friendly queue.
         */
        public boolean isTurnOver() {
            return currentCombatant.equals(getLast());
        }

    }

}
