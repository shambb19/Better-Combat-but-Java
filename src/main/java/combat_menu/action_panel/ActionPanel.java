package combat_menu.action_panel;

import __main.Main;
import __main.manager.CombatManager;
import __main.manager.EncounterManager;
import __main.manager.InspirationManager;
import character_info.combatant.Combatant;
import combat_menu.CombatantHeaderPanel;
import combat_menu.CombatantPanel;
import combat_menu.action_panel.form.AttackFormPanel;
import combat_menu.action_panel.form.DamageFormPanel;
import combat_menu.action_panel.form.HealFormPanel;
import combat_menu.action_panel.form.InspirationFormPanel;
import damage_implements.Implement;
import format.ColorStyles;
import org.intellij.lang.annotations.MagicConstant;
import swing.swing_comp.SwingPane;
import util.Locators;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.Map;
import java.util.function.Supplier;

public class ActionPanel extends JPanel {

    public static final String
            ACTION_BUTTONS = "ACTION_BUTTONS",
            ATTACK_OPTION = "ATTACK_OPTION",
            DAMAGE_AMOUNT_OPTION = "DAMAGE_AMOUNT_OPTION",
            HEAL_OPTION = "HEAL_OPTION",
            INSPIRATION_OPTION = "INSPIRATION_OPTION";
    private static final Map<String, Supplier<JPanel>> panelGetters = Map.of(
            ACTION_BUTTONS, () -> SwingPane.panel().withBackground(ColorStyles.TRACK).build(),
            ATTACK_OPTION, AttackFormPanel::newInstance,
            HEAL_OPTION, HealFormPanel::newInstance,
            INSPIRATION_OPTION, InspirationFormPanel::newInstance
    );
    private final JPanel turnInformation;
    private final ActionButtons buttonsPanel;
    private final JPanel formPanel;
    private CombatantHeaderPanel headerPanel = null;

    private ActionPanel() {
        SwingPane.modifiable(this)
                .withLayout(SwingPane.BORDER)
                .withEmptyBorder(30);

        turnInformation = SwingPane.panelIn(this, BorderLayout.NORTH)
                .withLayout(SwingPane.BORDER)
                .transparent()
                .withEmptyBorder(20)
                .build();

        JPanel mainPanel = SwingPane.panelIn(this, BorderLayout.CENTER)
                .withLayout(SwingPane.BORDER)
                .build();

        JPanel splitPanel = SwingPane.panel()
                .withLayout(SwingPane.BORDER)
                .withBackground(ColorStyles.TRACK)
                .opaque()
                .build();

        buttonsPanel = ActionButtons.newInstance(this);
        buttonsPanel.setOpaque(false);

        splitPanel.add(buttonsPanel, BorderLayout.WEST);

        formPanel = SwingPane.panel()
                .withLayout(SwingPane.BORDER)
                .transparent()
                .applied(p -> p.setBorder(new EmptyBorder(10, 20, 10, 20)))
                .build();

        splitPanel.add(this.formPanel, BorderLayout.CENTER);

        mainPanel.add(splitPanel, BorderLayout.CENTER);

        CombatManager.init(this);
        InspirationManager.init(this);

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

    public static ActionPanel newInstance() {
        return new ActionPanel();
    }

    public void confirmButtonStates() {
        buttonsPanel.confirmButtonStates();
    }

    public void returnToButtons() {
        switchTo(ACTION_BUTTONS);
        buttonsPanel.setEnabled(true);
        buttonsPanel.deselectAllButtons();

        Main.getMenu().setActionMode(CombatantPanel.TURN);
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

        Main.logAction();
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
}