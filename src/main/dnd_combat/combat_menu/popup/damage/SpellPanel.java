package combat_menu.popup.damage;

import character_info.Stat;
import damage_implements.Spell;
import damage_implements.Spells;
import main.CombatMain;
import character_info.Combatant;
import damage_implements.Effect;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static util.Message.informAttackFail;

public class SpellPanel extends JPanel {

    private final JFrame root;

    private final Combatant attacker;

    private final JComboBox<Combatant> targetBox;
    private final JComboBox<Spell> spellsBox;

    private enum spellPanel {HIT, SAVE, MANUAL_SAVE}
    private spellPanel activePanelType;
    private final JPanel variablePanel = new JPanel(new GridLayout(0, 1));
    private final Map<spellPanel, JComponent> attackComponents = new HashMap<>();

    private int saveRoll;
    private int hitRoll;

    public SpellPanel(JComboBox<Combatant> targetBox, Combatant currentCombatant, JFrame root) {
        this.root = root;

        this.attacker = currentCombatant;

        putComponent(spellPanel.HIT, "Roll to Hit:");
        putComponent(spellPanel.SAVE, "Opponent Save Roll:");
        putManualSaveComponent();

        this.targetBox = targetBox;
        spellsBox = getSpellBox(currentCombatant.spells());

        setLayout(new GridLayout(0, 1));

        add(new JLabel("Select a Target:"));
        add(targetBox);
        add(new JLabel("Select a Spell:"));
        add(spellsBox);
        add(variablePanel);
        add(getOkButton());
    }

    private JComboBox<Spell> getSpellBox(ArrayList<Spell> spells) {
        JComboBox<Spell> box = new JComboBox<>();
        box.putClientProperty("JComponent.roundRect", "true");
        spells.forEach(box::addItem);
        box.addItem(Spells.MANUAL_HIT);
        box.addItem(Spells.MANUAL_SAVE);
        box.addActionListener(e -> logSpellChange(box));

        logSpellChange(box);
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
        if (selected.equals(Spells.MANUAL_SAVE)) {
            variablePanel.add(attackComponents.get(spellPanel.MANUAL_SAVE));
            activePanelType = spellPanel.MANUAL_SAVE;
        } else if (selected.hasSave()) {
            variablePanel.add(attackComponents.get(spellPanel.SAVE));
            activePanelType = spellPanel.SAVE;
        } else {
            variablePanel.add(attackComponents.get(spellPanel.HIT));
            activePanelType = spellPanel.MANUAL_SAVE.HIT;
        }
    }

    /**
     * Logic to switch to the component specified in logSpellChange
     */
    private void putComponent(spellPanel spellPanel, String labelText) {
        JPanel panel = new JPanel(new FlowLayout());

        JTextField field = new JTextField();
        field.putClientProperty("JComponent.roundRect", true);
        field.addActionListener(e -> {
            switch (spellPanel) {
                case SAVE -> saveRoll = Integer.parseInt(field.getText());
                case HIT -> hitRoll = Integer.parseInt(field.getText());
            }
        });

        panel.add(new JLabel(labelText));
        panel.add(field);
        attackComponents.put(spellPanel, panel);
        root.pack();
    }

    private void putManualSaveComponent() {
        attackComponents.put(spellPanel.MANUAL_SAVE, new JCheckBox("Attack Succeeds?"));
    }

    /**
     * Button includes logic for compiling all information necessary to determine
     * attack success and then passing it to the registerAttack method.
     * @return the completed ok button
     */
    @SuppressWarnings("all")
    private JButton getOkButton() {
        JButton button = new JButton("Confirm");
        button.putClientProperty("JButton.buttonType", "roundRect");
        button.addActionListener(e -> {
            Combatant target = (Combatant) targetBox.getSelectedItem();
            Spell spell = (Spell) spellsBox.getSelectedItem();
            switch (activePanelType) {
                case HIT -> {
                    int attackVal = hitRoll + attacker.stats().spellAttackBonus();
                    registerAttack(target, attackVal >= target.ac(), spell);
                }
                case SAVE -> {
                    Stat saveType = spell.savingThrow();
                    int saveDC = 8 +
                            attacker.stats().spellAttackBonus() +
                            attacker.stats().mod(saveType);

                    registerAttack(target, saveRoll < saveDC, spell);
                }
                case MANUAL_SAVE -> {
                    boolean succeeds = ((JCheckBox) attackComponents.get(spellPanel.MANUAL_SAVE)).isSelected();
                    registerAttack(target, succeeds, spell);
                }
            }
            root.dispose();
            CombatMain.COMBAT_MENU.update();
        });
        return button;
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
        if (success) {
            if (spell.effect().equals(Effect.BONUS_DAMAGE)) {
                CombatMain.QUEUE.getCurrentCombatant().putEffect(target, Effect.BONUS_DAMAGE);
            } else {
                new DamageAmountPopup(spell, target, false).setVisible(true);
            }
            return;
        }

        if (spell.dealsHalfDamageAnyways()) {
            new DamageAmountPopup(spell, target, true).setVisible(true);
        } else {
            informAttackFail();
        }
    }

}
