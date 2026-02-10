package gui;

import combatants.Combatant;

import javax.swing.*;
import java.awt.*;

public class CombatantPanel extends JPanel {

    private final Combatant thisCombatant;

    private final JTextField nameField;
    private final JProgressBar healthBar;

    public CombatantPanel(Combatant thisCombatant) {
        this.thisCombatant = thisCombatant;

        setLayout(new BorderLayout());

        nameField = new JTextField(thisCombatant.getName());
        nameField.putClientProperty("JComponent.roundRect", true);
        nameField.setEditable(false);
        nameField.setEnabled(false);

        healthBar = new JProgressBar();
        healthBar.putClientProperty("JComponent.roundRect", true);
        healthBar.setMinimum(0);
        healthBar.setMaximum(thisCombatant.getMaximumHealth());
        healthBar.setStringPainted(true);
        update();

        add(nameField, BorderLayout.WEST);
        add(healthBar, BorderLayout.EAST);

        thisCombatant.setHealthBar(healthBar);
    }

    public void update() {
        if (thisCombatant.isEnemy() && thisCombatant.getLifeStatus().isConscious()) {
            healthBar.setValue(thisCombatant.getMaximumHealth());
            healthBar.setForeground(new Color(122, 160, 245));
            healthBar.setString("?");
            return;
        }
        if (!thisCombatant.getLifeStatus().isAlive()) {
            healthBar.setValue(0);
            healthBar.setString("Dead :((");
            return;
        }
        if (!thisCombatant.getLifeStatus().isConscious()) {
            healthBar.setValue(0);
            int successes = thisCombatant.getLifeStatus().getSuccesses();
            int fails = thisCombatant.getLifeStatus().getFails();
            String healthBarString = "Defeated (" + successes + "-" + fails + ")";
            healthBar.setString(healthBarString);
            return;
        }
        healthBar.setString(thisCombatant.getHealthString());
        healthBar.setValue(thisCombatant.getCurrentHealth());
        healthBar.setForeground(thisCombatant.getHealthBarColor());
        healthBar.repaint();
    }

    public void beginTurn() {
        nameField.setEnabled(true);
    }

    public void endTurn() {
        nameField.setEnabled(false);
    }

    public Combatant getThisCombatant() {
        return thisCombatant;
    }

}
