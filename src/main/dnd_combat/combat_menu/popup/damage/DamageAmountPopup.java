package combat_menu.popup.damage;

import _main.CombatMain;
import character_info.combatant.Combatant;
import combat_menu.listener.DieRollListener;
import combat_menu.listener.IntegerFieldListener;
import damage_implements.Effect;
import damage_implements.Implement;
import damage_implements.Spell;
import damage_implements.Weapon;
import util.Message;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import static util.Message.informIllusion;

public class DamageAmountPopup extends JFrame {

    private final boolean isManual;
    private final boolean isHalfDamage;
    private final Combatant target;
    private final Implement implement;
    private final Combatant attacker;
    private final JTextField mainDamageField;
    private final JCheckBox otherBonusDamageCheck;
    private final JTextField otherBonusDamageField;
    private final JButton okButton;

    public static void run(Implement implement, Combatant target) {
        new DamageAmountPopup(implement, target).setVisible(true);
    }

    private DamageAmountPopup(Implement implement, Combatant target) {
        this.implement = implement;
        this.target = target;
        attacker = CombatMain.QUEUE.getCurrentCombatant();
        isManual = implement.isManual();
        isHalfDamage = implement.isHalfDamage();

        setTitle("Damage Amount");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new GridLayout(0, 1));
        setAlwaysOnTop(true);

        String damageString = implement.damageString();
        int numDice = implement.numDice();
        int dieSize = implement.dieSize();

        mainDamageField = new JTextField();
        mainDamageField.putClientProperty("JComponent.roundRect", true);
        if (isManual) {
            mainDamageField.addKeyListener(new IntegerFieldListener());
        } else {
            mainDamageField.addKeyListener(new DieRollListener(numDice, dieSize, mainDamageField));
        }
        mainDamageField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                super.keyReleased(e);
                updateButtonText();
            }
        });

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

        otherBonusDamageCheck = new JCheckBox("Other Bonus Damage?");
        otherBonusDamageCheck.addActionListener(e -> otherBonusDamageField.setEnabled(otherBonusDamageCheck.isSelected()));

        okButton = new JButton("Deal Damage");
        okButton.putClientProperty("JButton.buttonType", "roundRect");
        okButton.addActionListener(e -> logAndFinish());

        add(new JLabel("Enter " + damageString + " Damage"));
        add(mainDamageField);

        if (implement instanceof Weapon && !isManual) {
            add(new JLabel("+" + attacker.mod(implement.stat()) + " from Stat Bonus"));
        }
        if (isHalfDamage) {
            add(new JLabel("Damage halved because attack failed (enter full value anyways)."));
        }
        if (target.isHexedBy(attacker)) {
            add(new JLabel("+1d6 from Hex (enter below)"));
        }

        add(okButton);

        pack();
        setLocationRelativeTo(CombatMain.COMBAT_MENU);
    }

    private void logAndFinish() {
        if (mainDamageField.getText().isEmpty()) {
            return;
        }
        if (calculateTotal() == 0) {
            return;
        }
        target.damage(calculateTotal());

        if (implement instanceof Spell spell) {
            Effect effect = spell.effect();
            attacker.putEffect(target, effect);

            if (effect.equals(Effect.ILLUSION)) {
                informIllusion(target);
            } else if (effect.equals(Effect.ADVANTAGE_SOON)) {
                String message = "Roll with advantage if you attack " + target +
                        " again this turn. You will lose the bonus otherwise!";
                Message.template(message);
            }
        }

        CombatMain.COMBAT_MENU.update();
        CombatMain.checkWinConditions();
        dispose();
    }

    /**
     * @return the total damage dealt from the following sources: 1. The main damage roll,
     * 2. The number (if present) in the bonus damage field, 3. Proficiency bonuses
     * (assumed to be present for simplicity of code), 4. stat bonuses, 5.
     * if the attacker has the HALF_DAMAGE de-buff.
     */
    private int calculateTotal() {
        int damage = Integer.parseInt(mainDamageField.getText());

        if (otherBonusDamageField.isEnabled() && !otherBonusDamageField.getText().isEmpty()) {
            damage += Integer.parseInt(otherBonusDamageField.getText());
        }
        if (isManual) {
            return damage;
        }
        if (isHalfDamage) {
            damage /= 2;
        }
        if (implement instanceof Weapon) {
            damage += attacker.mod(implement.stat());
        }

        return damage;
    }

    /**
     * Changes the text of the ok button to show the amount of damage that would be
     * dealt if it is pressed at the current moment.
     */
    private void updateButtonText() {
        int damage = calculateTotal();
        if (damage <= 0) {
            okButton.setText("Enter Damage to Attack");
            okButton.setEnabled(false);
        } else {
            okButton.setText("Deal " + damage + " Damage");
            okButton.setEnabled(true);
        }
    }

}