package gui.popup.damage;

import combat.Main;
import combatants.Combatant;
import damage.Spell;
import damage.Weapon;
import gui.listener.DieRollListener;
import gui.listener.IntegerFieldListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class DamageAmountPopup extends JFrame {

    private Weapon weapon;
    private Spell spell;
    private final boolean isManual;

    private Combatant attacker;
    private final Combatant target;

    private JTextField mainDamageField;
    private JCheckBox otherBonusDamageCheck;
    private JTextField otherBonusDamageField;
    private JButton okButton;

    public DamageAmountPopup(Weapon weapon, Combatant target) {
        this.weapon = weapon;
        isManual = weapon.isManual();
        this.target = target;
        construct();
    }

    public DamageAmountPopup(Spell spell, Combatant target) {
        this.spell = spell;
        isManual = spell.isManual();
        this.target = target;
        construct();
    }

    private void construct() {
        attacker = Main.queue.getCurrentCombatant();

        setTitle("Damage Amount");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new GridLayout(0, 1));

        String damageString;
        int numDice;
        int dieSize;
        if (weapon != null) {
            damageString = weapon.getDamageString();
            numDice = weapon.getNumDice();
            dieSize = weapon.getDieSize();
        } else {
            damageString = spell.getDamageString();
            numDice = spell.getNumDice();
            dieSize = spell.getDieSize();
        }

        mainDamageField = new JTextField();
        mainDamageField.putClientProperty("JComponent.roundRect", true);
        mainDamageField.addKeyListener(new DieRollListener(numDice, dieSize, mainDamageField));
        mainDamageField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                super.keyReleased(e);
                updateButtonText();
            }
        });

        otherBonusDamageCheck = new JCheckBox("Other Bonus Damage?");
        otherBonusDamageCheck.addActionListener(e -> otherBonusDamageField.setEnabled(otherBonusDamageCheck.isSelected()));

        otherBonusDamageField = new JTextField();
        otherBonusDamageField.putClientProperty("JComponent.roundRect", true);
        otherBonusDamageField.addKeyListener(new IntegerFieldListener());
        otherBonusDamageField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                super.keyReleased(e);
                updateButtonText();
            }
        });
        otherBonusDamageField.setEnabled(false);

        okButton = getOkButton();

        if (isManual) {
            add(new JLabel("Enter Damage"));
        } else {
            add(new JLabel("Enter " + damageString + " damage"));
        }
        add(mainDamageField);

        if (weapon != null && !weapon.equals(Weapon.MANUAL)) {
            add(new JLabel("+" + attacker.stats().prof() + " for Proficiency"));
            add(new JLabel("+" + attacker.stats().mod(weapon.getMod()) + " from Stat Bonus"));
        }

        if (!isManual) {
            add(otherBonusDamageCheck);
            add(otherBonusDamageField);
        }

        add(okButton);

        pack();
        setLocationRelativeTo(null);
    }

    private JButton getOkButton() {
        JButton button = new JButton("Deal Damage");
        button.putClientProperty("JButton.buttonType", "roundRect");
        button.addActionListener(e -> {
            if (calculateTotal() == 0) {
                return;
            }
            target.damage(calculateTotal());
            dispose();
        });
        return button;
    }

    private int calculateTotal() {
        int mainDamage = 0;
        if (!mainDamageField.getText().isEmpty()) {
            mainDamage = Integer.parseInt(mainDamageField.getText());
        }
        int bonusDamage = 0;
        if (otherBonusDamageField.isEnabled() && !otherBonusDamageField.getText().isEmpty()) {
            bonusDamage = Integer.parseInt(otherBonusDamageField.getText());
        }
        if (isManual) {
            return mainDamage;
        }
        return mainDamage +
                bonusDamage +
                attacker.stats().prof() +
                attacker.stats().mod(weapon.getMod());
    }

    private void updateButtonText() {
        int damage = calculateTotal();
        if (damage <= 0) {
            okButton.setText("Deal Damage");
            okButton.setEnabled(false);
        } else {
            okButton.setText("Deal " + damage + " Damage");
            okButton.setEnabled(true);
        }
    }

}