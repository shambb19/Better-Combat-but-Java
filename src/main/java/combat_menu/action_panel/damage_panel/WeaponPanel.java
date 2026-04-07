package combat_menu.action_panel.damage_panel;

import __main.EncounterInfo;
import __main.Main;
import character_info.combatant.Combatant;
import character_info.combatant.PC;
import combat_menu.action_panel.ActionPanel;
import damage_implements.Weapon;
import format.ColorStyle;
import format.swing_comp.SwingComp;
import format.swing_comp.SwingPane;

import javax.swing.*;
import java.util.ArrayList;

import static _global_list.DamageImplements.MANUAL_WEAPON;
import static util.Message.informAttackFail;

public class WeaponPanel extends JPanel {

    private final ActionPanel root;

    private final Combatant attacker;

    private final JComboBox<Combatant> targetBox;
    private final JComboBox<Weapon> weaponsBox;
    private final JTextField rollInputField;

    public static WeaponPanel newInstance(JComboBox<Combatant> targetBox, ActionPanel root) {
        return new WeaponPanel(targetBox, root);
    }

    private WeaponPanel(JComboBox<Combatant> targetBox, ActionPanel root) {
        this.root = root;

        attacker = EncounterInfo.getCurrentCombatant();

        ArrayList<Weapon> weapons = new ArrayList<>();
        if (attacker instanceof PC pc)
            weapons.addAll(pc.weapons());

        this.targetBox = targetBox;
        weaponsBox = SwingComp.comboBox(weapons, MANUAL_WEAPON).unselected().build();

        String hitString = "Roll to Hit";
        if (EncounterInfo.getCurrentCombatant().isPoisoned())
            hitString += " (With Disadvantage)";
        hitString += ":";

        rollInputField = SwingComp.field().forIntegersOnRange(1, 20).build();

        JPanel okCancelPanel = SwingComp.button("Use Weapon", this::logAndContinue)
                .withBackground(ColorStyle.DARKER_RED.getColor())
                .withCancelOption(root::returnToButtons)
                .build();

        SwingPane.modifiable(this).collect(
                "Select a Target:", targetBox,
                "Select a Weapon:", weaponsBox,
                hitString, rollInputField, okCancelPanel
        ).withLayout(SwingPane.ONE_COLUMN);
    }

    private void logAndContinue() {
        Weapon weapon = (Weapon) weaponsBox.getSelectedItem();
        Combatant target = (Combatant) targetBox.getSelectedItem();

        if (weapon == null || target == null) return;

        if (rollInputField.getText().isEmpty()) return;

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
        if (success)
            SwingUtilities.invokeLater(() -> root.promptDamageAmount(weapon, target));
        else
            informAttackFail();
    }

}
