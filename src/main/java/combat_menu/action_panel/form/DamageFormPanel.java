package combat_menu.action_panel.form;

import __main.manager.CombatManager;
import __main.manager.EffectManager;
import combat_object.combatant.Combatant;
import combat_object.damage_implements.Effect;
import combat_object.damage_implements.Implement;
import format.ColorStyles;
import lombok.*;
import lombok.experimental.*;
import swing_custom.ValidatedField;
import util.StringUtils;

import javax.swing.*;
import java.awt.*;
import java.util.Optional;

import static combat_object.damage_implements.Effect.*;
import static format.swing_comp.SwingPane.*;

@FieldDefaults(level = AccessLevel.PRIVATE)
@ExtensionMethod(StringUtils.class)
public class DamageFormPanel extends ActionFormPanel {

    static final Effect[] DAMAGE_EFFECTS = new Effect[]
            {
                    ADVANTAGE_SOON, ILLUSION, BLIND, DIFFICULT_TERRAIN,
                    DISADVANTAGE_ATTACK, FORCED_MOVE, FRIGHTEN, BANISH,
                    PRONE, PULL, RESTRAIN, TRACKING, STAT_DROP
            };

    final Implement implement;
    final boolean attackFailed;

    ValidatedField amountField;
    ValidatedField bonusField;
    JPanel bonusRow;

    public DamageFormPanel(Combatant target, Implement implement, boolean attackSucceeded) {
        super("Apply "
                + ((!attackSucceeded) ? (implement.getNumDice() / 2) + "d" + implement.getDieSize() : implement.damageString())
                + " Damage", target);

        this.implement = implement;
        this.attackFailed = !attackSucceeded;

        if (target != null) {
            onTargetChanged();
        }
        refreshButtons();
    }

    @Override
    protected void buildFields() {
        LabeledField amountLF = addLabeledField(fieldsPanel, "Damage Amount", "Enter Damage Amount");
        amountField = amountLF.field();
        amountField.setValidator(s -> {
            int v = s.toInt();
            return v >= 0 && v <= calculateMaxDamage();
        });

        JCheckBox bonusCheck = styledCheckbox();
        bonusCheck.addActionListener(e -> toggleBonusRow(bonusCheck.isSelected(), fieldsPanel));

        bonusRow = buildBonusRow();
        bonusRow.setVisible(false);

        fluent(fieldsPanel).collect(
                spacer(0, 10), checkboxRow(bonusCheck), spacer(0, 6), bonusRow, spacer(0, 12)
        );

        addNotices();
    }

    @Override
    protected void addNotices() {
        SwingUtilities.invokeLater(() -> {
            noticeConditions.put(Effect.HALF_DAMAGE, attackFailed);
            noticeConditions.put(Effect.BONUS_DAMAGE, EffectManager.isHexedBy(target, attacker));

            for (Effect e : DAMAGE_EFFECTS) {
                noticeConditions.put(e, implement.effectEquals(e));
            }

            super.addNotices();
        });
    }

    @Override
    protected void onConfirm() {
        if (!isInputValid()) return;

        int base = amountField.getValue().toInt();
        if (base != Integer.MIN_VALUE) {
            int bonus = parseBonusOrZero();
            CombatManager.logDamage(target, implement, base, bonus);
            onCancel();
        }
    }

    @Override
    protected void refreshButtons() {
        super.refreshButtons();
        cancelButton.setVisible(false);
    }

    @Override
    protected boolean isInputValid() {
        if (amountField == null || !amountField.isValid()) return false;
        if (bonusRow != null && bonusRow.isVisible())
            return bonusField != null && bonusField.isValid();
        return true;
    }

    private int parseBonusOrZero() {
        if (bonusField == null || bonusRow == null || !bonusRow.isVisible()) return 0;
        String v = bonusField.getValue();
        if (v.isBlank()) return 0;

        int n = v.toInt();
        return (n != Integer.MIN_VALUE) ? n : 0;
    }

    private int calculateMaxDamage() {
        int numDice = implement.getNumDice();
        int dieSize = implement.getDieSize();

        if (attackFailed)
            numDice /= 2;
        if (implement.effectEquals(Effect.FULL_HP_OPTION))
            dieSize = 12;

        int max = numDice * dieSize;

        if (EffectManager.isHexedBy(target, attacker))
            max += 6;

        return max;
    }

    private static JCheckBox styledCheckbox() {
        JCheckBox cb = new JCheckBox("Add bonus damage");
        cb.setFont(cb.getFont().deriveFont(Font.PLAIN, 12f));
        cb.setForeground(ColorStyles.FG_MUTED);
        cb.setOpaque(false);
        cb.setFocusPainted(false);
        cb.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return cb;
    }

    private void toggleBonusRow(boolean show, JPanel container) {
        bonusRow.setVisible(show);
        if (!show)
            Optional.ofNullable(bonusField).ifPresent(ValidatedField::clear);

        container.revalidate();
        container.repaint();
        refreshButtons();
    }

    private JPanel buildBonusRow() {
        JPanel row = newArrangedAs(BORDER, 12, 0)
                .transparent().onLeft().component();

        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        row.setMinimumSize(new Dimension(0, 45));

        JLabel lbl = label("Bonus damage").muted().component();

        lbl.setPreferredSize(new Dimension(110, 30));

        row.add(lbl, BorderLayout.WEST);

        bonusField = new ValidatedField("Enter Bonus Damage", this::refreshButtons);
        row.add(bonusField, BorderLayout.CENTER);
        return row;
    }

    private static JPanel checkboxRow(JCheckBox cb) {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        row.setOpaque(false);
        row.setAlignmentX(LEFT_ALIGNMENT);
        row.add(cb);
        return row;
    }
}