package manager;

import __main.Main;
import combat_menu.action_panel.ActionPanel;
import combat_object.combatant.Combatant;
import combat_object.implement.Implement;
import config.Config;
import lombok.*;
import lombok.experimental.*;
import util.StringUtil;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

@UtilityClass
@ExtensionMethod(StringUtil.class)
public class CombatManager {

    public static final Function<Integer, String> DAMAGE_MESSAGE =
            dmg -> "..attacker.. dealt " + dmg + " damage to ..target..";

    public static final Function<Integer, String> HEAL_MESSAGE =
            amt -> "..attacker.. healed ..target.. for " + amt + " HP";

    public static final String DEFEATED_MESSAGE = "..target.. was defeated by ..attacker..";

    @Getter List<LoggedAction> actionLog = new ArrayList<>();

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
        return Config.getRuleset().logAttack(target, implement, roll);
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
        Config.getRuleset().logDamage(target, implement, roll, bonus);

        Combatant attacker = EncounterManager.getCurrentCombatant();

        if (target.getLifeStatus().isConscious())
            logAction(DAMAGE_MESSAGE.apply(roll + bonus), attacker, target);
        else
            logAction(DEFEATED_MESSAGE, attacker, target);

        finishAction();
    }

    public void logHeal(Combatant target, int amount) {
        target.heal(amount);
        logAction(HEAL_MESSAGE.apply(amount), EncounterManager.getCurrentCombatant(), target);
        finishAction();
    }

    private void logAction(String str, Combatant attacker, Combatant target) {
        String logMessage = str.infoString(attacker, target);
        actionLog.add(new LoggedAction(logMessage));
    }

    private ActionPanel getActionPanel() {
        return Main.getCombatMenu().getActionPanel();
    }

    @Value public static class LoggedAction {
        String logMessage;
        String timeLogged;

        public LoggedAction(String logMessage) {
            this.logMessage = logMessage;
            timeLogged = StringUtil.gameTimeString();
        }
    }
}