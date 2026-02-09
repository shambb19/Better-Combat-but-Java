package gui.popup;

import combat.Main;
import combatants.Combatant;
import gui.IntegerFieldListener;
import util.InputPrompts;
import util.Locators;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class DamagePromptPopup extends JFrame {

    private final JComboBox<String> targetComboBox;
    private final JTextField hitRollInputField;

    private final ArrayList<Combatant> targetList;

    public DamagePromptPopup() {
        setTitle("Enter Attack Information");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new GridLayout(0, 1));

        targetList = Locators.getTargetList(true);

        targetComboBox = new JComboBox<>();
        targetComboBox.putClientProperty("JComponent.roundRect", true);
        targetList.forEach(target -> targetComboBox.addItem(target.getName()));

        hitRollInputField = new JTextField();
        hitRollInputField.putClientProperty("JComponent.roundRect", true);
        hitRollInputField.addKeyListener(new IntegerFieldListener());

        add(new JLabel("Select Attack Target"));
        add(targetComboBox);
        add(new JLabel("Roll to Hit"));
        add(hitRollInputField);
        add(getOkButton());

        pack();
        setLocationRelativeTo(null);
    }

    private JButton getOkButton() {
        JButton button = new JButton("Attack");
        button.putClientProperty("JButton.buttonType", "roundRect");
        button.addActionListener(e -> {
            Combatant target = Locators.getCombatantWithNameFrom(targetList, (String) targetComboBox.getSelectedItem());
            if (target == null) {
                JOptionPane.showMessageDialog(Main.menu, "Select a target.", "Better Combat but Java", JOptionPane.WARNING_MESSAGE);
                return;
            }

            int hitRoll = Integer.parseInt(hitRollInputField.getText());
            if (hitRoll < target.getArmorClass()) {
                JOptionPane.showMessageDialog(Main.menu, "The attack does not hit.", "Better Combat but Java", JOptionPane.INFORMATION_MESSAGE);
            } else {
                target.damage(InputPrompts.promptHealth(true));
                Main.menu.update();
            }
            dispose();
        });
        return button;
    }

}
