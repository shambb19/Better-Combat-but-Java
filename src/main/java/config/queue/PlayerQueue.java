package config.queue;

import __main.Main;
import _manager.CombatManager;
import combat_object.combatant.Combatant;
import exception.IllegalArgumentAtRootException;
import lombok.*;
import lombok.experimental.*;
import util.Message;
import util.Roll;

import javax.swing.*;
import java.util.Comparator;
import java.util.List;

@FieldDefaults(level = AccessLevel.PROTECTED)
public abstract class PlayerQueue {

    public static final Class<? extends PlayerQueue>
            STANDARD_QUEUE = SteampunkQueue.class,
            CATH_QUEUE = CathQueue.class;

    List<Combatant> friendlies, enemies;
    int friendlyIndex = 0, enemyIndex = -1;
    @Getter Combatant currentCombatant;

    public PlayerQueue(List<Combatant> friendlies, List<Combatant> enemies) {
        this.friendlies = friendlies;
        this.enemies = enemies;

        sortList(this.friendlies);
        sortList(this.enemies);

        if (this.friendlies.isEmpty() || this.enemies.isEmpty())
            throw new IllegalArgumentAtRootException("empty combatant list");

        currentCombatant = this.friendlies.getFirst();

        SwingUtilities.invokeLater(CombatManager::confirmButtonStates);
    }

    public void endCurrentTurn() {
        processTurnStart();
        CombatManager.confirmButtonStates();
        Main.getCombatMenu().startNewTurn();
    }

    protected void processTurnStart() {
        if (canTakeTurn()) Main.refreshUI();
        else endCurrentTurn();
    }

    protected boolean canTakeTurn() {
        if (currentCombatant.isUnconscious()) {
            int saveRoll = Message.promptRoll("for ..name..'s death save",
                    Roll.d20(), 10,
                    null, null
            );
            currentCombatant.getLifeStatus().rollDeathSave(saveRoll);

            return false;
        }
        return currentCombatant.isAlive();
    }

    protected void sortList(List<Combatant> combatants) {
        combatants.sort(Comparator.comparingInt(Combatant::getInitiative).reversed());
    }

}
