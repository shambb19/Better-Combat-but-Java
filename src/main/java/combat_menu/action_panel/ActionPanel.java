package combat_menu.action_panel;

import __main.Main;
import __main.manager.EncounterManager;
import combat_menu.CombatantHeaderPanel;
import combat_menu.CombatantPanel;
import combat_menu.action_panel.form.AttackFormPanel;
import combat_menu.action_panel.form.DamageFormPanel;
import combat_menu.action_panel.form.HealFormPanel;
import combat_menu.action_panel.form.InspirationFormPanel;
import combat_object.combatant.Combatant;
import combat_object.damage_implements.Implement;
import format.ColorStyles;
import lombok.*;
import lombok.experimental.*;
import org.intellij.lang.annotations.MagicConstant;
import swing.swing_comp.SwingPane;
import util.Locators;

import javax.swing.*;
import java.awt.*;
import java.util.Map;
import java.util.function.Supplier;

@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class ActionPanel extends JPanel {

    public static final String
            ACTION_BUTTONS = "ACTION_BUTTONS",
            ATTACK_OPTION = "ATTACK_OPTION",
            DAMAGE_AMOUNT_OPTION = "DAMAGE_AMOUNT_OPTION",
            HEAL_OPTION = "HEAL_OPTION",
            INSPIRATION_OPTION = "INSPIRATION_OPTION";

    static Map<String, Supplier<JPanel>> panelGetters = Map.of(
            ACTION_BUTTONS, () -> SwingPane.panel().withBackground(ColorStyles.TRACK).component(),
            ATTACK_OPTION, AttackFormPanel::newInstance,
            HEAL_OPTION, HealFormPanel::newInstance,
            INSPIRATION_OPTION, InspirationFormPanel::newInstance
    );

    JPanel turnInformation;
    ActionButtons buttonsPanel;
    JPanel formPanel;
    @NonFinal CombatantHeaderPanel headerPanel = null;

    public static ActionPanel newInstance() {
        return new ActionPanel();
    }

    {
        SwingPane.modifiable(this)
                .withLayout(SwingPane.BORDER)
                .withEmptyBorder(30, 30, 30, 30);

        turnInformation = SwingPane.panelIn(this, BorderLayout.NORTH)
                .withLayout(SwingPane.BORDER)
                .transparent()
                .withEmptyBorder(20, 20, 20, 20)
                .component();

        JPanel mainPanel = SwingPane.panelIn(this, BorderLayout.CENTER)
                .withLayout(SwingPane.BORDER)
                .component();

        JPanel splitPanel = SwingPane.panel()
                .withLayout(SwingPane.BORDER)
                .withBackground(ColorStyles.TRACK)
                .opaque()
                .component();

        buttonsPanel = ActionButtons.newInstance(this);
        buttonsPanel.setOpaque(false);

        splitPanel.add(buttonsPanel, BorderLayout.WEST);

        formPanel = SwingPane.panel()
                .withLayout(SwingPane.BORDER)
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
        buttonsPanel.confirmButtonStates();
    }

    public void cancelAction() {
        returnToButtons();
        buttonsPanel.onActionCancelled();
    }

    public void returnToButtons() {
        switchTo(ACTION_BUTTONS);
        buttonsPanel.setEnabled(true);
        Main.getCombatMenu().setActionMode(CombatantPanel.TURN);
    }

    public void switchTo(
            @MagicConstant(stringValues =
                    {
                            ActionPanel.ACTION_BUTTONS,
                            ActionPanel.ATTACK_OPTION,
                            ActionPanel.HEAL_OPTION,
                            ActionPanel.INSPIRATION_OPTION
                    }) String key
    ) {
        buttonsPanel.setEnabled(false);

        formPanel.removeAll();
        formPanel.add(panelGetters.get(key).get(), BorderLayout.CENTER);

        Main.refreshUI();
    }

    public void promptDamageAmount(Implement implement, Combatant target, boolean attackSucceeded) {
        Component existing = Locators.componentFromCardLayoutWithKey(formPanel, DAMAGE_AMOUNT_OPTION);
        if (existing != null) {
            formPanel.remove(existing);
        }

        DamageFormPanel damageAmountPanel = new DamageFormPanel(target, implement, attackSucceeded);

        formPanel.removeAll();
        formPanel.add(damageAmountPanel, BorderLayout.CENTER);
        formPanel.revalidate();
        formPanel.repaint();

        damageAmountPanel.requestFocusInWindow();
    }

    public void onMainActionConfirmed() {
        buttonsPanel.onMainActionConfirmed();
    }

    public void startNewTurn() {
        buttonsPanel.resetForNewTurn();
    }
}