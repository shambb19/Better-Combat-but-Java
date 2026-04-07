package combat_menu.action_panel;

import __main.EncounterInfo;
import __main.Main;
import character_info.combatant.Combatant;
import combat_menu.action_panel.damage_panel.AttackPanel;
import combat_menu.action_panel.damage_panel.DamageAmountPanel;
import damage_implements.Implement;
import format.swing_comp.SwingPane;
import util.Locators;

import javax.swing.*;
import java.awt.*;

public class ActionPanel extends JPanel {

    private final JPanel turnInformation;

    private final JPanel cardPanel;
    private final CardLayout cardLayout = new CardLayout();

    public static final String ACTION_BUTTONS = "ACTION_BUTTONS";
    public static final String ATTACK_OPTION = "ATTACK_OPTION";
    public static final String DAMAGE_AMOUNT_OPTION = "DAMAGE_AMOUNT_OPTION";
    public static final String HEAL_OPTION = "HEAL_OPTION";
    public static final String INSPIRATION_OPTION = "INSPIRATION_OPTION";

    public static ActionPanel newInstance() {
        return new ActionPanel();
    }

    private ActionPanel() {
        setLayout(new GridBagLayout());
        SwingPane.modifiable(this).withEmptyBorder(10);

        GridBagConstraints gbc = new GridBagConstraints();

        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.BOTH;

        turnInformation = SwingPane.panel().withLayout(SwingPane.VERTICAL_BOX).build();
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 0.3;
        add(turnInformation, gbc);

        cardPanel = new JPanel(cardLayout);
        ActionButtons buttonsPanel = ActionButtons.newInstance(this);
        JPanel attackPanel = AttackPanel.newInstance(this);
        JPanel healPanel = HealPanel.newInstance(this);
        JPanel inspirationPanel = InspirationPanel.newInstance(this);

        buttonsPanel.setName(ACTION_BUTTONS);
        attackPanel.setName(ATTACK_OPTION);
        healPanel.setName(HEAL_OPTION);
        inspirationPanel.setName(INSPIRATION_OPTION);

        cardPanel.add(buttonsPanel, ACTION_BUTTONS);
        cardPanel.add(attackPanel, ATTACK_OPTION);
        cardPanel.add(healPanel, HEAL_OPTION);
        cardPanel.add(inspirationPanel, INSPIRATION_OPTION);

        gbc.gridy = 1;
        gbc.weighty = 0.7;
        gbc.insets = new Insets(10, 0, 0, 0);
        add(cardPanel, gbc);

        updateTurnInformation();
    }

    public void returnToButtons() {
        cardLayout.show(cardPanel, ACTION_BUTTONS);
    }

    public void switchTo(String name) {
        cardLayout.show(cardPanel, name);
        Main.logAction();
    }

    public void promptDamageAmount(Implement implement, Combatant target) {
        Component existing = Locators.componentFromCardLayoutWithKey(cardPanel, DAMAGE_AMOUNT_OPTION);
        if (existing != null)
            cardPanel.remove(existing);

        DamageAmountPanel damageAmountPanel = DamageAmountPanel.newInstance(implement, target, this);
        cardPanel.add(damageAmountPanel, DAMAGE_AMOUNT_OPTION);
        damageAmountPanel.setName(DAMAGE_AMOUNT_OPTION);

        cardPanel.revalidate();
        cardPanel.repaint();
        cardLayout.show(cardPanel, DAMAGE_AMOUNT_OPTION);
        damageAmountPanel.requestFocusInWindow();
    }

    public void updateTurnInformation() {
        SwingPane.modifiable(turnInformation).contentsReplacedWith(EncounterInfo.getCurrentCombatant().toPanel());
    }
}