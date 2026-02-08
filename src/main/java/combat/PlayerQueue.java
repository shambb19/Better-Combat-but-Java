package combat;

import combatants.Combatant;

import java.util.ArrayList;

public class PlayerQueue {

    private Combatant currentCombatant;

    private final ArrayList<Combatant> friendlies;
    private int friendlyIndex;

    private final ArrayList<Combatant> enemies;
    private int enemyIndex;

    public PlayerQueue(ArrayList<Combatant> friendlies, ArrayList<Combatant> enemies) {
        this.friendlies = friendlies;
        this.enemies = enemies;

        sortList(this.friendlies);
        sortList(this.enemies);

        friendlyIndex = 0;
        enemyIndex = 0;

        currentCombatant = friendlies.getFirst();
    }

    public Combatant endTurnAndGetNext() {
        if (!currentCombatant.isEnemy()) {
            currentCombatant = enemies.getFirst();
            indexFriendlies();
        }
        indexEnemies();
        if (enemyIndex == -1) {
            enemyIndex++;
            currentCombatant = friendlies.get(friendlyIndex);
        } else {
            currentCombatant = enemies.get(enemyIndex);
        }

        if (!currentCombatant.getLifeStatus().isAlive()) {
            endTurnAndGetNext();
        }
        return currentCombatant;
    }

    public Combatant getCurrentCombatant() {
        return currentCombatant;
    }

    private void indexFriendlies() {
        friendlyIndex++;
        if (friendlyIndex == friendlies.size()) {
            friendlyIndex = 0;
        }
    }

    private void indexEnemies() {
        enemyIndex++;
        if (enemyIndex == enemies.size()) {
            enemyIndex = -1;
        }
    }

    private void sortList(ArrayList<Combatant> combatants) {
        combatants.sort((o1, o2) -> -1 * Integer.compare(o1.getInitiative(), o2.getInitiative()));
    }

}
