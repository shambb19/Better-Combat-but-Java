package combat_menu.action_panel;

import __main.CombatMain;
import character_info.combatant.Combatant;
import character_info.combatant.NPC;
import character_info.combatant.PC;
import format.ColorStyle;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import java.awt.*;

public class TurnInformationPanel extends JPanel {

    private final JPanel informationPanel;
    private final JLabel nameLabel;
    private final JLabel initiativeLabel;
    private final JLabel optionLabel;
    private final JProgressBar healthBar;

    public static TurnInformationPanel newInstance() {
        return new TurnInformationPanel();
    }

    private TurnInformationPanel() {
        setLayout(new BorderLayout());

        informationPanel = new JPanel();
        informationPanel.setLayout(new BoxLayout(informationPanel, BoxLayout.Y_AXIS));

        nameLabel = new JLabel();
        nameLabel.putClientProperty("FlatLaf.style", "font: $h1.font");
        nameLabel.setBorder(new EmptyBorder(10, 10, 10, 10));

        initiativeLabel = new JLabel();
        initiativeLabel.setBorder(new EmptyBorder(4, 4, 4, 4));

        optionLabel = new JLabel();
        optionLabel.putClientProperty("FlatLaf.style", "font: $h2.font");
        optionLabel.setBorder(new EmptyBorder(4, 4, 4, 4));

        healthBar = new JProgressBar();
        healthBar.setStringPainted(true);
        initiativeLabel.putClientProperty("FlatLaf.style", "font: $h2.font");
        healthBar.setBorder(new EmptyBorder(10, 10, 10, 10));
        copyHealthBarToCurrent();

        informationPanel.add(nameLabel);
        informationPanel.add(initiativeLabel);
        informationPanel.add(optionLabel);
        informationPanel.add(healthBar);

        add(informationPanel, BorderLayout.CENTER);
        update();
    }

    public void update() {
        Combatant currentCombatant = CombatMain.getCurrentCombatant();
        Color accentColor = switch (currentCombatant) {
            case PC ignored -> ColorStyle.PARTY.getColor();
            case NPC npc when npc.isEnemy() -> ColorStyle.ENEMY.getColor();
            default -> ColorStyle.NPC.getColor();
        };

        informationPanel.setBorder(new CompoundBorder(
                new MatteBorder(0, 5, 0, 0, accentColor),
                new EmptyBorder(10, 15, 10, 15)
        ));

        nameLabel.setText(currentCombatant.name());
        nameLabel.setForeground(accentColor);

        initiativeLabel.setText("Initiative: " + currentCombatant.initiative());

        optionLabel.setForeground(ColorStyle.FLATLAF_TEXT_DEFAULT.getColor());
        if (currentCombatant.lifeStatus().isConscious()) {
            int inspirationsUsed = currentCombatant.inspiration();
            optionLabel.setText("Inspirations Used: " + inspirationsUsed + "/2");
            if (inspirationsUsed > 2) {
                optionLabel.setForeground(ColorStyle.ORANGE_ISH_RED.getColor());
            }
        } else {
            optionLabel.setText("Not in Fighting Condition");
            optionLabel.setForeground(ColorStyle.ORANGE_ISH_RED.getColor());
        }
        optionLabel.setVisible(!currentCombatant.isEnemy());

        copyHealthBarToCurrent();

        revalidate();
        repaint();
    }

    private void copyHealthBarToCurrent() {
        JProgressBar mimic = CombatMain.getCurrentCombatant().getHealthBar();

        healthBar.setStringPainted(false);
        healthBar.setMinimum(mimic.getMinimum());
        healthBar.setMaximum(mimic.getMaximum());
        healthBar.setForeground(mimic.getForeground());

        if (CombatMain.getCurrentCombatant().isEnemy()) {
            healthBar.setValue(healthBar.getMaximum());
        } else {
            healthBar.setValue(mimic.getValue());
        }
    }

}
