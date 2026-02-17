package gui.popup.damage;

import combat.Main;
import combatants.Combatant;
import damage.Effect;
import damage.Spell;
import combatants.Stats;
import damage.Weapon;
import gui.listener.DieRollListener;
import util.DeadEndMessage;
import util.Locators;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Objects;

public class DamagePromptPopup extends JFrame {

    private JPanel activeSpellPanel;
    private JPanel spellPanelContainer;

    private enum SpellPanel {MANUAL, HIT, SAVE}
    private SpellPanel activeSpellPanelName = SpellPanel.HIT;

    private JComboBox<String> targetComboBoxWeapon;
    private JComboBox<String> targetComboBoxSpell;

    private JComboBox<Weapon> weaponComboBox;
    private JComboBox<Spell> spellComboBox;

    private JTextField weaponInputField;
    private JTextField spellInputFieldHit;
    private JTextField spellInputFieldSave;
    private JCheckBox manualSaveSpellHitBox;

    private final ArrayList<Combatant> targetList;
    private final Combatant currentCombatant;

    public DamagePromptPopup() {
        currentCombatant = Main.queue.getCurrentCombatant();

        setTitle("Enter Attack Information");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new GridLayout(0, 1));

        targetList = Locators.getTargetList(true);

        add(getMainPanel());

