package combat_menu.popup.action_panel;

import __main.CombatMain;
import character_info.combatant.Combatant;
import combat_menu.ActionButtonsPanel;
import damage_implements.Implement;
import encounter_info.PlayerQueue;
import format.SwingStyles;
import util.Locators;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class ActionPanel extends JPanel {

    private final PlayerQueue queue;

    private final JTextArea turnInformation;
    private final JProgressBar currentCombatantHealthBar;

    private final JPanel cardPanel;
    private final CardLayout cardLayout = new CardLayout();
    private final ActionButtonsPanel buttonsPanel;

    public static final String ACTION_BUTTONS = "ACTION_BUTTONS";
    public static final String ATTACK_OPTION = "ATTACK_OPTION";
    public static final String DAMAGE_AMOUNT_OPTION = "DAMAGE_AMOUNT_OPTION";
    public static final String HEAL_OPTION = "HEAL_OPTION";

    public static ActionPanel newInstance() {
        return new ActionPanel();
    }

    private ActionPanel() {
        queue = CombatMain.QUEUE;

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(new EmptyBorder(10, 10, 10, 10));

        JPanel currentCombatantPanel = new JPanel();
        currentCombatantPanel.setLayout(new BoxLayout(currentCombatantPanel, BoxLayout.Y_AXIS));
        SwingStyles.addLabeledBorder(currentCombatantPanel, "Current Combatant");

        turnInformation = new JTextArea();
        turnInformation.setOpaque(false);
        turnInformation.setBackground(new Color(0, 0, 0, 0));
        turnInformation.setLineWrap(true);
        turnInformation.setWrapStyleWord(true);

        JScrollPane scrollPane = new JScrollPane(turnInformation);
        scrollPane.setBorder(null);
        currentCombatantPanel.add(scrollPane);

        currentCombatantHealthBar = new JProgressBar();
        currentCombatantHealthBar.setStringPainted(true);
        currentCombatantHealthBar.setMinimum(0);
        currentCombatantHealthBar.setBorder(new EmptyBorder(10, 10, 10, 10));
        copyHealthBar(queue.getCurrentCombatant().getHealthBar());

        currentCombatantPanel.add(turnInformation);
        currentCombatantPanel.add(currentCombatantHealthBar);

        cardPanel = new JPanel(cardLayout);

        buttonsPanel = ActionButtonsPanel.newInstance(this);
        JPanel attackPanel = AttackPanel.newInstance(this);
        JPanel healPanel = HealPanel.newInstance(this);

        buttonsPanel.setName(ACTION_BUTTONS);
        attackPanel.setName(ATTACK_OPTION);
        healPanel.setName(HEAL_OPTION);

        cardPanel.add(buttonsPanel, ACTION_BUTTONS);
        cardPanel.add(attackPanel, ATTACK_OPTION);
        cardPanel.add(healPanel, HEAL_OPTION);

        add(currentCombatantPanel);
        add(cardPanel);

        updateTurnInformation();
    }

    public void returnToButtons() {
        cardLayout.show(cardPanel, ACTION_BUTTONS);
    }

    public void switchTo(String name) {
        cardLayout.show(cardPanel, name);
    }

    public void promptDamageAmount(Implement implement, Combatant target) {
        Component existing = Locators.componentFromCardLayoutWithKey(cardPanel, DAMAGE_AMOUNT_OPTION);
        if (existing != null) {
            cardPanel.remove(existing);
        }

        DamageAmountPanel damageAmountPanel = DamageAmountPanel.newInstance(implement, target, this);
        cardPanel.add(damageAmountPanel, DAMAGE_AMOUNT_OPTION);
        damageAmountPanel.setName(DAMAGE_AMOUNT_OPTION);

        cardPanel.revalidate();
        cardPanel.repaint();
        cardLayout.show(cardPanel, DAMAGE_AMOUNT_OPTION);
        damageAmountPanel.requestFocusInWindow();
    }

    public void updateTurnInformation() {
        turnInformation.setText(queue.getCurrentCombatant().actionList());

        if (queue.getCurrentCombatant().isEnemy()) {
            turnInformation.setForeground(new Color(122, 160, 245));
        } else {
            turnInformation.setForeground(UIManager.getColor("Label.foreground"));
        }

        turnInformation.setEditable(false);
        buttonsPanel.updateTurnInformation();
    }

    public void copyHealthBar(JProgressBar mimic) {
        currentCombatantHealthBar.setString(mimic.getString());
        currentCombatantHealthBar.setMaximum(mimic.getMaximum());
        currentCombatantHealthBar.setForeground(mimic.getForeground());
        if (queue.getCurrentCombatant().isEnemy()) {
            currentCombatantHealthBar.setValue(currentCombatantHealthBar.getMaximum());
        } else {
            currentCombatantHealthBar.setValue(mimic.getValue());
        }
    }

}
