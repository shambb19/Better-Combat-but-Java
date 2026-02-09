package gui;

import combat.Main;
import combat.PlayerQueue;
import combatants.Combatant;
import gui.popup.DamagePromptPopup;
import gui.popup.HealPromptPopup;
import util.Dice;

import javax.swing.*;
import java.awt.*;

public class ActionPanel extends JPanel {

    private final PlayerQueue queue;

    private final JTextArea turnInformation = new JTextArea();

    public ActionPanel() {
        queue = Main.queue;

        setLayout(new GridLayout(0, 1));

        turnInformation.setText(queue.getCurrentCombatant().toString());
        turnInformation.setEditable(false);

        JButton attackButton = new JButton("Attack");
        attackButton.putClientProperty("JButton.buttonType", "roundRect");
        attackButton.addActionListener(e -> new DamagePromptPopup().setVisible(true));

        JButton healButton = new JButton("Heal");
        healButton.putClientProperty("JButton.buttonType", "roundRect");
        healButton.addActionListener(e -> new HealPromptPopup().setVisible(true));

        add(turnInformation);
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
            turnInformation.setText(newCurrentCombatant.toString());

            if (!newCurrentCombatant.getLifeStatus().isConscious()) {
                int deathSaveThrow = Dice.promptValueFromRoll("Death Save", 1, 20);
                newCurrentCombatant.getLifeStatus().rollDeathSave(deathSaveThrow);
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
            turnInformation.setText(queue.getCurrentCombatant().toString());

            if (isExcessInspiration) {
                int excessInspirationRoll = Dice.promptValueFromRoll("Inspiration", 1, 4);
                Main.menu.logInspiration(excessInspirationRoll);
            }
        });

        return inspirationButton;
    }
}
