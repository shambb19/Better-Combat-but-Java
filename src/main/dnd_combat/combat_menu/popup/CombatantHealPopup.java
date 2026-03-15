package combat_menu.popup;

import __main.CombatMain;
import character_info.combatant.Combatant;
import combat_menu.listener.IntegerFieldListener;
import util.Locators;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class CombatantHealPopup extends JFrame {

    private final JComboBox<Combatant> targetComboBox;
    private final JTextField healAmountInputField;
    private final JCheckBox fullHealButton;

    private CombatantHealPopup() {
        setTitle("Enter Heal Information");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new GridLayout(0, 1));
        setAlwaysOnTop(true);

        List<Combatant> targetList = Locators.getTargetList(false);

        targetComboBox = new JComboBox<>();
        targetComboBox.putClientProperty("JComponent.roundRect", true);
        targetList.forEach(targetComboBox::addItem);

        healAmountInputField = new JTextField();
        healAmountInputField.putClientProperty("JComponent.roundRect", true);
        healAmountInputField.addKeyListener(new IntegerFieldListener());

        fullHealButton = new JCheckBox("To Full Health");
        fullHealButton.putClientProperty("JButton.buttonType", "roundRect");
        fullHealButton.addActionListener(e -> healAmountInputField.setEnabled(!fullHealButton.isSelected()));

        JButton okButton = new JButton("Heal");
        okButton.putClientProperty("JButton.buttonType", "roundRect");
        okButton.addActionListener(e -> logAndClose());

        add(new JLabel("Select Heal Target"));
        add(targetComboBox);
        add(new JLabel("Enter Heal Amount"));
        add(healAmountInputField);
        add(fullHealButton);
        add(okButton);

        pack();
        setLocationRelativeTo(CombatMain.COMBAT_MENU);
    }

    public static void run() {
        new CombatantHealPopup().setVisible(true);
    }

    private void logAndClose() {
        Combatant target = (Combatant) targetComboBox.getSelectedItem();

        if (target == null) {
            return;
        }

        if (fullHealButton.isSelected()) {
            target.healFull();
        } else {
            int healAmount = Integer.parseInt(healAmountInputField.getText());
            target.heal(healAmount);
        }

        CombatMain.COMBAT_MENU.update();
        dispose();
    }

}

