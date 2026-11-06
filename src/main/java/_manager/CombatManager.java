package _manager;

import __main.Main;
import combat_menu.menu.CombatMenu;
import combat_menu.action_panel.ActionPanel;
import combat_object.combatant.Combatant;
import combat_object.implement.Implement;
import config.Config;
import config.ruleset.AttackResult;
import lombok.*;
import lombok.experimental.*;
import swing.builder.ComponentBuilder;
import util.StringUtil;
import util.function.Writer;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static swing.ColorStyles.*;
import static swing.builder.ComponentBuilder.FontBuilder.*;

@UtilityClass
@ExtensionMethod(StringUtil.class)
public class CombatManager {

    public static final Writer<Integer> DAMAGE_MESSAGE = dmg ->
            "..attacker.. dealt " + dmg + " damage to ..target..";

    public static final Writer<Integer> HEAL_MESSAGE = amt ->
            "..attacker.. healed ..target.. for " + amt + " HP";

    public static final Writer<Integer> DEFEATED_MESSAGE = amt ->
            "..target.. was defeated by ..attacker..";

    @Getter
    List<LoggedAction> actionLog = new ArrayList<>();

    /**
     * Closes any open action dialog in the {@code ActionPanel} and returns to
     * its default state, then refreshes all {@code ActionPanel} components. This
     * method is functionally different from {@link #finishAction()} in
     * that it does not log changes in turn status; it only verifies the
     * standard state.
     */
    public void confirmButtonStates() {
        SwingUtilities.invokeLater(() ->
                Optional.ofNullable(getActionPanel()).ifPresent(ap -> {
                    ap.returnToButtons();
                    ap.confirmButtonStates();
                }));
    }

    /**
     * Forces the end of any outstanding action in the {@code ActionPanel}.
     * No inputs or results are saved.
     */
    public void cancelAction() {
        SwingUtilities.invokeLater(() -> {
            getActionPanel().cancelAction();
            Main.getCombatMenu().endActionState();
        });
    }

    /**
     * Returns the {@link AttackResult} enum constant specifying the outcome of the
     * attack with the given parameters, which is determined in the {@code Ruleset}
     * specific implementation of this method. Target is the only Combatant input
     * required, as the attacker is in all contexts assumed to be
     * {@link EncounterManager#currentCombatant()}. This is not the same action as
     * {@link #logDamage(Combatant, Implement, int, int)} as it is only used to prompt
     * the attempt of an attack; on a successful attack, it will be called here
     * @param target the Combatant being targeted
     * @param roll the roll used in the attack
     * @param implement the implement used in the attack
     * @return the AttackResult enum constant indicating the attack's outcome
     */
    public AttackResult logAttack(Combatant target, int roll, Implement implement) {
        return Config.getRuleset().logAttack(target, implement, roll);
    }

    /**
     * Closes any open action dialog in the {@code ActionPanel} and logs the
     * completion of that dialog's action, then refreshes all {@code ActionPanel}
     * components. This method is functionally different from
     * {@link #confirmButtonStates()} in that this latter method will not
     * log the completion of an action or adjust the current Combatant's number
     * of actions remaining; that method only verifies the standard state.
     */
    public void finishAction() {
        SwingUtilities.invokeLater(() -> {
            getActionPanel().returnToButtons();
            getActionPanel().onMainActionConfirmed();
            Main.getCombatMenu().endActionState();
            Main.refreshUI();
        });
    }

    /**
     * Logs damage dealt to the given target Combatant (determined by the
     * given parameters in this method's {@code Ruleset} implementation) then
     * writes an appropriate message based on whether the target Combatant has
     * been defeated. Target is the only Combatant input required here,
     * as the attacker is in all contexts assumed to be
     * EncounterManager.getCurrentCombatant
     * @param target the Combatant being targeted
     * @param implement the Implement used in the attack
     * @param roll the roll used in the attack
     * @param bonus any bonus damage entered
     */
    public void logDamage(Combatant target, Implement implement,
                          int roll, int bonus) {
        Config.getRuleset().logDamage(target, implement, roll, bonus);

        if (target.isConscious()) logAction(DAMAGE_MESSAGE, roll + bonus, target);
        else logAction(DEFEATED_MESSAGE, 0, target);

        finishAction();
    }

    /**
     * Heals the given combatant by the given amount, writes a heal message, then closes the heal action dialog
     * @param target the Combatant to be healed
     * @param amount the amount by which the target is to be healed
     */
    public void logHeal(Combatant target, int amount) {
        target.heal(amount);
        logAction(HEAL_MESSAGE, amount, target);
        finishAction();
    }

    /**
     * Writes a message to the action log. The message is the result
     * of the given int applied to the given Writer, with all
     * {@code ..attacker..} and {@code ..target..} placeholders replaced
     * respectively with {@link EncounterManager#currentCombatant()} and
     * the given target.
     * @param writer the Writer to which int input is applied
     * @param input the input applied to the Writer
     * @param target the Combatant being targeted in the logged action
     */
    private void logAction(Writer<Integer> writer, int input, Combatant target) {
        String message = writer.write(input);
        Combatant attacker = EncounterManager.currentCombatant();
        String logMessage = message.infoString(attacker, target);
        actionLog.add(new LoggedAction(logMessage));
    }

    /**
     * Returns the {@code ActionPanel} if {@link Main#getCombatMenu()} is not null, else null
     * @return {@link CombatMenu#getActionPanel()} if combat menu is present, else null
     */
    private ActionPanel getActionPanel() {
        Optional<CombatMenu> combatMenu = Optional.ofNullable(Main.getCombatMenu());
        return combatMenu.map(CombatMenu::getActionPanel).orElse(null);
    }

    @Value
    public static class LoggedAction {
        String logMessage;
        String timeLogged;
        ComponentBuilder.FontBuilder displayFont;

        /**
         * A data class that holds a message detailing an action, the time in
         * m:ss format at which the action was completed, and the
         * {@link swing.builder.ComponentBuilder.FontBuilder} object with which
         * the message is to be displayed.
         * Damage messages show {@link swing.ColorStyles#UNKNOWN UNKNOWN},
         * heal messages show {@link swing.ColorStyles#HEALTHY HEALTHY},
         * and kill messages show {@link swing.ColorStyles#CRITICAL CRITICAL} and are bolded.
         */
        public LoggedAction(String logMessage) {
            this.logMessage = logMessage;

            long exactElapsedSeconds = (System.currentTimeMillis() - Main.START_TIME_MILLISECONDS) / 1000;
            long elapsedMinutes = exactElapsedSeconds / 60;
            long elapsedSeconds = exactElapsedSeconds % 60;
            timeLogged = String.format("%d:%02d", elapsedMinutes, elapsedSeconds);

            if (logMessage.contains("damage")) displayFont = of(Font.PLAIN, 12f, UNKNOWN);
            else if (logMessage.contains("defeated")) displayFont = of(Font.BOLD, 14f, CRITICAL);
            else displayFont = of(Font.PLAIN, 12f, HEALTHY);
        }
    }
}