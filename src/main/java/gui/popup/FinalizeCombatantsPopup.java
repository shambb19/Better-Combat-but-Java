package gui.popup;

import combat.Main;
import combatants.Combatant;
import gui.listener.DieRollListener;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class FinalizeCombatantsPopup extends JFrame {

    JTextField[] currentHealths;
    JTextField[] initiatives;

    ArrayList<Combatant> friendlies;

    JButton okButton;

    public FinalizeCombatantsPopup() {
        setTitle("Finalize Combat Information");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setAlwaysOnTop(true);
        setLayout(new GridLayout(0, 3));

        add(new JLabel("Player"));
        add(new JLabel("Current HP"));
        add(new JLabel("Initiative Roll"));

        friendlies = Main.battle.friendlies();

        currentHealths = new JTextField[friendlies.size()];
        initiatives = new JTextField[friendlies.size()];

        for (int i = 0; i < friendlies.size(); i++) {
            add(new JLabel(friendlies.get(i).name()));

            JTextField currentHealthField = new JTextField();
            currentHealths[i] = currentHealthField;
            currentHealthField.putClientProperty("JComponent.roundRect", true);
            currentHealthField.addKeyListener(new DieRollListener(friendlies.get(i).maxHp(), currentHealthField));
            currentHealthField.addActionListener(e -> checkAllBoxesFilled(currentHealths));

            JTextField initiativeField = new JTextField();
            initiatives[i] = initiativeField;
            initiativeField.putClientProperty("JComponent.roundRect", true);
            initiativeField.addKeyListener(new DieRollListener(20, initiativeField));
            initiativeField.addActionListener(e -> checkAllBoxesFilled(initiatives));

            add(currentHealthField);
            add(initiativeField);
        }

        okButton = createOkButton();
        add(okButton);

        pack();
        setLocationRelativeTo(null);
    }

    private JButton createOkButton() {
        JButton button = new JButton("Confirm");
        button.putClientProperty("JButton.buttonType", "roundRect");

        button.addActionListener(e -> {
            for (int i = 0; i < friendlies.size(); i++) {
                int updatedHealth = Integer.parseInt(currentHealths[i].getText());
                int initiative = Integer.parseInt(initiatives[i].getText());
                friendlies.get(i).setHealth(updatedHealth);
                friendlies.get(i).setInitiative(initiative);
            }
            new FinalizeEnemiesPopup().setVisible(true);
            dispose();
        });

        return button;
    }

    private void checkAllBoxesFilled(JTextField[] checkedFields) {
        boolean allBoxesCorrectlyFilled = true;
        for (JTextField field : checkedFields) {
            if (field.getText().isEmpty()) {
                allBoxesCorrectlyFilled = false;
            }
            try {
                Integer.parseInt(field.getText());
            } catch (Exception ignored) {
                allBoxesCorrectlyFilled = false;
            }
        }
        okButton.setEnabled(allBoxesCorrectlyFilled);
    }

    class FinalizeEnemiesPopup extends JFrame {

        ArrayList<Combatant> enemies;

        JTextField[] initiativesEnemies;

        JButton okButtonEnemies;

        public FinalizeEnemiesPopup() {
            setTitle("Finalize Combat Information");
            setDefaultCloseOperation(DISPOSE_ON_CLOSE);
            setAlwaysOnTop(true);
            setLayout(new GridLayout(0, 2));

            add(new JLabel("Enemy"));
            add(new JLabel("Initiative Roll"));

            enemies = Main.battle.enemies();

            initiativesEnemies = new JTextField[enemies.size()];

            for (int i = 0; i < enemies.size(); i++) {
                add(new JLabel(enemies.get(i).name()));

                JTextField initiativeField = new JTextField();
                initiativesEnemies[i] = initiativeField;
                initiativeField.putClientProperty("JComponent.roundRect", true);
                initiativeField.addKeyListener(new DieRollListener(20, initiativeField));
                initiativeField.addActionListener(e -> checkAllBoxesFilled(initiativesEnemies));

                add(initiativeField);
            }

            okButtonEnemies = createEnemyOKButton();
            add(okButtonEnemies);

            pack();
            setLocationRelativeTo(null);
        }

        private JButton createEnemyOKButton() {
            JButton button = new JButton("Confirm");
            button.putClientProperty("JButton.buttonType", "roundRect");

            button.addActionListener(e -> {
                for (int i = 0; i < enemies.size(); i++) {
                    int initiative = Integer.parseInt(initiativesEnemies[i].getText());
                    enemies.get(i).setInitiative(initiative);
                }
                Main.start();
                dispose();
            });

            return button;
        }

    }

}