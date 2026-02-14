package gui;

import combat.Main;
import combat.PlayerQueue;
import combatants.Combatant;
import gui.popup.damage.DamagePromptPopup;
import gui.popup.HealPromptPopup;
import util.Dice;

import javax.swing.*;
import java.awt.*;

public class ActionPanel extends JPanel {

    private final PlayerQueue queue;

    private final JTextArea turnInformation = new JTextArea();
    private final JProgressBar currentCombatantHealthBar;

    public ActionPanel() {
        queue = Main.queue;

        setLayout(new GridLayout(0, 1));

        updateTurnInformation();

        currentCombatantHealthBar = new JProgressBar();
        currentCombatantHealthBar.putClientProperty("JComponent.roundRect", true);
        currentCombatantHealthBar.setStringPainted(true);
        currentCombatantHealthBar.setMinimum(0);
        copyHealthBar(queue.getCurrentCombatant().getHealthBar());

        JButton attackButton = new JButton("Attack");
        attackButton.putClientProperty("JButton.buttonType", "roundRect");
        attackButton.addActionListener(e -> new DamagePromptPopup().setVisible(true));

        JButton healButton = new JButton("Heal");
        healButton.putClientProperty("JButton.buttonType", "roundRect");
        healButton.addActionListener(e -> new HealPromptPopup().setVisible(true));

        add(turnInformation);
        add(currentCombatantHealthBar);
        add(attackButton);
        add(healButton);
        add(getInspirationUsedButton());
        add(getEndTurnButton());
    }

    private JButton getEndTurnButton() {
        JButton endTurnButton = new JButton("End Turn");
        endTurnButton.putClientProperty("JButton.buttonType", "roundRect");

        endTurnButton.addActionListener(e -> {
            Combatant newCurrentCombatant = queue.endTurnAndGetNext();
            updateTurnInformation();

            if (!newCurrentCombatant.lifeStatus().isConscious()) {
                int deathSaveThrow = Dice.promptValueFromRoll("Death Save", 1, 20);
                newCurrentCombatant.lifeStatus().rollDeathSave(deathSaveThrow);
            }

            Main.menu.update();
        });

        return endTurnButton;
    }

    private JButton getInspirationUsedButton() {
        JButton inspirationButton = new JButton("Use Inspiration");
        inspirationButton.putClientProperty("JButton.buttonType", "roundRect");

        inspirationButton.addActionListener(e -> {
            boolean isExcessInspiration = queue.getCurrentCombatant().useInspirationAndCheckExcess();
            updateTurnInformation();

            if (isExcessInspiration) {
                int excessInspirationRoll = Dice.promptValueFromRoll("Inspiration", 1, 4);
                Main.menu.logInspiration(excessInspirationRoll);
            }
        });

        return inspirationButton;
    }

    public void updateTurnInformation() {
        turnInformation.setText(queue.getCurrentCombatant().toString());
        if (queue.getCurrentCombatant().isEnemy()) {
            turnInformation.setForeground(new Color(122, 160, 245));
        } else {
            turnInformation.setForeground(Color.WHITE);
        }
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