package combat_menu.popup.damage;

import __main.CombatMain;
import character_info.combatant.Combatant;
import character_info.combatant.PC;
import combat_menu.listener.DieRollListener;
import damage_implements.Weapon;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

import static _global_list.DamageImplements.MANUAL_WEAPON;
import static util.Message.informAttackFail;

public class WeaponPanel extends JPanel {

    private final JFrame root;

    private final Combatant attacker;
    private final ArrayList<Weapon> weapons;

    private final JComboBox<Combatant> targetBox;
    private final JComboBox<Weapon> weaponsBox;
    private final JTextField rollInputField;

    public static WeaponPanel get(JComboBox<Combatant> targetBox, JFrame root) {
        return new WeaponPanel(targetBox, root);
    }

    private WeaponPanel(JComboBox<Combatant> targetBox, JFrame root) {
        this.root = root;

        attacker = CombatMain.QUEUE.getCurrentCombatant();

        weapons = new ArrayList<>();
        if (attacker instanceof PC pc) {
            weapons.addAll(pc.weapons());
        }

        this.targetBox = targetBox;
        weaponsBox = getWeaponComboBox();

        String hitString = "Roll to Hit";
        if (CombatMain.QUEUE.getCurrentCombatant().isPoisoned()) {
            hitString += " (With Disadvantage)";
        }
        hitString += ":";

        rollInputField = new JTextField();
        rollInputField.addKeyListener(new DieRollListener(1, 20, rollInputField));

        JButton okButton = new JButton("Confirm");
        okButton.putClientProperty("JButton.buttonType", "roundRect");
        okButton.addActionListener(e -> logAndContinue());

        setLayout(new GridLayout(0, 1));

        add(new JLabel("Select a Target:"));
        add(targetBox);
        add(new JLabel("Select a Weapon:"));
        add(weaponsBox);
        add(new JLabel(hitString));
        add(rollInputField);
        add(okButton);
    }

    private JComboBox<Weapon> getWeaponComboBox() {
        JComboBox<Weapon> box = new JComboBox<>();
        box.putClientProperty("JComponent.roundRect", true);
        weapons.forEach(box::addItem);
        box.addItem(MANUAL_WEAPON);
        return box;
    }

    private void logAndContinue() {
        Weapon weapon = (Weapon) weaponsBox.getSelectedItem();
        Combatant target = (Combatant) targetBox.getSelectedItem();

        if (weapon == null || target == null) {
            return;
        }

        if (rollInputField.getText().isEmpty()) {
            return;
        }
        int fieldVal = Integer.parseInt(rollInputField.getText());

        int hitRoll = fieldVal + attacker.attackBonus(weapon);

        registerAttack(target, hitRoll >= target.ac(), weapon);
        root.dispose();
        CombatMain.COMBAT_MENU.update();
    }

    /**
     * On a successful attack, prompts a damage popup and updates the menu accordingly.
     * On a failed hit, prompts a dialog that informs the user of such.
     * @param target the targeted combatant
     * @param success the conditions required for an attack to be successful; parameter
     *                entered as a condition (i.e. roll>=ac), not raw boolean.
     * @param weapon the weapon used
     */
    private void registerAttack(Combatant target, boolean success, Weapon weapon) {
        if (success) {
            DamageInputPopup.run(weapon, target);
        } else {
            informAttackFail();
        }
    }

}
