package encounter_info;

import __main.CombatManager;
import __main.Main;
import character_info.combatant.Combatant;
import util.Message;

import javax.swing.*;
import java.util.Comparator;
import java.util.List;

public class PlayerQueue {

    private final List<Combatant> friendlies;
    private final List<Combatant> enemies;

    private int friendlyIndex = 0;
    private int enemyIndex = -1;

    private Combatant currentCombatant = null;

    public PlayerQueue(List<Combatant> friendlies, List<Combatant> enemies) {
        this.friendlies = friendlies;
        this.enemies = enemies;

        sortList(this.friendlies);
        sortList(this.enemies);

        if (!this.friendlies.isEmpty()) {
            this.currentCombatant = this.friendlies.get(friendlyIndex);
        } else if (!this.enemies.isEmpty()) {
            this.enemyIndex = 0;
            this.currentCombatant = this.enemies.get(enemyIndex);
        }

        SwingUtilities.invokeLater(CombatManager::confirmButtonStates);
    }

    public Combatant getCurrentCombatant() {
        return currentCombatant;
    }

    public void endCurrentTurn() {
        if (friendlies.isEmpty() && enemies.isEmpty()) return;

        if (enemyIndex == -1) {
            if (!enemies.isEmpty()) {
                enemyIndex = 0;
                currentCombatant = enemies.get(enemyIndex);
            } else
                incrementFriendly();
        } else {
            enemyIndex++;
            if (enemyIndex < enemies.size())
                currentCombatant = enemies.get(enemyIndex);
            else {
                enemyIndex = -1;
                incrementFriendly();
            }
        }

        processTurnStart();
        CombatManager.confirmButtonStates();
    }

    private void incrementFriendly() {
        friendlyIndex++;
        if (friendlyIndex >= friendlies.size())
            friendlyIndex = 0;

        currentCombatant = friendlies.get(friendlyIndex);
    }

    private void processTurnStart() {
        if (currentCombatant == null) return;

        currentCombatant.endDealtEffects();

        if (!currentCombatant.lifeStatus().isConscious()) {
            if (currentCombatant.lifeStatus().isAlive()) {
                int saveRoll = Message.getDeathSaveRoll();
                currentCombatant.lifeStatus().rollDeathSave(saveRoll);
            }

            endCurrentTurn();
            return;
        }

        Main.logAction();
    }

    private void sortList(List<Combatant> combatants) {
        combatants.sort(
                Comparator.comparingInt(Combatant::initiative)
                        .reversed()
                        .thenComparing(Combatant::name)
        );
    }
}