package gui;

import combat.Main;
import combat.PlayerQueue;
import combatants.Combatant;
import gui.popup.DamagePromptPopup;
import gui.popup.HealPromptPopup;
import util.Dice;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class ActionPanel extends JPanel {

    private final PlayerQueue queue;
    private final ArrayList<Combatant> friendlies;
    private final ArrayList<Combatant> enemies;

    private final JTextArea turnInformation = new JTextArea();
    private final JButton attackButton = new JButton("Attack");
    private final JButton healButton = new JButton("Heal");

    public ActionPanel() {
        queue = Main.queue;

        setLayout(new GridLayout(0, 1));

        friendlies = Main.battle.getFriendlies();
        enemies = Main.battle.getEnemies();

        turnInformation.setText(queue.getCurrentCombatant().toString());
        turnInformation.setEditable(false);

        attackButton.putClientProperty("JButton.buttonType", "roundRect");
        attackButton.addActionListener(e -> new DamagePromptPopup().setVisible(true));

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
