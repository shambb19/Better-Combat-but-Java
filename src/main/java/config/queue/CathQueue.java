package config.queue;

import _manager.ConcentrationManager;
import _manager.EffectManager;
import combat_object.combatant.Combatant;
import lombok.experimental.*;
import util.Message;
import swing.popup.PopupPrompt;

import java.util.Set;

import static combat_object.implement.Effect.BANISHED;
import static swing.ColorStyles.CONCENTRATION;
import static swing.ColorStyles.SUCCESS;

@ExtensionMethod(EffectManager.class)
public class CathQueue extends PlayerQueue {

    public CathQueue(Set<Combatant> friendlies, Set<Combatant> enemies) {
        super(friendlies, enemies);
    }

    @Override public void endCurrentTurn() {
        currentCombatant.logTurnEnd();

        if (++enemyIndex < enemies.size()) {
            currentCombatant = enemies.get(enemyIndex);
        } else {
            enemyIndex = -1;
            if (++friendlyIndex >= friendlies.size()) {
                friendlyIndex = 0;
            }
            currentCombatant = friendlies.get(friendlyIndex);
        }

        super.endCurrentTurn();
    }

    @Override protected void processTurnStart() {
        if (currentCombatant == null) return;
        currentCombatant.logTurnStart();

        promptConcentration();
        super.processTurnStart();
    }

    @Override protected boolean canTakeTurn() {
        if (currentCombatant.hasEffect(BANISHED)) {
            Message.showAsInfoMessage(currentCombatant + " is banished to another realm and cannot act.");
            currentCombatant.removeEffect(BANISHED);
            return false;
        }
        return super.canTakeTurn();
    }

    private void promptConcentration() {
        if (!ConcentrationManager.isCombatantConcentrating(currentCombatant)) return;

        String message = currentCombatant + " is currently using a spell that requires concentration. " +
                "Taking any actions will end this spell's effects.";
        Message.showActionPrompt(message,
                new PopupPrompt.ActionButton("Take New Action", SUCCESS, null),
                new PopupPrompt.ActionButton("Continue Concentrating", CONCENTRATION, this::endCurrentTurn));
    }

}
