package combat_menu.action_panel;

import __main.CombatMain;
import character_info.combatant.Combatant;
import character_info.combatant.PC;
import combat_menu.listener.DieRollListener;
import damage_implements.Spell;
import format.ColorStyle;
import format.SwingStyles;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static _global_list.DamageImplements.MANUAL_HIT;
import static _global_list.DamageImplements.MANUAL_SAVE;
import static util.Message.informAttackFail;

public class SpellPanel extends JPanel {

    private final ActionPanel root;

    private final Combatant attacker;
    private final ArrayList<Spell> spells;

    private final JComboBox<Combatant> targetBox;
    private final JComboBox<Spell> spellsBox;

    private enum spellPanel {HIT, SAVE, MANUAL_SAVE}
    private spellPanel activePanelType;
    private final JPanel variablePanel = new JPanel(new GridLayout(0, 1));
    private final Map<spellPanel, JComponent> attackComponents = new HashMap<>();
    private final Map<JComponent, JTextField> attackComponentInputFields = new HashMap<>();

    public static SpellPanel newInstance(JComboBox<Combatant> targetBox, ActionPanel root) {
        return new SpellPanel(targetBox, root);
    }

    private SpellPanel(JComboBox<Combatant> targetBox, ActionPanel root) {
        this.root = root;

        this.attacker = CombatMain.getCurrentCombatant();

        spells = new ArrayList<>();
        if (attacker instanceof PC pc) {
            spells.addAll(pc.spells());
        }

        putComponent(spellPanel.HIT, "Roll to Hit:");
        putComponent(spellPanel.SAVE, "Opponent Save Roll:");
        putManualSaveComponent();

        this.targetBox = targetBox;
        spellsBox = getSpellBox();

        JButton okButton = new JButton("Confirm");
        okButton.setBackground(ColorStyle.DARKER_RED.getColor());

        JPanel okCancelPanel = SwingStyles.getConfirmCancelPanel(
                okButton,
                e -> logAndContinue(),
                e -> root.returnToButtons()
        );

        setLayout(new GridLayout(0, 1));

        add(new JLabel("Select a Target:"));
        add(targetBox);
        add(new JLabel("Select a Spell:"));
        add(spellsBox);
        add(variablePanel);
        add(okCancelPanel);
    }

    private JComboBox<Spell> getSpellBox() {
        JComboBox<Spell> box = new JComboBox<>();
        spells.forEach(box::addItem);
        box.addItem(MANUAL_HIT);
        box.addItem(MANUAL_SAVE);
        box.addActionListener(e -> logSpellChange(box));

        return box;
    }

    /**
     * Switches the present method of inputting attack success based on the spell
     * selected in the JComboBox param. For example, selecting a spell that has
     * a save DC will switch to the dialog for entering a saving throw, and a spell
     * with a hit throw will switch to that dialog. Spells not added into the system
     * that have a saving throw will simply have a JCheckBox because when the players
     * roll it, they will already know if the attack succeeds, so there is no point
     * entering that into the program.
     */
    @SuppressWarnings("all")
    private void logSpellChange(JComboBox<Spell> box) {
        variablePanel.removeAll();
        Spell selected = (Spell) box.getSelectedItem();
        if (selected == null) {
            return;
        }

        if (selected.equals(MANUAL_SAVE)) {
            variablePanel.add(attackComponents.get(spellPanel.MANUAL_SAVE));
            activePanelType = spellPanel.MANUAL_SAVE;
        } else if (selected.hasSave()) {
            variablePanel.add(attackComponents.get(spellPanel.SAVE));
            activePanelType = spellPanel.SAVE;
        } else {
            variablePanel.add(attackComponents.get(spellPanel.HIT));
            activePanelType = spellPanel.HIT;
        }

        variablePanel.revalidate();
        variablePanel.repaint();
    }

    /**
     * Logic to switch to the component specified in logSpellChange
     */
    private void putComponent(spellPanel spellPanel, String labelText) {
        JPanel panel = new JPanel(new FlowLayout());

        JTextField field = new JTextField();
        field.addKeyListener(new DieRollListener(1, 20, field));

        panel.add(new JLabel(labelText));
        panel.add(field);

        attackComponents.put(spellPanel, panel);
        attackComponentInputFields.put(panel, field);
    }

    private void putManualSaveComponent() {
        attackComponents.put(spellPanel.MANUAL_SAVE, new JCheckBox("Attack Succeeds?"));
    }

    private void logAndContinue() {
        Combatant target = (Combatant) targetBox.getSelectedItem();
        Spell spell = (Spell) spellsBox.getSelectedItem();

        if (target == null || spell == null) {
            return;
        }

        JComponent activePanel = attackComponents.get(activePanelType);

        boolean successCondition;
        if (activePanelType.equals(spellPanel.MANUAL_SAVE)) {
            successCondition = ((JCheckBox) attackComponents.get(activePanelType)).isSelected();
        } else {
            JTextField activeField = attackComponentInputFields.get(activePanel);
            int roll = Integer.parseInt(activeField.getText());

            if (activePanelType.equals(spellPanel.HIT)) {
                int attackVal = roll + attacker.spellAttackBonus();
                successCondition = attackVal >= target.ac();
            } else {
                successCondition = roll < attacker.saveDc();
            }
        }

        registerAttack(target, successCondition, spell);
        root.returnToButtons();
        CombatMain.logAction();
    }

    /**
     * On a successful attack, prompts a damage popup, handles any remaining possible
     * spell effects, and updates the menu accordingly. On a failed attack, prompts
     * a dialog informing the user of such.
     * @param target the targeted combatant
     * @param success the conditions required for an attack to be successful; parameter
     *                entered as a condition (i.e. roll>=ac), not raw boolean.
     * @param spell the spell used
     */
    private void registerAttack(Combatant target, boolean success, Spell spell) {
        if (success || spell.dealsHalfDamageAnyways()) {
            SwingUtilities.invokeLater(() -> root.promptDamageAmount(spell, target));
        } else {
            informAttackFail();
        }
    }

    public void reset() {
        targetBox.setSelectedIndex(-1);
        spellsBox.setSelectedIndex(-1);
        attackComponentInputFields.forEach((comp, field) -> field.setText(""));
    }

}
