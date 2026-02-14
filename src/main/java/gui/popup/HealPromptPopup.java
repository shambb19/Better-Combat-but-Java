package gui.popup;

import combat.Main;
import combatants.Combatant;
import gui.listener.IntegerFieldListener;
import util.Locators;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class HealPromptPopup extends JFrame {

    private final JComboBox<String> targetComboBox;
    private final JTextField healAmountInputField;
    private final JCheckBox fullHealButton;

    private final ArrayList<Combatant> targetList;

    public HealPromptPopup() {
        setTitle("Enter Heal Information");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new GridLayout(0, 1));

        targetList = Locators.getTargetList(false);

        targetComboBox = new JComboBox<>();
        targetComboBox.putClientProperty("JComponent.roundRect", true);
        targetList.forEach(target -> targetComboBox.addItem(target.name()));

        healAmountInputField = new JTextField();
        healAmountInputField.putClientProperty("JComponent.roundRect", true);
        healAmountInputField.addKeyListener(new IntegerFieldListener());

        fullHealButton = new JCheckBox("To Full Health");
        fullHealButton.putClientProperty("JButton.buttonType", "roundRect");
        fullHealButton.addActionListener(e -> healAmountInputField.setEnabled(!fullHealButton.isSelected()));

        add(new JLabel("Select Heal Target"));
        add(targetComboBox);
        add(new JLabel("Enter Heal Amount"));
        add(healAmountInputField);
        add(fullHealButton);
        add(getOkButton());

        pack();
        setLocationRelativeTo(null);
    }

    private JButton getOkButton() {
        JButton button = new JButton("Heal");
        button.putClientProperty("JButton.buttonType", "roundRect");
        button.addActionListener(e -> {
            Combatant target = Locators.getCombatantWithNameFrom(targetList, (String) targetComboBox.getSelectedItem());
            if (target == null) {
                JOptionPane.showMessageDialog(Main.menu, "Select a target.", Main.TITLE, JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (fullHealButton.isSelected()) {
                target.heal(target.maxHp());
            } else {
                target.heal(Integer.parseInt(healAmountInputField.getText()));
            }
            Main.menu.update();
            dispose();
        });
        return button;
    }

}
