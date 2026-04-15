package __main.manager;

import __main.Main;
import combat_menu.CombatantPanel;
import combat_menu.action_panel.ActionPanel;
import combat_object.combatant.Combatant;
import combat_object.damage_implements.Effect;
import combat_object.damage_implements.Implement;
import combat_object.damage_implements.Spell;
import combat_object.damage_implements.Weapon;
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
            Main.getCombatMenu().setActionMode(CombatantPanel.TURN);
        });
    }

    public boolean logAttack(Combatant target, int roll, Implement implement) {
        Combatant attacker = EncounterManager.getCurrentCombatant();

        boolean autoHits = implement instanceof Spell s && s.doesNotRequireAttackRoll();

        if (!autoHits) {
            if (implement instanceof Spell s && s.hasSave()) {
                target.logRoll(roll, 1, 20);

                EffectManager.endPenaltiesOn(target);
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
            case Weapon w -> roll + attacker.attackBonus(w) >= target.getArmorClass();
            case Spell s when s.doesNotRequireAttackRoll() -> true;
            case Spell s when s.hasSave() -> roll + target.mod(s.getStat()) < attacker.saveDc();
            case Spell s when !s.hasSave() -> roll + attacker.spellAttackBonus() >= target.getArmorClass();
            default -> throw new ClassCastException("isAttackSuccess: implement is neither Weapon nor Spell");
        };
    }

    public void finishAction() {
        SwingUtilities.invokeLater(() -> {
            getActionPanel().returnToButtons();
            getActionPanel().onMainActionConfirmed();
            Main.getCombatMenu().setActionMode(CombatantPanel.TURN);
            Main.refreshUI();
        });
    }

    public void logDamage(Combatant target, Implement implement,
                          int roll, int bonus) {
        Combatant attacker = EncounterManager.getCurrentCombatant();

        if (!implement.isManual())
            attacker.logRoll(roll, implement.getNumDice(), implement.getDieSize());

        target.damage(roll + bonus);

        if (implement.effectEquals(Effect.HEAL_SELF))
            attacker.heal(roll);

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