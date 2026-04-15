package combat_menu.action_panel.form;

import __main.manager.CombatManager;
import __main.manager.EncounterManager;
import combat_menu.action_panel.DropZonePanel;
import combat_object.combatant.Combatant;
import combat_object.damage_implements.Effect;
import combat_object.damage_implements.Implement;
import format.ColorStyles;
import lombok.*;
import lombok.experimental.*;
import swing.ValidatedField;
import swing.swing_comp.SwingComp;
import swing.swing_comp.SwingPane;

import javax.swing.*;
import java.awt.*;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import static swing.swing_comp.SwingPane.*;

@FieldDefaults(makeFinal = true, level = AccessLevel.PROTECTED)
public abstract class ActionFormPanel extends JPanel {

    DropZonePanel dropZone;
    JPanel fieldsPanel;
    JPanel btnRow;
    JButton confirmButton, cancelButton;
    @NonFinal Map<Effect, Boolean> noticeConditions;

    Combatant attacker;
    @NonFinal Combatant target;

    protected ActionFormPanel(String confirmLabel, Combatant target) {
        attacker = EncounterManager.getCurrentCombatant();
        this.target = target;

        modifiable(this).withLayout(BORDER)
                .withBackground(ColorStyles.BACKGROUND)
                .opaque()
                .withEmptyBorder(16, 18, 14, 18);

        JPanel stack = panelIn(this, BorderLayout.NORTH).withLayout(VERTICAL_BOX)
                .transparent()
                .component();

        dropZone = new DropZonePanel(this::onTargetDropped);
        dropZone.setAlignmentX(LEFT_ALIGNMENT);
        stack.add(dropZone);
        stack.add(vgap(14));

        fieldsPanel = panel().withLayout(VERTICAL_BOX)
                .transparent()
                .onLeft()
                .in(stack)
                .component();
        buildFields(fieldsPanel);

        stack.add(vgap(12));

        btnRow = SwingPane.panel().withLayout(SwingPane.FLOW_LEFT).transparent().onLeft().component();

        @Helper class ConfirmCancel {
            static JButton styledButton(String text, Color bg, Color fg, Runnable onClick) {
                return SwingComp.button(text, onClick)
                        .withBackgroundAndForeground(bg, fg)
                        .withEmptyBorder(8, 20, 8, 20)
                        .withDerivedFont(Font.BOLD, 12f)
                        .component();
            }
        }

        confirmButton =
                ConfirmCancel.styledButton(confirmLabel, ColorStyles.HEALTHY, new Color(0xD8, 0xF4, 0xEC), this::onConfirm);
        cancelButton =
                ConfirmCancel.styledButton("Cancel", ColorStyles.TRACK, ColorStyles.TEXT_MUTED, this::onCancel);

        btnRow.add(confirmButton);
        btnRow.add(hgap());
        btnRow.add(cancelButton);
        stack.add(btnRow);

        SwingUtilities.invokeLater(this::refreshButtons);
    }

    protected ActionFormPanel(String confirmLabel) {
        this(confirmLabel, null);
    }

    protected static Component hgap() {
        return Box.createRigidArea(new Dimension(8, 0));
    }

    protected abstract void buildFields(JPanel container);

    protected void addNotice(Effect effect, JPanel container) {
        container.add(effect.noticePanel(target), 0);
    }

    protected void addNotices(Map<Effect, Boolean> possibleConditions, JPanel container) {
        SwingUtilities.invokeLater(() -> {
            AtomicInteger insertIdx = new AtomicInteger(0);

            possibleConditions.forEach((effect, condition) -> {
                if (condition) {
                    container.add(effect.noticePanel(target), insertIdx.getAndIncrement());
                    container.add(Box.createRigidArea(new Dimension(0, 10)), insertIdx.getAndIncrement());
                }
            });
            container.revalidate();
            container.repaint();
        });
    }

    protected void clearNotices(JPanel container) {
        for (Component component : container.getComponents()) {
            if (component instanceof Effect.NoticePanel)
                container.remove(component);
        }
    }

    protected abstract void onConfirm();

    protected void onCancel() {
        CombatManager.cancelAction();
        clearTarget();
    }

    protected void clearTarget() {
        target = null;
        dropZone.clearTarget();
        refreshButtons();
    }

    private void onTargetDropped(Combatant dropped) {
        this.target = dropped;
        dropZone.setTarget(dropped);
        onTargetChanged();
        refreshButtons();
    }

    protected void onTargetChanged() {
    }

    protected LabeledField addLabeledField(JPanel container, String labelText, String placeholder) {
        JPanel row = panel().withLayout(BORDER)
                .withGaps(12, 0)
                .transparent()
                .onLeft()
                .withMaximumSize(Integer.MAX_VALUE, 52)
                .withEmptyBorder(12, 0, 4, 0)
                .component();

        JLabel label = label(labelText)
                .asStandardTextSize()
                .withForeground(ColorStyles.TEXT_MUTED)
                .withPreferredSize(110, 0)
                .in(row, BorderLayout.WEST)
                .component();

        ValidatedField field = new ValidatedField(placeholder, this::refreshButtons);
        row.add(field, BorderLayout.CENTER);

        container.add(row);
        container.add(vgap(10));

        return new LabeledField(label, field);
    }

    protected void refreshButtons() {
        boolean canConfirm = (target != null && isInputValid());
        confirmButton.setEnabled(canConfirm);
    }

    protected static Component vgap(int h) {
        return Box.createRigidArea(new Dimension(0, h));
    }

    protected abstract boolean isInputValid();

    protected void setTargetValidator(java.util.function.Predicate<Combatant> validator) {
        dropZone.setTargetValidator(validator);
    }

    protected JPanel attackComboRow(JComboBox<Implement> comboBox) {
        JLabel label = SwingComp.label("Select an attack")
                .asStandardTextSize()
                .withForeground(ColorStyles.TEXT_MUTED)
                .withPreferredSize(110, 0)
                .component();

        return SwingPane.panel().withLayout(SwingPane.BORDER)
                .with(label, BorderLayout.WEST)
                .with(comboBox, BorderLayout.CENTER)
                .withGaps(12, 0)
                .transparent()
                .onLeft()
                .withPreferredSize(Integer.MAX_VALUE, 52)
                .component();
    }

    protected record LabeledField(JLabel label, ValidatedField field) {
    }
}