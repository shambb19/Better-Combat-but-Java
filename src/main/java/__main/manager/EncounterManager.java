package __main.manager;

import __main.Main;
import combat_object.combatant.Combatant;
import combat_object.combatant.PC;
import combat_object.combatant.info.LifeStatus;
import combat_object.damage_implements.Effect;
import encounter.Encounter;
import format.ColorStyles;
import lombok.*;
import lombok.experimental.*;
import util.Filter;
import util.Message;
import util.PopupPrompt;

import javax.swing.*;
import java.util.Comparator;
import java.util.List;

@UtilityClass
public class EncounterManager {

    @Getter @Setter private static Encounter encounter = null;

    @Getter private static PlayerQueue queue = null;

    public void confirmQueueFinalized() {
        queue = new PlayerQueue(encounter.getFriendlies(), encounter.getEnemies());
    }

    public List<PC> getParty() {
        return Filter.castTo(encounter.getFriendlies(), PC.class);
    }

    public List<Combatant> getFriendlies() {
        return encounter.getFriendlies();
    }

    public List<Combatant> getEnemies() {
        return encounter.getEnemies();
    }

    public Combatant getCurrentCombatant() {
        return queue.getCurrentCombatant();
    }

    public void endCurrentTurn() {
        queue.endCurrentTurn();
    }

    class PlayerQueue {

        final List<Combatant> friendlies, enemies;

        int friendlyIndex = 0, enemyIndex = -1;

        @Getter Combatant currentCombatant = null;

        PlayerQueue(List<Combatant> friendlies, List<Combatant> enemies) {
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

        void endCurrentTurn() {
            EffectManager.logTurnEnd(currentCombatant);

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

        void incrementFriendly() {
            friendlyIndex++;
            if (friendlyIndex >= friendlies.size())
                friendlyIndex = 0;

            currentCombatant = friendlies.get(friendlyIndex);
        }

        void processTurnStart() {
            if (currentCombatant == null) return;

            EffectManager.logTurnStart(currentCombatant);

            LifeStatus status = currentCombatant.getLifeStatus();

            boolean canTakeTurn = true;
            if (!status.isConscious()) {
                if (status.isAlive()) {
                    int saveRoll = Message.promptDeathSaveRoll();
                    status.rollDeathSave(saveRoll);
                }
                canTakeTurn = false;
            }
            if (EffectManager.hasEffect(currentCombatant, Effect.BANISH)) {
                Message.showAsInfoMessage(currentCombatant + "'s turn was skipped because they are banished to another realm.");
                EffectManager.removeEffectOn(currentCombatant, Effect.BANISH);
                canTakeTurn = false;
            }
            if (ConcentrationManager.isCombatantConcentrating(currentCombatant)) {
                String message = currentCombatant + " is currently using a spell that requires concentration. " +
                        "Taking any actions will end this spell's effects.";
                Message.showActionPrompt(message,
                        new PopupPrompt.ActionButton[]{
                                new PopupPrompt.ActionButton("Take New Action", ColorStyles.SUCCESS, null),
                                new PopupPrompt.ActionButton("Continue Concentrating", ColorStyles.CONCENTRATION, this::endCurrentTurn)
                        });
            }

            if (canTakeTurn)
                Main.refreshUI();
            else
                endCurrentTurn();
        }

        void sortList(List<Combatant> combatants) {
            combatants.sort(Comparator.comparingInt(Combatant::getInitiative).reversed());
        }
    }
}