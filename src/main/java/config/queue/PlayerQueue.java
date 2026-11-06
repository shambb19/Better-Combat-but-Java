package config.queue;

import __main.Main;
import _manager.CombatManager;
import combat_object.combatant.Combatant;
import exception.ProgramRunError;
import lombok.*;
import lombok.experimental.*;
import swing.popup.InitiativeResolutionPopup;
import util.Message;
import util.Roll;

import javax.swing.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * A class holding ordered lists of the encounter's friendly and enemy
 * combatants that is used to track the combat ordering of combatants
 */
@FieldDefaults(level = AccessLevel.PROTECTED)
public abstract class PlayerQueue {

    public static final Class<? extends PlayerQueue>
            STANDARD_QUEUE = SteampunkQueue.class,
            CATH_QUEUE = CathQueue.class;

    List<Combatant> friendlies, enemies;
    int friendlyIndex = 0, enemyIndex = -1;
    @Getter Combatant currentCombatant;

    /**
     * Accepts the sets of friendly and enemy combatants, sorts and stores
     * them as lists, and selects the first combatant. The constructor also
     * updates the system GUI for the first combatant
     * @param friendlies the set of friendly combatants
     * @param enemies the set of enemy combatants
     */
    public PlayerQueue(Set<Combatant> friendlies, Set<Combatant> enemies) {
        this.friendlies = sorted(friendlies);
        this.enemies = sorted(enemies);

        if (this.friendlies.isEmpty() || this.enemies.isEmpty())
            throw new ProgramRunError("no combatants on at least one team");

        Combatant firstFriendly = this.friendlies.getFirst();
        Combatant firstEnemy = this.enemies.getFirst();

        if (firstFriendly.getInitiative() > firstEnemy.getInitiative()) currentCombatant = firstFriendly;
        else if (firstEnemy.getInitiative() > firstFriendly.getInitiative()) currentCombatant = firstEnemy;
        else currentCombatant = firstFriendly;

        SwingUtilities.invokeLater(CombatManager::confirmButtonStates);
    }

    /**
     * Ends the turn of the current combatant, and determines the Combatant
     * who is next in the queue. super.endCurrentTurn then processes the next
     * Combatant's turn start (ending it if there exist conditions preventing
     * that Combatant from taking a turn) and updates the UI to match
     */
    public void endCurrentTurn() {
        processTurnStart();
        CombatManager.confirmButtonStates();
        Main.getCombatMenu().startNewTurn();
    }

    /**
     * Immediately ends the current combatant's turn and starts a turn for the
     * given Combatant. This method does not alter the queue order in any way;
     * only the turn of the previous current combatant is ended. After the
     * inserted combatant's turn is done, the queue will progress immediately to
     * the turn of the Combatant after the previous current combatant
     * @param c the Combatant for which to forcibly insert a turn
     */
    public void insertCombatantTurn(Combatant c) {
        currentCombatant = c;
        processTurnStart();
    }

    /**
     * Updates the system UI to match the queue update if the new current combatant
     * is able to take a turn (determined with canTakeTurn), else skips that Combatant's
     * turn and progresses in the queue.
     */
    protected void processTurnStart() {
        if (canTakeTurn()) Main.refreshUI();
        else endCurrentTurn();
    }

    /**
     * Returns whether the current combatant is capable of taking any actions on this turn.
     * Reasons why this method may return false include the Combatant being dead or unconscious
     * and an effect being dealt on the Combatant which prevents them from taking actions. If
     * the combatant is unconscious, this method also handles the entering of death saving throws
     * @return true if the current combatant can take actions, else false
     */
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

    /**
     * Returns a List derived from the given Set, which is sorted by initiative descending
     * @param combatants the set of Combatants to be sorted
     * @return the sorted List
     */
    protected static List<Combatant> sorted(Set<Combatant> combatants) {
        ArrayList<Combatant> sortedList = new ArrayList<>();

        var initiativeSets = combatants.stream().collect(Collectors.groupingBy(Combatant::getInitiative));
        TreeMap<Integer, List<Combatant>> treeMap = new TreeMap<>(initiativeSets);

        for (var set : treeMap.descendingMap().values()) {
            List<Combatant> toAdd = (set.size() == 1) ? set : new InitiativeResolutionPopup(set).result;
            sortedList.addAll(toAdd);
        }

        return sortedList;
    }

}
