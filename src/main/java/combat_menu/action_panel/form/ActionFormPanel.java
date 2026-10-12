package combat_menu.action_panel.form;

import _manager.CombatManager;
import _manager.EffectManager;
import _manager.EncounterManager;
import combat_object.combatant.Combatant;
import combat_object.implement.Effect;
import lombok.*;
import lombok.experimental.*;
import swing.custom.ValidatedField;
import swing.builder.ButtonBuilder;
import swing.builder.ComponentBuilder;

import javax.swing.*;
import java.awt.*;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import static swing.ColorStyles.*;
import static swing.builder.Components.*;
import static swing.builder.PanelBuilder.*;

@FieldDefaults(makeFinal = true, level = AccessLevel.PROTECTED)
public abstract class ActionFormPanel extends JPanel {

    JLabel selectionZone;
    JPanel fieldsPanel;
    JPanel buttonRow;
    JButton confirmButton, cancelButton;
    Map<Effect, Combatant> noticeConditions = new LinkedHashMap<>();

    Combatant attacker;
    @NonFinal Combatant target;

    protected ActionFormPanel(String confirmLabel, Combatant target) {
        attacker = EncounterManager.getCurrentCombatant();
        this.target = target;

        modifyBasicPanel(this, BORDER, BorderFactory.createEmptyBorder(16, 18, 14, 18));

        JPanel stack = createPanel(VERTICAL_BOX).build().putIn(this, BorderLayout.NORTH);

        selectionZone = ComponentBuilder.createLabel("No target selected", Font.ITALIC, 13f, FG_MUTED)
                .alignmentX(LEFT_ALIGNMENT).build().get();

        fieldsPanel = createPanel(VERTICAL_BOX).alignmentX(LEFT_ALIGNMENT).build().get();

        buildFields();

        buttonRow = createPanel(FLOW_LEFT).alignmentX(LEFT_ALIGNMENT).build().get();

        interface ConfirmCancelFactory {
            JButton getStyledButton(String txt, Color bg, Color fg, Runnable onClick);
        }
        ConfirmCancelFactory ccf = (txt, bg, fg, onClick) ->
                ButtonBuilder.createButton(txt, bg, onClick)
                        .foreground(fg)
                        .fontBuilder(FontBuilder.of(Font.BOLD, 12f)).build().get();

        confirmButton = ccf.getStyledButton(confirmLabel, HEALTHY, new Color(0xD8, 0xF4, 0xEC), this::onConfirm);
        cancelButton = ccf.getStyledButton("Cancel", TRACK, FG_MUTED, this::onCancel);

        addConstraintlessComponentsTo(buttonRow, confirmButton, spacer(8, 0), cancelButton);

        addConstraintlessComponentsTo(stack,
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
        container.add(effect.noticePanel(target, attacker), 0);
    }

    protected void addNotices() {
        clearNotices();

        for (Component c : fieldsPanel.getComponents())
            if (c instanceof Effect.NoticePanel n)
                fieldsPanel.remove(n);

        SwingUtilities.invokeLater(() -> {
            AtomicInteger insertIdx = new AtomicInteger(0);

            noticeConditions.forEach((effect, on) -> {
                if (on != null) {
                    Combatant by = EffectManager.getSource(on, effect).orElse(attacker);
                    fieldsPanel.add(effect.noticePanel(on, by), insertIdx.getAndIncrement());
                    fieldsPanel.add(spacer(0, 10), insertIdx.getAndIncrement());
                }
            });
            fieldsPanel.revalidate();
            fieldsPanel.repaint();
        });
    }

    protected void clearNotices() {
        for (Component component : fieldsPanel.getComponents()) {
            if (component instanceof Effect.NoticePanel)
                fieldsPanel.remove(component);
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
        selectionZone.setFont(selectionZone.getFont().deriveFont(Font.PLAIN, 13f));
        selectionZone.setForeground(FOREGROUND);
        selectionZone.setText(target.getName() + "  ·  " + target.getHealthBarString());

        JPanel header = (JPanel) selectionZone.getParent().getParent();
        header.setBorder(BorderFactory.createMatteBorder(0, 4, 0, 0, target.getCombatantColor()));
        header.repaint();
    }

    protected JPanel getLabeledRow(String labelText, ValidatedField input) {
        JLabel label = ComponentBuilder.createLabel(labelText, Font.PLAIN, 12f, FG_MUTED)
                .preferredSize(new Dimension(110, 30)).build().get();

        return createPanel(BORDER)
                .hostedComponent(label, BorderLayout.WEST)
                .hostedComponent(input, BorderLayout.CENTER)
                .fixedHeight(45)
                .alignmentX(LEFT_ALIGNMENT).build().get();
    }

    protected LabeledField addLabeledField(JPanel container, String labelText, String placeholder) {
        JLabel label = ComponentBuilder.createLabel(labelText, Font.PLAIN, 12f, FG_MUTED)
                .preferredSize(new Dimension(110, 0)).build().get();

        ValidatedField field = new ValidatedField(placeholder, this::refreshButtons);

        JPanel row = createPanel(BORDER).hgap(12)
                .hostedComponent(label, BorderLayout.WEST)
                .hostedComponent(field, BorderLayout.CENTER)
                .alignmentX(LEFT_ALIGNMENT)
                .fixedHeight(52)
                .border(BorderFactory.createEmptyBorder(12, 0, 4, 0))
                .build().get();

        addConstraintlessComponentsTo(container, row, spacer(0, 10));

        return new LabeledField(label, field);
    }

    protected void refreshButtons() {
        boolean canConfirm = (target != null && isInputValid());
        confirmButton.setEnabled(canConfirm);
    }

    protected abstract boolean isInputValid();

    protected JPanel getTargetSelectionPanel() {
        JPanel targetHeader = createPanel(BORDER)
                .background(BG_SURFACE)
                .fixedHeight(52)
                .border(BorderFactory.createMatteBorder(0, 4, 0, 0, Color.GRAY)).build().get();

        JLabel targetLabel = ComponentBuilder.createLabel("TARGET", Font.BOLD, 10f, FG_MUTED)
                .alignmentX(LEFT_ALIGNMENT).build().get();

        createPanel(VERTICAL_BOX,
                targetLabel, spacer(0, 3), selectionZone
        ).border(BorderFactory.createEmptyBorder(8, 14, 8, 14))
                .build().putIn(targetHeader, BorderLayout.CENTER);

        return targetHeader;
    }

    protected record LabeledField(JLabel label, ValidatedField field) {
    }
}