package combat_menu.action_panel.form;

import _manager.CombatManager;
import _manager.EffectManager;
import combat_object.combatant.Combatant;
import combat_object.implement.Effect;
import combat_object.implement.Implement;
import lombok.*;
import lombok.experimental.*;
import swing.ColorStyles;
import swing.custom.ValidatedField;
import util.StringUtil;

import javax.swing.*;
import java.awt.*;
import java.util.Optional;

import static combat_object.implement.Effect.*;
import static swing.fluent.SwingPane.fluent;
import static swing.fluent.SwingPane.spacer;

@FieldDefaults(level = AccessLevel.PRIVATE)
@ExtensionMethod(StringUtil.class)
public class DamageFormPanel extends ActionFormPanel {

    static final Effect[] DAMAGE_EFFECTS = new Effect[]
            {
                    POISON, ADVANTAGE_SOON, ILLUSION, BLIND, TRACKING,
                    DIFFICULT_TERRAIN, DISADVANTAGE_ATTACK, FORCED_MOVE,
                    FRIGHTEN, BANISH, PRONE, PULL, RESTRAIN, STAT_DROP, HEAL_SELF
            };

    final Implement implement;
    final boolean attackFailed;

    ValidatedField amountField;
    ValidatedField bonusField;
    JPanel bonusRow;

    public DamageFormPanel(Combatant target, Implement implement, boolean attackSucceeded) {
        super("Apply" + implement.damageString(!attackSucceeded) + " Damage", target);

        // FIXME input line does not appear

        this.implement = implement;
        this.attackFailed = !attackSucceeded;

        if (target != null) {
            onTargetChanged();
        }
        refreshButtons();
    }

    @Override protected void buildFields() {
        amountField = new ValidatedField("Enter Damage Amount", this::refreshButtons);
        amountField.setValidator(s -> {
            int v = s.toInt();
            return v >= 0 && v <= getMaxDamage();
        });
        JPanel amountRow = getLabeledRow("Damage Amount", amountField);

        JCheckBox bonusCheck = fluent(new JCheckBox("Add bonus damage"))
                .withAction(b -> toggleBonusRow(b.isSelected(), fieldsPanel))
                .withDerivedFont(Font.PLAIN, 12f)
                .withForeground(ColorStyles.FG_MUTED)
                .transparent()
                .component();

        bonusField = new ValidatedField("Bonus Damage", this::refreshButtons);
        bonusField.setValidator(s -> s.toInt() > 0);
        bonusRow = getLabeledRow("Bonus Damage", bonusField);
        bonusRow.setVisible(false);

        fluent(fieldsPanel).collect(
                amountRow, spacer(0, 10),
                checkboxRow(bonusCheck), spacer(0, 6),
                bonusRow, spacer(0, 12)
        );

        addNotices();
    }

    @Override protected void addNotices() {
        SwingUtilities.invokeLater(() -> {
            noticeConditions.put(Effect.HALF_DAMAGE, attackFailed);
            noticeConditions.put(Effect.BONUS_DAMAGE, EffectManager.isHexedBy(target, attacker));

            for (Effect e : DAMAGE_EFFECTS) {
                noticeConditions.put(e, implement.effectEquals(e));
            }

            super.addNotices();
        });
    }

    @Override protected void onConfirm() {
        if (!isInputValid()) return;

        int base = amountField.getValue().toInt();
        if (base != Integer.MIN_VALUE) {
            int bonus = parseBonusOrZero();
            CombatManager.logDamage(target, implement, base, bonus);
            onCancel();
        }
    }

    @Override protected void refreshButtons() {
        super.refreshButtons();
        cancelButton.setVisible(false);
    }

    @Override protected boolean isInputValid() {
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

    private int getMaxDamage() {
        int numDice = implement.getRoll().getNumDice();
        int dieSize = implement.getRoll().getNumDice();

        if (attackFailed)
            numDice /= 2;
        if (implement.effectEquals(Effect.FULL_HP_OPTION))
            dieSize = 12;

        int max = numDice * dieSize;

        if (EffectManager.isHexedBy(target, attacker))
            max += 6;

        return max;
    }

    private void toggleBonusRow(boolean show, JPanel container) {
        bonusRow.setVisible(show);
        if (!show) Optional.ofNullable(bonusField).ifPresent(ValidatedField::clear);

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
}