package combat_menu;

import character_info.combatant.Combatant;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class CombatantPanel extends JPanel {

    private final Combatant thisCombatant;

    private final JLabel nameLabel;
    private final JProgressBar healthBar;

    public CombatantPanel(Combatant thisCombatant) {
        this.thisCombatant = thisCombatant;

        setLayout(new BorderLayout());

        setBorder(new EmptyBorder(8, 8, 8, 8));
        setBackground(UIManager.getColor("LayeredPane.background"));

        nameLabel = new JLabel(thisCombatant.name());
        nameLabel.putClientProperty("JComponent.roundRect", true);
        nameLabel.setEnabled(false);

        healthBar = new JProgressBar();
        healthBar.putClientProperty("JComponent.roundRect", true);
        healthBar.putClientProperty("JComponent.roundRect", true);
        healthBar.putClientProperty("JProgressBar.square", false);
        healthBar.setMinimum(0);
        healthBar.setMaximum(thisCombatant.maxHp());
        healthBar.setStringPainted(true);
        update();

        add(nameLabel, BorderLayout.WEST);
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
        nameLabel.setForeground(Color.BLACK);
        nameLabel.setFont(nameLabel.getFont().deriveFont(Font.BOLD));

        setBackground(UIManager.getColor("List.selectionBackground"));
        setOpaque(true);
    }

    public void endTurn() {
        nameLabel.setForeground(UIManager.getColor("Label.foreground"));
        nameLabel.setFont(nameLabel.getFont().deriveFont(Font.PLAIN));

        setBackground(null);
        setOpaque(false);
    }

    public Combatant getThisCombatant() {
        return thisCombatant;
    }

}
