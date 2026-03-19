package combat_menu.action_panel;

import __main.EncounterInfo;
import __main.Main;
import character_info.combatant.Combatant;
import character_info.combatant.PC;
import combat_menu.listener.DieRollListener;
import damage_implements.Weapon;
import format.ColorStyle;
import format.SwingStyles;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

import static _global_list.DamageImplements.MANUAL_WEAPON;
import static util.Message.informAttackFail;

public class WeaponPanel extends JPanel {

    private final ActionPanel root;

    private final Combatant attacker;
    private final ArrayList<Weapon> weapons;

    private final JComboBox<Combatant> targetBox;
    private final JComboBox<Weapon> weaponsBox;
    private final JTextField rollInputField;
    private final DieRollListener listener;

    public static WeaponPanel newInstance(JComboBox<Combatant> targetBox, ActionPanel root) {
        return new WeaponPanel(targetBox, root);
    }

    private WeaponPanel(JComboBox<Combatant> targetBox, ActionPanel root) {
        this.root = root;

        attacker = EncounterInfo.getCurrentCombatant();

        weapons = new ArrayList<>();
        if (attacker instanceof PC pc) {
            weapons.addAll(pc.weapons());
        }

        this.targetBox = targetBox;
        weaponsBox = getWeaponComboBox();

        String hitString = "Roll to Hit";
        if (EncounterInfo.getCurrentCombatant().isPoisoned()) {
            hitString += " (With Disadvantage)";
        }
        hitString += ":";

        rollInputField = new JTextField();
        listener = new DieRollListener(1, 20, rollInputField);
        rollInputField.addKeyListener(listener);

        JButton okButton = new JButton("Attack");
        okButton.setBackground(ColorStyle.DARKER_RED.getColor());

        JPanel okCancelPanel = SwingStyles.getConfirmCancelPanel(
                okButton,
                e -> logAndContinue(),
                e -> root.returnToButtons()
        );

        setLayout(new GridLayout(0, 1));

        SwingStyles.addComponents(this,
                new JLabel("Select a Target:"), targetBox,
                new JLabel("Select a Weapon:"), weaponsBox,
                new JLabel(hitString), rollInputField, okCancelPanel
        );
    }

    private JComboBox<Weapon> getWeaponComboBox() {
        JComboBox<Weapon> box = new JComboBox<>();
        weapons.forEach(box::addItem);
        box.addItem(MANUAL_WEAPON);
        box.setSelectedIndex(-1);
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
        root.returnToButtons();
        Main.logAction();
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
            SwingUtilities.invokeLater(() -> root.promptDamageAmount(weapon, target));
        } else {
            informAttackFail();
        }
    }

    public void reset() {
        targetBox.setSelectedIndex(-1);
        weaponsBox.setSelectedIndex(-1);
        rollInputField.setText("");
        rollInputField.removeKeyListener(listener);
        rollInputField.addKeyListener(listener);
    }

}
