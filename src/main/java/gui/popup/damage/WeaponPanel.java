package gui.popup.damage;

import combat.Main;
import combatants.Combatant;
import damage.Weapon;
import gui.listener.DieRollListener;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

import static util.Message.informAttackFail;

public class WeaponPanel extends JPanel {

    private final JFrame root;

    private final JComboBox<Combatant> targetBox;
    private final JComboBox<Weapon> weaponsBox;
    private final JTextField rollInputField;

    public WeaponPanel(JComboBox<Combatant> targetBox, Combatant currentCombatant, JFrame root) {
        this.root = root;

        this.targetBox = targetBox;
        weaponsBox = getWeaponComboBox(currentCombatant.weapons());

        String hitString = "Roll to Hit";
        if (Main.queue.getCurrentCombatant().isPoisoned()) {
            hitString += " (With Disadvantage)";
        }
        hitString += ":";
        rollInputField = new JTextField();
        rollInputField.addKeyListener(new DieRollListener(1, 20, rollInputField));

        setLayout(new GridLayout(0, 1));

        add(new JLabel("Select a Target:"));
        add(targetBox);
        add(new JLabel("Select a Weapon:"));
        add(weaponsBox);
        add(new JLabel(hitString));
        add(rollInputField);
        add(getOkButton());
    }

    private JComboBox<Weapon> getWeaponComboBox(ArrayList<Weapon> weapons) {
        JComboBox<Weapon> box = new JComboBox<>();
        box.putClientProperty("JComponent.roundRect", true);
        weapons.forEach(box::addItem);
        return box;
    }

    @SuppressWarnings("all")
    private JButton getOkButton() {
        JButton button = new JButton("Confirm");
        button.putClientProperty("JButton.buttonType", "roundRect");
        button.addActionListener(e -> {
            if (rollInputField.getText().isEmpty()) {
                return;
            }
            Combatant target = (Combatant) targetBox.getSelectedItem();
            int hitRoll = Integer.parseInt(rollInputField.getText());
            Weapon weapon = (Weapon) weaponsBox.getSelectedItem();
            registerAttack(target, hitRoll >= target.ac(), weapon);
            root.dispose();
            Main.menu.update();
        });
        return button;
    }

    private void registerAttack(Combatant target, boolean success, Weapon weapon) {
        if (success) {
            new DamageAmountPopup(weapon, target).setVisible(true);
            Main.queue.getCurrentCombatant().logHit();
        } else {
            informAttackFail();
        }
    }

}
