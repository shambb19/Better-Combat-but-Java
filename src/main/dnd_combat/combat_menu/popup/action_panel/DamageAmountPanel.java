package combat_menu.popup.action_panel;

import __main.CombatMain;
import character_info.combatant.Combatant;
import combat_menu.listener.DieRollListener;
import combat_menu.listener.IntegerFieldListener;
import damage_implements.Effect;
import damage_implements.Implement;
import damage_implements.Spell;
import damage_implements.Weapon;
import format.ColorStyle;
import format.SwingStyles;
import util.Message;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;

import static util.Message.informIllusion;

public class DamageAmountPanel extends JPanel {

    private final ActionPanel root;

    private final Combatant target;
    private final Implement implement;
    private final Combatant attacker;

    private final JTextField mainDamageField = new JTextField(8);
    private final JCheckBox otherBonusDamageCheck = new JCheckBox("Other Bonus Damage?");
    private final JTextField otherBonusDamageField = new JTextField(8);
    private final JLabel summaryLabel = new JLabel("Total Damage: 0");
    private final JButton okButton = new JButton("Deal Damage");

    public static DamageAmountPanel newInstance(Implement implement, Combatant target, ActionPanel root) {
        return new DamageAmountPanel(implement, target, root);
    }

    private DamageAmountPanel(Implement implement, Combatant target, ActionPanel root) {
        this.root = root;

        this.implement = implement;
        this.target = target;
        this.attacker = CombatMain.QUEUE.getCurrentCombatant();

        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        JPanel damagePanel = getDamagePanel();
        JPanel bonusPanel = getBonusPanel();
        JPanel summaryPanel = getSummaryPanel();

        okButton.setBackground(ColorStyle.DARKER_RED.getColor());
        JPanel okCancelPanel = SwingStyles.getConfirmCancelPanel(
                okButton,
                e -> logAndFinish(),
                e -> root.returnToButtons()
        );

        add(damagePanel);
        add(Box.createVerticalStrut(10));
        add(bonusPanel);
        add(Box.createVerticalStrut(10));
        add(summaryPanel);
        add(Box.createVerticalStrut(15));
        add(okCancelPanel);

        setupListeners();
        updateUIState();

        SwingUtilities.invokeLater(mainDamageField::requestFocusInWindow);
    }

    private JPanel getDamagePanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        SwingStyles.addLabeledBorder(panel, "Main Damage");

        JLabel label = new JLabel("Enter " + implement.damageString() + ":");

        mainDamageField.setFont(mainDamageField.getFont().deriveFont(16f));
        mainDamageField.setHorizontalAlignment(JTextField.CENTER);

        panel.add(label);
        panel.add(mainDamageField);

        return panel;
    }

    private JPanel getBonusPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout(FlowLayout.LEFT));
        SwingStyles.addLabeledBorder(panel, "Bonus Damage");

        otherBonusDamageField.setEnabled(false);

        panel.add(otherBonusDamageCheck);
        panel.add(otherBonusDamageField);

        return panel;
    }

    private JPanel getSummaryPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        SwingStyles.addLabeledBorder(panel, "Breakdown");

        if (implement instanceof Weapon && !implement.isManual()) {
            panel.add(new JLabel("Stat Bonus: +" + attacker.mod(implement.stat())));
        }
        if (implement.isHalfDamage()) {
            panel.add(new JLabel("Damage will be halved (Failed Attack)"));
        }
        if (target.isHexedBy(attacker)) {
            panel.add(new JLabel("Hex: include +1d6 in bonus field"));
        }

        summaryLabel.setFont(summaryLabel.getFont().deriveFont(Font.BOLD, 16f));
        summaryLabel.setForeground(ColorStyle.DARKER_RED.getColor());
        summaryLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        panel.add(Box.createVerticalStrut(5));
        panel.add(summaryLabel);

        return panel;
    }

    private void setupListeners() {
        DocumentListener updateListener = new DocumentListener() {
            public void insertUpdate(DocumentEvent e) {
                updateUIState();
            }

            public void removeUpdate(DocumentEvent e) {
                updateUIState();
            }

            public void changedUpdate(DocumentEvent e) {
                updateUIState();
            }
        };

        mainDamageField.getDocument().addDocumentListener(updateListener);
        otherBonusDamageField.getDocument().addDocumentListener(updateListener);

        if (implement.isManual()) {
            mainDamageField.addKeyListener(new IntegerFieldListener());
        } else {
            mainDamageField.addKeyListener(
                    new DieRollListener(
                            implement.numDice(),
                            implement.dieSize(),
                            mainDamageField
                    )
            );
        }

        otherBonusDamageField.addKeyListener(new IntegerFieldListener());
        otherBonusDamageCheck.addActionListener(e -> {
            boolean enabled = otherBonusDamageCheck.isSelected();
            otherBonusDamageField.setEnabled(enabled);

            if (!enabled) {
                otherBonusDamageField.setText("");
            }

            updateUIState();
        });

        okButton.addActionListener(e -> logAndFinish());
    }

    private int calculateTotal() {
        int damage = getMainDamage() + getBonusDamage();

        if (implement.isManual()) {
            return damage;
        }
        if (implement.isHalfDamage()) {
            damage /= 2;
        }
        if (implement instanceof Weapon) {
            damage += attacker.mod(implement.stat());
        }

        return Math.max(0, damage);
    }

    private int getMainDamage() {
        String text = mainDamageField.getText().trim();
        if (text.isEmpty()) return 0;
        return Integer.parseInt(text);
    }

    private int getBonusDamage() {
        if (!otherBonusDamageCheck.isSelected()) return 0;

        String text = otherBonusDamageField.getText().trim();
        if (text.isEmpty()) return 0;

        return Integer.parseInt(text);
    }

    private void updateUIState() {
        okButton.setBackground(ColorStyle.DARKER_RED.getColor());
        try {
            int total = calculateTotal();
            if (total > 0) {
                okButton.setEnabled(true);
                okButton.setText("Deal " + total + " Damage");
                summaryLabel.setText("Total Damage: " + total);
            } else {
                okButton.setEnabled(false);
                okButton.setText("Enter Damage");
                summaryLabel.setText("Total Damage: 0");
            }
        } catch (NumberFormatException e) {
            okButton.setEnabled(false);
            okButton.setText("Numbers Only");
        }
    }

    private void logAndFinish() {
        int total = calculateTotal();
        target.damage(total);

        if (implement instanceof Spell spell) {
            Effect effect = spell.effect();
            attacker.putEffect(target, effect);

            if (effect.equals(Effect.ILLUSION)) {
                informIllusion(target);
            } else if (effect.equals(Effect.ADVANTAGE_SOON)) {
                Message.template("Advantage on next attack against " + target + " this turn.");
            }
        }

        CombatMain.COMBAT_MENU.update();
        CombatMain.checkWinConditions();

        root.returnToButtons();
    }
}