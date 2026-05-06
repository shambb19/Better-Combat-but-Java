package combat_menu.action_panel.form;

import combat_object.combatant.Combatant;
import combat_object.implement.Effect;
import combat_object.implement.Implement;
import lombok.*;
import lombok.experimental.*;
import manager.CombatManager;
import manager.EncounterManager;
import swing.custom.ValidatedField;

import javax.swing.*;
import javax.swing.border.MatteBorder;
import java.awt.*;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import static swing.ColorStyles.*;
import static swing.fluent.SwingComp.*;
import static swing.fluent.SwingPane.fluent;
import static swing.fluent.SwingPane.*;

@FieldDefaults(makeFinal = true, level = AccessLevel.PROTECTED)
public abstract class ActionFormPanel extends JPanel {

    JLabel selectionZone;
    JPanel fieldsPanel;
    JPanel buttonRow;
    JButton confirmButton, cancelButton;
    Map<Effect, Boolean> noticeConditions = new LinkedHashMap<>();

    Combatant attacker;
    @NonFinal Combatant target;

    protected ActionFormPanel(String confirmLabel, Combatant target) {
        attacker = EncounterManager.getCurrentCombatant();
        this.target = target;

        fluent(this).arrangedAs(BORDER)
                .withBackground(BACKGROUND)
                .withEmptyBorder(16, 18, 14, 18);

        JPanel stack = panelIn(this, BorderLayout.NORTH).arrangedAs(VERTICAL_BOX).transparent().component();

        selectionZone = label("No target selected", Font.ITALIC, 13f, FG_MUTED).onLeft().component();

        fieldsPanel = newArrangedAs(VERTICAL_BOX).transparent().onLeft().component();
        buildFields();

        buttonRow = newArrangedAs(FLOW_LEFT).transparent().onLeft().component();

        @Helper class ConfirmCancel {
            static JButton styledButton(String text, Color bg, Color fg, Runnable onClick) {
                return button(text, bg, onClick)
                        .withText(Font.BOLD, 12f, fg)
                        .component();
            }
        }
        confirmButton =
                ConfirmCancel.styledButton(confirmLabel, HEALTHY, new Color(0xD8, 0xF4, 0xEC), this::onConfirm);
        cancelButton =
                ConfirmCancel.styledButton("Cancel", TRACK, FG_MUTED, this::onCancel);

        fluent(buttonRow).collect(confirmButton, spacer(8, 0), cancelButton);

        fluent(stack).collect(
                getTargetSelectionPanel(), spacer(0, 14),
                fieldsPanel, spacer(0, 12),
                buttonRow
        );

        SwingUtilities.invokeLater(this::refreshButtons);
    }

    protected ActionFormPanel(String confirmLabel) {
        this(confirmLabel, null);
    }

    protected abstract void buildFields();

    protected void addNotice(Effect effect, JPanel container) {
        container.add(effect.noticePanel(target), 0);
    }

    protected void addNotices() {
        for (Component c : fieldsPanel.getComponents())
            if (c instanceof Effect.NoticePanel n)
                fieldsPanel.remove(n);

        SwingUtilities.invokeLater(() -> {
            AtomicInteger insertIdx = new AtomicInteger(0);

            noticeConditions.forEach((effect, condition) -> {
                if (condition) {
                    fieldsPanel.add(effect.noticePanel(target), insertIdx.getAndIncrement());
                    fieldsPanel.add(spacer(0, 10), insertIdx.getAndIncrement());
                }
            });
            fieldsPanel.revalidate();
            fieldsPanel.repaint();
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
    }

    public void submitTarget(Combatant dropped) {
        this.target = dropped;
        onTargetChanged();
        refreshButtons();
    }

    protected void onTargetChanged() {
        selectionZone.setText(target.getName() + "  ·  " + target.getHealthBarString());
        selectionZone.setFont(selectionZone.getFont().deriveFont(Font.PLAIN, 13f));
        selectionZone.setForeground(FOREGROUND);

        JPanel header = (JPanel) selectionZone.getParent().getParent();
        header.setBorder(BorderFactory.createMatteBorder(0, 4, 0, 0, target.getCombatantColor()));
        header.repaint();
    }

    protected LabeledField addLabeledField(JPanel container, String labelText, String placeholder) {
        JLabel label = label(labelText).muted()
                .withPreferredSize(110, 0).component();

        ValidatedField field = new ValidatedField(placeholder, this::refreshButtons);

        JPanel row = newArrangedAs(BORDER, 12, 0)
                .collect(label, field)
                .transparent()
                .onLeft()
                .withMaximumSize(Integer.MAX_VALUE, 52)
                .withEmptyBorder(12, 0, 4, 0)
                .component();

        fluent(container).collect(row, spacer(0, 10));

        return new LabeledField(label, field);
    }

    protected void refreshButtons() {
        boolean canConfirm = (target != null && isInputValid());
        confirmButton.setEnabled(canConfirm);
    }

    protected abstract boolean isInputValid();

    protected JPanel getTargetSelectionPanel() {
        JPanel targetHeader = newArrangedAs(BORDER)
                .withPreferredSize(0, 52)
                .withMaximumSize(Integer.MAX_VALUE, 52)
                .opaque()
                .withBackground(BG_SURFACE)
                .withBorder(new MatteBorder(0, 4, 0, 0, Color.GRAY))
                .component();

        JLabel targetLabel = label("TARGET", Font.BOLD, 10f, FG_MUTED).onLeft().component();

        newArrangedAs(VERTICAL_BOX)
                .collect(
                        targetLabel, spacer(0, 3), selectionZone
                ).withEmptyBorder(8, 14, 8, 14)
                .transparent()
                .in(targetHeader, BorderLayout.CENTER);

        return targetHeader;
    }

    protected JPanel getAttackComboRow(JComboBox<Implement> comboBox) {
        JLabel label = label("Select an attack", FG_MUTED).withPreferredSize(110, 0).component();

        return newBorderPanel(12, 0, west(label), center(comboBox))
                .transparent()
                .onLeft()
                .withPreferredSize(Integer.MAX_VALUE, 52)
                .component();
    }

    protected record LabeledField(JLabel label, ValidatedField field) {
    }
}