package gui;

import combat.Main;
import combat.PlayerQueue;
import combatants.Combatant;
import util.Dice;
import util.Locators;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class ActionPanel extends JPanel {

    private final PlayerQueue queue;
    private final ArrayList<Combatant> friendlies;
    private final ArrayList<Combatant> enemies;
    private final JTextArea turnInformation = new JTextArea();

    public ActionPanel() {
        queue = Main.queue;

        setLayout(new GridLayout(0, 1));

        friendlies = Main.battle.getFriendlies();
        enemies = Main.battle.getEnemies();

        turnInformation.setText(queue.getCurrentCombatant().toString());
        turnInformation.setEditable(false);
        add(turnInformation);

        add(getHealthAdjustButton(true));
        add(getHealthAdjustButton(false));
        add(getInspirationUsedButton());
        add(getEndTurnButton());
    }

    private JButton getHealthAdjustButton(boolean isForDamage) {
        String buttonName = (isForDamage) ? "Attack" : "Heal";
        String promptMessage = (isForDamage) ? "Enter Damage Dealt" : "Enter Heal Amount";

        JButton button = new JButton(buttonName);
        button.putClientProperty("JButton.buttonType", "roundRect");

        button.addActionListener(e -> {
            Combatant target = promptTargetCombatant(isForDamage);

            int healthAdjustment = -1;
            while (healthAdjustment < 0) {
                String healthString = JOptionPane.showInputDialog(Main.menu, promptMessage, "Better Combat but Java", JOptionPane.QUESTION_MESSAGE);
                try {
                    healthAdjustment = Integer.parseInt(healthString);
                } catch (Exception ignored) {}
            }
            if (isForDamage) {
                target.damage(healthAdjustment);
            } else {
                target.heal(healthAdjustment);
            }

            Main.menu.update();
        });

        return button;
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

    private Combatant promptTargetCombatant(boolean isForDamage) {
        Combatant subject = queue.getCurrentCombatant();
        ArrayList<Combatant> targetTeam = enemies;
        if ((subject.isEnemy() && isForDamage) || (!subject.isEnemy() && !isForDamage)) {
            targetTeam = friendlies;
        }

        Combatant target = null;
        while (target == null) {
            String targetString = JOptionPane.showInputDialog(Main.menu, "Enter Target Combatant", "Better Combat but Java", JOptionPane.QUESTION_MESSAGE);
            target = Locators.locateCombatant(targetTeam, targetString);
        }

        return target;
    }
}
