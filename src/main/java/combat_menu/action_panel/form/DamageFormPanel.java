package combat_menu.action_panel.form;

import __main.manager.CombatManager;
import __main.manager.EffectManager;
import combat_object.combatant.Combatant;
import combat_object.damage_implements.Effect;
import combat_object.damage_implements.Implement;
import format.ColorStyles;
import lombok.*;
import lombok.experimental.*;
import swing.ValidatedField;
import swing.swing_comp.SwingComp;
import swing.swing_comp.SwingPane;
import util.StringUtils;

import javax.swing.*;
import java.awt.*;
import java.util.LinkedHashMap;
import java.util.stream.Stream;

@FieldDefaults(level = AccessLevel.PRIVATE)
@ExtensionMethod(StringUtils.class)
public class DamageFormPanel extends ActionFormPanel {

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

        if (dropZone != null) {
            dropZone.setTarget(target);
            dropZone.removeClearOption();
            dropZone.setTargetValidator(c -> false);
        }

        refreshButtons();
    }

    @Override
    protected void buildFields(JPanel container) {
        LabeledField amountLF = addLabeledField(container, "Damage amount", "Enter amount...");
        amountField = amountLF.field();
        amountField.setValidator(s -> {
            int v = s.toInt();
            if (v == Integer.MIN_VALUE) return false;

            return v >= 0 && v <= calculateMaxDamage();
        });

        container.add(vgap(10));

        JCheckBox bonusCheck = styledCheckbox();
        bonusCheck.addActionListener(e -> toggleBonusRow(bonusCheck.isSelected(), container));
        container.add(checkboxRow(bonusCheck));
        container.add(vgap(6));

        bonusRow = buildBonusRow();
        bonusRow.setVisible(false);
        container.add(bonusRow);
        container.add(vgap(12));

        SwingUtilities.invokeLater(() -> {
            noticeConditions = new LinkedHashMap<>();

            noticeConditions.put(Effect.HALF_DAMAGE, attackFailed);
            noticeConditions.put(Effect.BONUS_DAMAGE, EffectManager.isHexedBy(target, attacker));

            Stream.of(
                    Effect.ADVANTAGE_SOON, Effect.ILLUSION, Effect.BLIND, Effect.DIFFICULT_TERRAIN,
                    Effect.DISADVANTAGE_ATTACK, Effect.FORCED_MOVE, Effect.FRIGHTEN,
                    Effect.PRONE, Effect.PULL, Effect.RESTRAIN, Effect.TRACKING
            ).forEach(e -> noticeConditions.put(e, implement.effectEquals(e)));

            addNotices(noticeConditions, container);
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
        cancelButton.setEnabled(false);
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

    private static JCheckBox styledCheckbox() {
        JCheckBox cb = new JCheckBox("Add bonus damage");
        cb.setFont(cb.getFont().deriveFont(Font.PLAIN, 12f));
        cb.setForeground(ColorStyles.TEXT_MUTED);
        cb.setOpaque(false);
        cb.setFocusPainted(false);
        cb.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return cb;
    }

    private void toggleBonusRow(boolean show, JPanel container) {
        bonusRow.setVisible(show);
        if (!show && bonusField != null)
            bonusField.clear();

        container.revalidate();
        container.repaint();
        refreshButtons();
    }

    private static JPanel checkboxRow(JCheckBox cb) {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        row.setOpaque(false);
        row.setAlignmentX(LEFT_ALIGNMENT);
        row.add(cb);
        return row;
    }

    private JPanel buildBonusRow() {
        JPanel row = SwingPane.panel().withLayout(SwingPane.BORDER).withGaps(12, 0)
                .transparent().onLeft().component();

        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        row.setMinimumSize(new Dimension(0, 45));

        JLabel lbl = SwingComp.label("Bonus damage").withDerivedFont(Font.PLAIN, 12f)
                .withForeground(ColorStyles.TEXT_MUTED).component();

        lbl.setPreferredSize(new Dimension(110, 30));

        row.add(lbl, BorderLayout.WEST);

        bonusField = new ValidatedField("0", this::refreshButtons);
        row.add(bonusField, BorderLayout.CENTER);
        return row;
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
}