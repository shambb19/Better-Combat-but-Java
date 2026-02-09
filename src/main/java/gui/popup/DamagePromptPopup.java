package gui.popup;

import combat.Main;
import combatants.Combatant;
import gui.popup.listener.DieRollListener;
import util.InputPrompts;
import util.Locators;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class DamagePromptPopup extends JFrame {

    private JComboBox<String> targetComboBoxHit;
    private JComboBox<String> targetComboBoxSave;

    private JTextField hitRollInputField;
    private JTextField saveDcInputField;

    private final ArrayList<Combatant> targetList;

    public DamagePromptPopup() {
        setTitle("Enter Attack Information");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new GridLayout(0, 1));

        targetList = Locators.getTargetList(true);

        add(getMainPanel());

        pack();
        setLocationRelativeTo(null);
    }

    private JTabbedPane getMainPanel() {
        JTabbedPane panel = new JTabbedPane();
        panel.setTabPlacement(SwingConstants.TOP);

        panel.addTab("Hit Roll", getHitRollPanel());
        panel.addTab("Save DC", getSavingThrowPanel());
        return panel;
    }

    private JPanel getHitRollPanel() {
        JPanel panel = new JPanel(new GridLayout(0, 1));
        hitRollInputField = getInputField();
        targetComboBoxHit = getComboBox();

        panel.add(new JLabel("Target:"));
        panel.add(targetComboBoxHit);
        panel.add(new JLabel("Roll to Hit:"));
        panel.add(hitRollInputField);
        panel.add(getOkButton(true));
        return panel;
    }

    private JPanel getSavingThrowPanel() {
        JPanel panel = new JPanel(new GridLayout(0, 1));
        saveDcInputField = getInputField();
        targetComboBoxSave = getComboBox();

        panel.add(new JLabel("Target: "));
        panel.add(targetComboBoxSave);
        panel.add(new JLabel("Attack Save DC:"));
        panel.add(saveDcInputField);
        panel.add(getOkButton(false));
        return panel;
    }

    private JTextField getInputField() {
        JTextField field = new JTextField();
        field.putClientProperty("JComponent.roundRect", true);
        field.addKeyListener(new DieRollListener(20, field));
        return field;
    }

    private JComboBox<String> getComboBox() {
        JComboBox<String> comboBox = new JComboBox<>();
        comboBox.putClientProperty("JComponent.roundRect", true);
        targetList.forEach(target -> comboBox.addItem(target.getName()));
        return comboBox;
    }

    private JButton getOkButton(boolean isHitRoll) {
        JButton button = new JButton("Attack");
        button.putClientProperty("JButton.buttonType", "roundRect");
        button.addActionListener(e -> {
            JComboBox<String> targetComboBox = targetComboBoxSave;
            if (isHitRoll) {
                targetComboBox = targetComboBoxHit;
            }
            Combatant target = Locators.getCombatantWithNameFrom(targetList, (String) targetComboBox.getSelectedItem());
            if (target == null) {
                JOptionPane.showMessageDialog(Main.menu, "Select a target.", Main.TITLE, JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (isHitRoll) {
                int hitRoll = Integer.parseInt(hitRollInputField.getText());
                registerAttack(target, hitRoll >= target.getArmorClass());
            } else {
                int saveDC = Integer.parseInt(saveDcInputField.getText());
                int saveRoll = InputPrompts.promptHealth("save roll");
                registerAttack(target, saveRoll >= saveDC);
            }
            Main.menu.update();
            dispose();
        });
        return button;
    }

    private void registerAttack(Combatant target, boolean isSuccessfulWhen) {
        if (isSuccessfulWhen) {
            target.damage(InputPrompts.promptHealth("damage amount"));
        } else {
            InputPrompts.informAttackFail();
        }
    }

}