package scenario_info;

import __main.CombatMain;
import character_info.combatant.Combatant;
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
    public PlayerQueue(List<Combatant> friendlies, List<Combatant> enemies) {
        sortList(friendlies);
        sortList(enemies);

        this.friendlies = new TeamQueue();
        this.friendlies.addAll(friendlies);

        this.enemies = new TeamQueue();
        this.enemies.addAll(enemies);

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
        CombatMain.COMBAT_MENU.update();
        return currentCombatant;
    }

    public Combatant getCurrentCombatant() {
        return currentCombatant;
    }

    /**
     * Sorts the provided list by initiative descending
     * @param combatants unsorted list
     */
    private void sortList(List<Combatant> combatants) {
        combatants.sort((o1, o2) -> -1 * Integer.compare(o1.getInitiative(), o2.getInitiative()));
    }

    static class TeamQueue extends ArrayList<Combatant> {

        private int currentIndex = -1;

        public Combatant getNext() {
            if (currentIndex == size() - 1) {
                currentIndex = -1;
            }
            return get(++currentIndex);
        }

        public boolean isTurnOver() {
            return currentIndex == size() - 1;
        }
    }

}
