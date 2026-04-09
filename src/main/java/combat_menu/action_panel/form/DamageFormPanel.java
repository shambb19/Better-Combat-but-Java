package combat_menu.action_panel.form;

import __main.CombatManager;
import __main.EncounterInfo;
import character_info.combatant.Combatant;
import combat_menu.action_panel.LabeledField;
import damage_implements.Effect;
import damage_implements.Implement;
import damage_implements.Spell;
import format.ColorStyles;

import javax.swing.*;
import java.awt.*;
import java.util.concurrent.atomic.AtomicInteger;

public class DamageFormPanel extends ActionFormPanel {

    private final Implement implement;
    private final boolean attackFailed;

    private ValidatedField amountField;
    private ValidatedField bonusField;
    private JPanel bonusRow;

    public DamageFormPanel(Combatant combatant, Implement implement, boolean attackSucceeded) {
        super("Apply " + (attackSucceeded ? implement.damageString() : "Halved") + " Damage");

        this.implement = implement;
        this.attackFailed = !attackSucceeded;

        this.target = combatant;
        if (dropZone != null) {
            dropZone.setTarget(combatant);
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
            try {
                int v = Integer.parseInt(s);
                int max = (implement != null) ? implement.getMaxDamage() : Integer.MAX_VALUE;
                return v > 0 && v <= max;
            } catch (NumberFormatException e) {
                return false;
            }
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
            AtomicInteger insertIdx = new AtomicInteger(0);

            if (implement instanceof Spell s) {
                Effect effect = s.effect();

                if (attackFailed)
                    addNotice(Effect.HALF_DAMAGE, container, insertIdx);
                if (target.isHexedBy(EncounterInfo.getCurrentCombatant()))
                    addNotice(Effect.BONUS_DAMAGE, container, insertIdx);
                if (effect.equals(Effect.ADVANTAGE_SOON))
                    addNotice(effect, container, insertIdx);
                if (effect.equals(Effect.ILLUSION))
                    addNotice(effect, container, insertIdx);
            }

            container.revalidate();
            container.repaint();
        });
    }

    private void addNotice(Effect effect, Container container, AtomicInteger insertIdx) {
        container.add(effect.noticePanel(target), insertIdx.getAndIncrement());
        container.add(Box.createRigidArea(new Dimension(0, 10)), insertIdx.getAndIncrement());
    }

    private JPanel buildBonusRow() {
        JPanel row = new JPanel(new BorderLayout(12, 0));
        row.setOpaque(false);
        row.setAlignmentX(LEFT_ALIGNMENT);

        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        row.setMinimumSize(new Dimension(0, 45));

        JLabel lbl = new JLabel("Bonus damage");
        lbl.setFont(lbl.getFont().deriveFont(Font.PLAIN, 12f));
        lbl.setForeground(ColorStyles.TEXT_MUTED);

        lbl.setPreferredSize(new Dimension(110, 30));

        row.add(lbl, BorderLayout.WEST);

        bonusField = new ValidatedField("0", this::refreshButtons);
        row.add(bonusField, BorderLayout.CENTER);
        return row;
    }

    private void toggleBonusRow(boolean show, JPanel container) {
        bonusRow.setVisible(show);
        if (!show && bonusField != null)
            bonusField.clear();

        container.revalidate();
        container.repaint();
        refreshButtons();
    }

    @Override
    protected boolean isInputValid() {
        if (amountField == null || !amountField.isValid()) return false;
        if (bonusRow != null && bonusRow.isVisible())
            return bonusField != null && bonusField.isValid();
        return true;
    }

    @Override
    protected void refreshButtons() {
        super.refreshButtons();
        cancelButton.setEnabled(false);
    }

    @Override
    protected void onConfirm() {
        if (!isInputValid()) return;
        try {
            int base = Integer.parseInt(amountField.getValue());
            int bonus = parseBonusOrZero();
            CombatManager.logDamage(target, implement, base, bonus, attackFailed);
            onCancel();
        } catch (NumberFormatException ignored) {
        }
    }

    private int parseBonusOrZero() {
        if (bonusField == null || bonusRow == null || !bonusRow.isVisible()) return 0;
        String v = bonusField.getValue();
        if (v.isBlank()) return 0;
        try {
            return Integer.parseInt(v);
        } catch (Exception e) {
            return 0;
        }
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

    private static JPanel checkboxRow(JCheckBox cb) {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        row.setOpaque(false);
        row.setAlignmentX(LEFT_ALIGNMENT);
        row.add(cb);
        return row;
    }
}