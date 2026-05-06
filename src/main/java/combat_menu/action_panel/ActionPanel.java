package combat_menu.action_panel;

import __main.Main;
import combat_menu.CombatantHeaderPanel;
import combat_menu.action_panel.form.AttackFormPanel;
import combat_menu.action_panel.form.DamageFormPanel;
import combat_menu.action_panel.form.HealFormPanel;
import combat_menu.action_panel.form.InspirationFormPanel;
import combat_object.combatant.Combatant;
import combat_object.implement.Implement;
import lombok.*;
import lombok.experimental.*;
import manager.EncounterManager;
import org.intellij.lang.annotations.MagicConstant;

import javax.swing.*;
import java.awt.*;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

import static swing.ColorStyles.TRACK;
import static swing.fluent.SwingPane.*;

@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@ExtensionMethod(util.Filterable.class)
public class ActionPanel extends JPanel {

    public static final String
            ACTION_BUTTONS = "ACTION_BUTTONS",
            ATTACK_OPTION = "ATTACK_OPTION",
            DAMAGE_AMOUNT_OPTION = "DAMAGE_AMOUNT_OPTION",
            HEAL_OPTION = "HEAL_OPTION",
            INSPIRATION_OPTION = "INSPIRATION_OPTION";

    static Map<String, Supplier<JPanel>> panelGetters = Map.of(
            ACTION_BUTTONS, () -> newArrangedAs(BORDER).withBackground(TRACK).component(),
            ATTACK_OPTION, AttackFormPanel::new,
            HEAL_OPTION, HealFormPanel::new,
            INSPIRATION_OPTION, InspirationFormPanel::new
    );

    JPanel turnInformation;
    ActionButtons buttonsPanel;
    JPanel formPanel;
    @NonFinal CombatantHeaderPanel headerPanel = null;

    {
        fluent(this).arrangedAs(BORDER).withEmptyBorder(0, 30, 30, 30);

        turnInformation = panelIn(this, BorderLayout.NORTH).arrangedAs(BORDER).spaced()
                .transparent().component();

        JPanel mainPanel = panelIn(this, BorderLayout.CENTER).arrangedAs(BORDER).component();

        JPanel splitPanel = newArrangedAs(BORDER).withBackground(TRACK).component();

        buttonsPanel = new ActionButtons(this);
        buttonsPanel.setOpaque(false);

        splitPanel.add(buttonsPanel, BorderLayout.WEST);

        formPanel = newArrangedAs(BORDER)
                .transparent()
                .withEmptyBorder(10, 20, 10, 20)
                .component();

        splitPanel.add(this.formPanel, BorderLayout.CENTER);

        mainPanel.add(splitPanel, BorderLayout.CENTER);

        update();
    }

    public void update() {
        Combatant currentCombatant = EncounterManager.getCurrentCombatant();

        if (headerPanel == null || headerPanel.getCombatant() != currentCombatant) {
            headerPanel = new CombatantHeaderPanel(currentCombatant);
            turnInformation.removeAll();
            turnInformation.add(headerPanel);
        } else
            headerPanel.refresh();

        turnInformation.revalidate();
        turnInformation.repaint();
    }

    public void confirmButtonStates() {
        buttonsPanel.refreshLockState();
    }

    public void cancelAction() {
        returnToButtons();
        buttonsPanel.logActionChange(ActionButtons.CANCEL);
    }

    public void returnToButtons() {
        switchTo(ACTION_BUTTONS);
        buttonsPanel.setEnabled(true);
        Main.getCombatMenu().endActionState();
    }

    public void switchTo(
            @MagicConstant(stringValues = {ACTION_BUTTONS, ATTACK_OPTION, HEAL_OPTION, INSPIRATION_OPTION}) String key
    ) {
        buttonsPanel.setEnabled(false);

        formPanel.removeAll();
        formPanel.add(panelGetters.get(key).get(), BorderLayout.CENTER);

        Main.refreshUI();
    }

    public void promptDamageAmount(Implement implement, Combatant target, boolean attackSucceeded) {
        Component existing = formPanel.getComponents().of()
                .filteredBy(c -> c.getName() != null).firstWithToStringEquals(DAMAGE_AMOUNT_OPTION);

        Optional.ofNullable(existing).ifPresent(formPanel::remove);

        DamageFormPanel damageAmountPanel = new DamageFormPanel(target, implement, attackSucceeded);

        formPanel.removeAll();
        formPanel.add(damageAmountPanel, BorderLayout.CENTER);
        formPanel.revalidate();
        formPanel.repaint();

        damageAmountPanel.requestFocusInWindow();
    }

    public void onMainActionConfirmed() {
        buttonsPanel.logActionChange(ActionButtons.CONFIRM);
    }

    public void startNewTurn() {
        buttonsPanel.logActionChange(ActionButtons.NEW_TURN);
    }
}