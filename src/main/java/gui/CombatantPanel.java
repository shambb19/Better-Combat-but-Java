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

        nameField = new JTextField(thisCombatant.name());
        nameField.putClientProperty("JComponent.roundRect", true);
        nameField.setEditable(false);
        nameField.setEnabled(false);

        healthBar = new JProgressBar();
        healthBar.putClientProperty("JComponent.roundRect", true);
        healthBar.setMinimum(0);
        healthBar.setMaximum(thisCombatant.maxHp());
        healthBar.setStringPainted(true);
        update();

        add(nameField, BorderLayout.WEST);
        add(healthBar, BorderLayout.EAST);

        thisCombatant.setHealthBar(healthBar);
    }

    public void update() {
        if (thisCombatant.isEnemy() && thisCombatant.lifeStatus().isConscious()) {
            healthBar.setValue(thisCombatant.maxHp());
            healthBar.setForeground(new Color(122, 160, 245));
            healthBar.setString("?");
            return;
        }
        if (!thisCombatant.lifeStatus().isAlive()) {
            healthBar.setValue(0);
            healthBar.setString("Dead :((");
            return;
        }
        if (!thisCombatant.lifeStatus().isConscious()) {
            healthBar.setValue(0);
            int successes = thisCombatant.lifeStatus().getSuccesses();
            int fails = thisCombatant.lifeStatus().getFails();
            String healthBarString = "Defeated (" + successes + "-" + fails + ")";
            healthBar.setString(healthBarString);
            return;
        }
        if (thisCombatant.hp() == 0) {
            healthBar.setValue(0);
            healthBar.setString("Alive but down for the count");
        }
        healthBar.setString(thisCombatant.getHealthString());
        healthBar.setValue(thisCombatant.hp());
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
