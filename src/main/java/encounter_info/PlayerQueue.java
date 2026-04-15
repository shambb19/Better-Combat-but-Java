package encounter_info;

import __main.Main;
import __main.manager.CombatManager;
import __main.manager.EffectManager;
import combat_object.combatant.Combatant;
import combat_object.combatant.LifeStatus;
import lombok.*;
import lombok.experimental.*;
import util.Message;

import javax.swing.*;
import java.util.Comparator;
import java.util.List;

@FieldDefaults(level = AccessLevel.PRIVATE)
public class PlayerQueue {

    final List<Combatant> friendlies, enemies;

    int friendlyIndex = 0, enemyIndex = -1;

    @Getter Combatant currentCombatant = null;

    public PlayerQueue(List<Combatant> friendlies, List<Combatant> enemies) {
        this.friendlies = friendlies;
        this.enemies = enemies;

        sortList(this.friendlies);
        sortList(this.enemies);

        if (!this.friendlies.isEmpty())
            this.currentCombatant = this.friendlies.get(friendlyIndex);
        else if (!this.enemies.isEmpty()) {
            this.enemyIndex = 0;
            this.currentCombatant = this.enemies.get(enemyIndex);
        }

        SwingUtilities.invokeLater(CombatManager::confirmButtonStates);
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
        Main.getCombatMenu().startNewTurn();
    }

    private void incrementFriendly() {
        friendlyIndex++;
        if (friendlyIndex >= friendlies.size())
            friendlyIndex = 0;

        currentCombatant = friendlies.get(friendlyIndex);
    }

    private void processTurnStart() {
        if (currentCombatant == null) return;

        EffectManager.processCombatantTurnStart();

        LifeStatus status = currentCombatant.getLifeStatus();
        if (!status.isConscious()) {
            if (status.isAlive()) {
                int saveRoll = Message.getDeathSaveRoll();
                status.rollDeathSave(saveRoll);
            }

            endCurrentTurn();
            return;
        }

        Main.refreshUI();
    }

    private void sortList(List<Combatant> combatants) {
        combatants.sort(
                Comparator.comparingInt(Combatant::getInitiative)
                        .reversed()
                        .thenComparing(Combatant::getName)
        );
    }
}