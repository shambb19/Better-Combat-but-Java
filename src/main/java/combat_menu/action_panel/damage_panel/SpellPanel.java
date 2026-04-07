package combat_menu.action_panel.damage_panel;

import __main.EncounterInfo;
import __main.Main;
import character_info.combatant.Combatant;
import character_info.combatant.PC;
import combat_menu.action_panel.ActionPanel;
import damage_implements.Spell;
import format.ColorStyle;
import format.swing_comp.SwingComp;
import format.swing_comp.SwingPane;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

import static _global_list.DamageImplements.MANUAL_HIT;
import static _global_list.DamageImplements.MANUAL_SAVE;
import static util.Message.informAttackFail;

public class SpellPanel extends JPanel {

    private final ActionPanel root;
    private final Combatant attacker;

    private final JComboBox<Combatant> targetBox;
    private final JComboBox<Spell> spellsBox;

    private final JPanel variablePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    private final JTextField rollField = new JTextField(5);
    private final JCheckBox manualSuccessBox = new JCheckBox("Attack Succeeds?");

    private SpellPanel(JComboBox<Combatant> targetBox, ActionPanel root) {
        this.root = root;
        this.attacker = EncounterInfo.getCurrentCombatant();
        this.targetBox = targetBox;

        ArrayList<Spell> spells = new ArrayList<>();
        if (attacker instanceof PC pc)
            spells.addAll(pc.spells());

        SwingComp.modifiable(rollField).forIntegersOnRange(1, 20);
        spellsBox = SwingComp.comboBox(spells, MANUAL_HIT, MANUAL_SAVE)
                .withAction(this::updateInputUI)
                .unselected()
                .build();

        JPanel okCancelPanel = SwingComp.button("Use Spell", this::logAndContinue)
                .withBackground(ColorStyle.DARKER_RED.getColor())
                .withCancelOption(root::returnToButtons)
                .build();

        SwingPane.modifiable(this).collect(
                "Select a Target", targetBox,
                "Select a Spell", spellsBox,
                variablePanel, okCancelPanel
        ).withLayout(SwingPane.ONE_COLUMN);

        updateInputUI();
    }

    public static SpellPanel newInstance(JComboBox<Combatant> targetBox, ActionPanel root) {
        return new SpellPanel(targetBox, root);
    }

    private void updateInputUI() {
        Spell selected = (Spell) spellsBox.getSelectedItem();
        if (selected == null) return;

        if (selected.equals(MANUAL_SAVE)) {
            SwingPane.modifiable(variablePanel).contentsReplacedWith(manualSuccessBox);
            variablePanel.add(manualSuccessBox);
        } else
            SwingPane.modifiable(variablePanel).contentsReplacedWith(
                    selected.hasSave() ? "Opponent Save Roll:" : "Roll to Hit:",
                    rollField
            );
    }

    private void logAndContinue() {
        Combatant target = (Combatant) targetBox.getSelectedItem();
        Spell spell = (Spell) spellsBox.getSelectedItem();

        if (target == null || spell == null) return;

        registerAttack(target, spell);
        root.returnToButtons();
        Main.logAction();
    }

    private void registerAttack(Combatant target, Spell spell) {
        if (succeeds() || spell.dealsHalfDamageAnyways())
            SwingUtilities.invokeLater(() -> root.promptDamageAmount(spell, target));
        else
            informAttackFail();
    }

    private boolean succeeds() {
        Combatant target = (Combatant) targetBox.getSelectedItem();
        Spell spell = (Spell) spellsBox.getSelectedItem();
        if (target == null || spell == null) return false;

        if (spell.equals(MANUAL_SAVE)) return manualSuccessBox.isSelected();

        int roll = parseRoll();
        if (spell.hasSave())
            return roll >= attacker.saveDc();
        else
            return (roll + attacker.spellAttackBonus()) >= target.ac();

    }

    private int parseRoll() {
        try {
            String text = rollField.getText().trim();
            return Integer.parseInt(text);
        } catch (NumberFormatException | NullPointerException e) {
            return 0;
        }
    }
}