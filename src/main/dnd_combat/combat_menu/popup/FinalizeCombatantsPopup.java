package combat_menu.popup;

import main.CombatMain;
import character_info.Combatant;
import combat_menu.listener.DieRollListener;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class FinalizeCombatantsPopup extends JFrame {

    private final JTextField[] initiatives;
    private final JTextField[] initiativesEnemies;

    private final ArrayList<Combatant> friendlies;
    private final ArrayList<Combatant> enemies;

    public FinalizeCombatantsPopup() {
        setTitle("Finalize Combat Information");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setAlwaysOnTop(true);
        setLayout(new GridLayout(0, 2));

        add(new JLabel("Combatant"));
        add(new JLabel("Initiative Roll"));

        friendlies = CombatMain.BATTLE.friendlies();
        enemies = CombatMain.BATTLE.enemies();

        initiatives = new JTextField[friendlies.size()];
        initiativesEnemies = new JTextField[enemies.size()];

        addDivider();

        for (int i = 0; i < friendlies.size(); i++) {
            JTextField initiativeField = new JTextField();
            initiatives[i] = initiativeField;
            initiativeField.putClientProperty("JComponent.roundRect", true);
            initiativeField.addKeyListener(new DieRollListener(20, initiativeField));

            add(new JLabel(friendlies.get(i).name()));
            add(initiativeField);
        }

        addDivider();

        for (int i = 0; i < enemies.size(); i++) {
            JTextField initiativeField = new JTextField();
            initiativesEnemies[i] = initiativeField;
            initiativeField.putClientProperty("JComponent.roundRect", true);
            initiativeField.addKeyListener(new DieRollListener(20, initiativeField));

            add(new JLabel(enemies.get(i).name()));
            add(initiativeField);
        }

        add(createOkButton());

        pack();
        setLocationRelativeTo(null);
    }

    private JButton createOkButton() {
        JButton button = new JButton("Confirm");
        button.putClientProperty("JButton.buttonType", "roundRect");

        button.addActionListener(e -> {
            for (int i = 0; i < friendlies.size(); i++) {
                int initiative = Integer.parseInt(initiatives[i].getText());
                friendlies.get(i).setInitiative(initiative);
            }
            for (int i = 0; i < enemies.size(); i++) {
                int initiative = Integer.parseInt(initiativesEnemies[i].getText());
                enemies.get(i).setInitiative(initiative);
            }
            dispose();
            CombatMain.start();
        });

        return button;
    }

    private void addDivider() {
        for (int i = 0; i < 2; i++) {
            add(new JSeparator());
        }
    }

}