package __main.manager;

import __main.Main;
import combat_menu.action_panel.ActionPanel;
import combat_object.combatant.Combatant;
import combat_object.combatant.info.AbilityModifier;
import combat_object.damage_implements.Effect;
import combat_object.damage_implements.Implement;
import combat_object.damage_implements.Spell;
import lombok.experimental.*;

import javax.swing.*;

@UtilityClass
public class CombatManager {

    public void confirmButtonStates() {
        SwingUtilities.invokeLater(() -> {
            getActionPanel().returnToButtons();
            getActionPanel().confirmButtonStates();
        });
    }

    public void cancelAction() {
        SwingUtilities.invokeLater(() -> {
            getActionPanel().cancelAction();
            Main.getCombatMenu().endActionState();
        });
    }

    public boolean logAttack(Combatant target, int roll, Implement implement) {
        Combatant attacker = EncounterManager.getCurrentCombatant();

        boolean autoHits = implement instanceof Spell s && s.doesNotRequireAttackRoll();

        if (!autoHits) {
            if (implement instanceof Spell s && s.hasSave()) {
                target.logRoll(roll, 1, 20);

                EffectManager.removeEffectOn(target, Effect.PENALTY_SAVE);
            } else
                attacker.logRoll(roll, 1, 20);
        }

        boolean hit = isAttackSuccess(attacker, target, implement, roll);
        boolean continues = hit;
        if (!hit && implement instanceof Spell s) {
            continues = s.dealsHalfDamageAnyways() || autoHits;
        }

        if (continues) {
            EffectManager.logEffect(target, attacker, implement);

            SwingUtilities.invokeLater(() ->
                    getActionPanel().promptDamageAmount(implement, target, hit));
        }

        Main.refreshUI();
        return continues;
    }

    private boolean isAttackSuccess(Combatant attacker, Combatant target, Implement implement, int roll) {
        return switch (implement) {
            case Spell s when s.doesNotRequireAttackRoll() -> true;
            case Spell s when s.hasSave() -> target.getSaveThrow(roll, implement) < attacker.getStats().saveDc();
            default -> attacker.getAttackRoll(roll, implement) >= target.getArmorClass();
        };
    }

    public void finishAction() {
        SwingUtilities.invokeLater(() -> {
            getActionPanel().returnToButtons();
            getActionPanel().onMainActionConfirmed();
            Main.getCombatMenu().endActionState();
            Main.refreshUI();
        });
    }

    public void logDamage(Combatant target, Implement implement,
                          int roll, int bonus) {
        Combatant attacker = EncounterManager.getCurrentCombatant();

        if (!implement.isManual())
            attacker.logRoll(roll, implement.getNumDice(), implement.getDieSize());

        if (implement.effectEquals(Effect.STAT_DROP)) {
            target.getStats().put(AbilityModifier.INT, 1);
            target.getStats().put(AbilityModifier.CHA, 1);
        } else if (implement.effectEquals(Effect.HEAL_SELF)) {
            attacker.heal(roll);
        }
        target.damage(roll + bonus);

        finishAction();
    }

    public void logHeal(Combatant target, int amount) {
        target.heal(amount);
        finishAction();
    }

    private ActionPanel getActionPanel() {
        return Main.getCombatMenu().getActionPanel();
    }
}