        pack();
        setLocationRelativeTo(null);
    }

    private JTabbedPane getMainPanel() {
        JTabbedPane panel = new JTabbedPane();
        panel.setTabPlacement(SwingConstants.TOP);

        panel.addTab("Weapon", getWeaponPanel());
        panel.addTab("Spell", getSpellPanel());

        return panel;
    }

    private JPanel getWeaponPanel() {
        JPanel panel = new JPanel(new GridLayout(0, 1));

        weaponInputField = getInputField();
        targetComboBoxWeapon = getTargetComboBox();
        weaponComboBox = getWeaponBox();

        String hitString = "Roll to Hit";
        if (currentCombatant.isPoisoned()) {
            hitString += " (with Disadvantage)";
        }
        hitString += ":";

        panel.add(new JLabel("Target:"));
        panel.add(targetComboBoxWeapon);
        panel.add(new JLabel("Weapon:"));
        panel.add(weaponComboBox);
        panel.add(new JLabel(hitString));
        panel.add(weaponInputField);
        panel.add(getOkButton(true));

        return panel;
    }

    private JComboBox<Weapon> getWeaponBox() {
        JComboBox<Weapon> box = new JComboBox<>();
        box.putClientProperty("JComponent.roundRect", true);

        if (currentCombatant.hasWeapons()) {
            for (Weapon weapon : currentCombatant.weapons()) {
                box.addItem(weapon);
            }
        }

        box.addItem(Weapon.MANUAL);
        return box;
    }

    private JPanel getSpellPanel() {
        JPanel panel = new JPanel(new GridLayout(0, 1));

        spellPanelContainer = new JPanel(new BorderLayout()); // NEW container

        targetComboBoxSpell = getTargetComboBox();
        spellComboBox = getSpellBox();

        panel.add(new JLabel("Target: "));
        panel.add(targetComboBoxSpell);
        panel.add(new JLabel("Spell:"));
        panel.add(spellComboBox);

        activeSpellPanel = getSpellPanelHit(); // DEFAULT PANEL
        spellPanelContainer.add(activeSpellPanel);

        panel.add(spellPanelContainer);
        panel.add(getOkButton(false));

        return panel;
    }

    private JComboBox<Spell> getSpellBox() {
        JComboBox<Spell> box = new JComboBox<>();
        box.putClientProperty("JComponent.roundRect", true);

        if (currentCombatant.hasSpells()) {
            for (Spell spell : currentCombatant.spells()) {
                box.addItem(spell);
            }
        }

        box.addItem(Spell.MANUAL_SAVE);
        box.addItem(Spell.MANUAL_HIT);

        box.addActionListener(e -> switchSpellPanel((Spell) box.getSelectedItem()));

        return box;
    }

    private void switchSpellPanel(Spell selected) {
        if (selected == null) return;

        spellPanelContainer.removeAll();

        if (selected.hasSave() || selected.equals(Spell.MANUAL_HIT)) {
            activeSpellPanel = getSpellPanelSave();
            activeSpellPanelName = SpellPanel.SAVE;
        } else if (selected.isManual()) {
            activeSpellPanel = getSpellPanelManual();
            activeSpellPanelName = SpellPanel.MANUAL;
        } else {
            activeSpellPanel = getSpellPanelHit();
            activeSpellPanelName = SpellPanel.HIT;
        }

        spellPanelContainer.add(activeSpellPanel);
        spellPanelContainer.revalidate();
        spellPanelContainer.repaint();
    }

    private JPanel getSpellPanelHit() {
        JPanel hitPanel = new JPanel(new GridLayout(0, 1));
        spellInputFieldHit = getInputField();
        hitPanel.add(new JLabel("Roll to Hit:"));
        hitPanel.add(spellInputFieldHit);
        return hitPanel;
    }

    private JPanel getSpellPanelSave() {
        JPanel savePanel = new JPanel(new GridLayout(0, 1));
        spellInputFieldSave = getInputField();
        savePanel.add(new JLabel("Opponent Save Roll:"));
        savePanel.add(spellInputFieldSave);
        return savePanel;
    }

    private JPanel getSpellPanelManual() {
        JPanel manualPanel = new JPanel();
        manualSaveSpellHitBox = new JCheckBox("Spell Hits?");
        manualPanel.add(manualSaveSpellHitBox);
        return manualPanel;
    }

    private JTextField getInputField() {
        JTextField field = new JTextField();
        field.putClientProperty("JComponent.roundRect", true);
        field.addKeyListener(new DieRollListener(20, field));
        return field;
    }

    private JComboBox<String> getTargetComboBox() {
        JComboBox<String> comboBox = new JComboBox<>();
        comboBox.putClientProperty("JComponent.roundRect", true);
        targetList.forEach(target -> comboBox.addItem(target.name()));
        return comboBox;
    }

    private JButton getOkButton(boolean isWithWeapon) {
        JButton button = new JButton("Attack");
        button.putClientProperty("JButton.buttonType", "roundRect");

        button.addActionListener(e -> {

            JComboBox<String> targetComboBox =
                    isWithWeapon ? targetComboBoxWeapon : targetComboBoxSpell;

            Combatant target = Locators.getCombatantWithNameFrom(
                    targetList,
                    (String) targetComboBox.getSelectedItem()
            );

            if (target == null) {
                JOptionPane.showMessageDialog(Main.menu,
                        "Select a target.",
                        Main.TITLE,
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (isWithWeapon) {
                int hitRoll = Integer.parseInt(weaponInputField.getText());
                registerAttack(target, hitRoll >= target.ac(), (Weapon) weaponComboBox.getSelectedItem());
            } else {
                Spell spell = (Spell) spellComboBox.getSelectedItem();
                switch (activeSpellPanelName) {
                    case MANUAL ->
                            registerAttack(target, manualSaveSpellHitBox.isSelected(), spell);

                    case HIT -> {
                        int hitRoll = Integer.parseInt(spellInputFieldHit.getText());
                        registerAttack(target, hitRoll >= target.ac(), spell);
                    }

                    case SAVE -> {
                        Stats.stat saveType =
                                ((Spell) Objects.requireNonNull(spellComboBox.getSelectedItem())).getSaveType();
                        int saveDC = 8 +
                                currentCombatant.stats().spellMod() +
                                currentCombatant.stats().mod(saveType);

                        int opponentRoll =
                                Integer.parseInt(spellInputFieldSave.getText());

                        registerAttack(target, opponentRoll < saveDC, spell);
                    }
                }
            }

            dispose();
            Main.menu.update();
            Main.checkWinConditions();
        });

        return button;
    }

    private void registerAttack(Combatant target, boolean success, Weapon weapon) {
        if (success) {
            new DamageAmountPopup(weapon, target).setVisible(true);
        } else {
            DeadEndMessage.informAttackFail();
        }
    }

    private void registerAttack(Combatant target, boolean success, Spell spell) {
        if (success) {
            if (spell.equals(Spell.HEX)) {
                Main.queue.getCurrentCombatant().putEffect(target, Effect.BONUS_DAMAGE);
                DeadEndMessage.informHexSuccess(target);
            } else {
                new DamageAmountPopup(spell, target, false).setVisible(true);
            }
        } else if (spell.dealsHalfDamageAnyways()) {
            new DamageAmountPopup(spell, target, true).setVisible(true);
        } else {
            DeadEndMessage.informAttackFail();
        }
    }
}