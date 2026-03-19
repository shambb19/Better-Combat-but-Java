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
        int barValue = thisCombatant.hp();
        if (thisCombatant.isEnemy() && thisCombatant.lifeStatus().isConscious()) {
            barValue = thisCombatant.maxHp();
        }

        healthBar.setValue(barValue);
        healthBar.setString(thisCombatant.getHealthBarString());
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
