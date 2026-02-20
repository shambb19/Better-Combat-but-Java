package gui.popup;

import combat.Main;
import combatants.Combatant;
import gui.listener.DieRollListener;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class FinalizeCombatantsPopup extends JFrame {

    private final JTextField[] currentHealths;
    private final JTextField[] initiatives;
    private final JTextField[] initiativesEnemies;

    private final ArrayList<Combatant> friendlies;
    private final ArrayList<Combatant> enemies;

    private final boolean isNeedsHpCur;

    public FinalizeCombatantsPopup(boolean isNeedsHpCur) {
        this.isNeedsHpCur = isNeedsHpCur;

        setTitle("Finalize Combat Information");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setAlwaysOnTop(true);
        setLayout(new GridLayout(0, 3));

        add(new JLabel("Player"));
        add(new JLabel("Initiative Roll"));
        if (isNeedsHpCur) {
            add(new JLabel("Current HP"));
        } else {
            add(new JLabel());
        }

        friendlies = Main.battle.friendlies();
        enemies = Main.battle.enemies();

        currentHealths = new JTextField[friendlies.size()];
        initiatives = new JTextField[friendlies.size()];
        initiativesEnemies = new JTextField[enemies.size()];

        addDivider();

        for (int i = 0; i < friendlies.size(); i++) {
            JTextField initiativeField = new JTextField();
            initiatives[i] = initiativeField;
            initiativeField.putClientProperty("JComponent.roundRect", true);
            initiativeField.addKeyListener(new DieRollListener(20, initiativeField));

            JTextField currentHealthField = new JTextField();
            currentHealths[i] = currentHealthField;
            currentHealthField.putClientProperty("JComponent.roundRect", true);
            currentHealthField.addKeyListener(new DieRollListener(friendlies.get(i).maxHp(), currentHealthField));

            add(new JLabel(friendlies.get(i).name()));
            add(initiativeField);
            if (isNeedsHpCur) {
                add(currentHealthField);
            } else {
                add(new JLabel());
            }
        }

        addDivider();

        for (int i = 0; i < enemies.size(); i++) {
            JTextField initiativeField = new JTextField();
            initiativesEnemies[i] = initiativeField;
            initiativeField.putClientProperty("JComponent.roundRect", true);
            initiativeField.addKeyListener(new DieRollListener(20, initiativeField));

            add(new JLabel(enemies.get(i).name()));
            add(initiativeField);
            add(new JLabel());
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
                if (isNeedsHpCur) {
                    int updatedHealth = Integer.parseInt(currentHealths[i].getText());
                    friendlies.get(i).setHealth(updatedHealth);
                }
                int initiative = Integer.parseInt(initiatives[i].getText());
                friendlies.get(i).setInitiative(initiative);
            }
            for (int i = 0; i < enemies.size(); i++) {
                int initiative = Integer.parseInt(initiativesEnemies[i].getText());
                enemies.get(i).setInitiative(initiative);
            }
            dispose();
            Main.start();
        });

        return button;
    }

    private void addDivider() {
        for (int i = 0; i < 3; i++) {
            add(new JSeparator());
        }
    }

}