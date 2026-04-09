package combat_menu.action_panel.form;

import __main.CombatManager;
import character_info.combatant.Combatant;
import combat_menu.action_panel.DropZonePanel;
import combat_menu.action_panel.LabeledField;
import damage_implements.Implement;
import format.ColorStyles;
import swing.swing_comp.SwingComp;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public abstract class ActionFormPanel extends JPanel {

    protected final DropZonePanel dropZone;
    protected final JPanel fieldsPanel;
    protected final JPanel btnRow;
    protected final JButton confirmButton;
    protected final JButton cancelButton;
    protected Combatant target;

    protected ActionFormPanel(String confirmLabel) {
        setLayout(new BorderLayout());
        setBackground(ColorStyles.BACKGROUND);
        setOpaque(true);
        setBorder(new EmptyBorder(16, 18, 14, 18));

        JPanel stack = new JPanel();
        stack.setLayout(new BoxLayout(stack, BoxLayout.Y_AXIS));
        stack.setOpaque(false);
        add(stack, BorderLayout.NORTH);

        dropZone = new DropZonePanel(this::onTargetDropped);
        dropZone.setAlignmentX(LEFT_ALIGNMENT);
        stack.add(dropZone);
        stack.add(vgap(14));

        fieldsPanel = new JPanel();
        fieldsPanel.setLayout(new BoxLayout(fieldsPanel, BoxLayout.Y_AXIS));
        fieldsPanel.setOpaque(false);
        fieldsPanel.setAlignmentX(LEFT_ALIGNMENT);
        buildFields(fieldsPanel);
        stack.add(fieldsPanel);
        stack.add(vgap(12));

        btnRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        btnRow.setOpaque(false);
        btnRow.setAlignmentX(LEFT_ALIGNMENT);

        confirmButton = styledButton(confirmLabel, ColorStyles.HP_HEALTHY, new Color(0xD8, 0xF4, 0xEC));
        cancelButton = styledButton("Cancel", ColorStyles.TRACK, ColorStyles.TEXT_MUTED);

        btnRow.add(confirmButton);
        btnRow.add(hgap());
        btnRow.add(cancelButton);
        stack.add(btnRow);

        confirmButton.addActionListener(e -> onConfirm());
        cancelButton.addActionListener(e -> onCancel());

        SwingUtilities.invokeLater(this::refreshButtons);
    }

    private static JButton styledButton(String text, Color bg, Color fg) {
        return SwingComp.button(text, null)
                .withBackground(bg)
                .withForeground(fg)
                .opaque()
                .applied(b -> {
                    b.setFont(b.getFont().deriveFont(Font.BOLD, 12f));
                    b.setBorder(new EmptyBorder(8, 20, 8, 20));
                    b.setFocusPainted(false);
                    b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                })
                .build();
    }

    protected static Component hgap() {
        return Box.createRigidArea(new Dimension(8, 0));
    }

    protected abstract void buildFields(JPanel container);

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
        JPanel row = new JPanel(new BorderLayout(12, 0));
        row.setOpaque(false);
        row.setAlignmentX(LEFT_ALIGNMENT);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 52));
        row.setBorder(new EmptyBorder(12, 0, 4, 0));

        JLabel lbl = new JLabel(labelText);
        lbl.setFont(lbl.getFont().deriveFont(Font.PLAIN, 12f));
        lbl.setForeground(ColorStyles.TEXT_MUTED);
        lbl.setPreferredSize(new Dimension(110, 0));
        row.add(lbl, BorderLayout.WEST);

        ValidatedField field = new ValidatedField(placeholder, this::refreshButtons);
        row.add(field, BorderLayout.CENTER);

        container.add(row);
        container.add(vgap(10));

        return new LabeledField(lbl, field);
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
        JPanel row = new JPanel(new BorderLayout(12, 0));
        row.setOpaque(false);
        row.setAlignmentX(LEFT_ALIGNMENT);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 52));

        JLabel label = new JLabel("Select an attack option");
        label.setFont(label.getFont().deriveFont(Font.PLAIN, 12f));
        label.setForeground(ColorStyles.TEXT_MUTED);
        label.setPreferredSize(new Dimension(110, 0));
        row.add(label, BorderLayout.WEST);

        row.add(comboBox, BorderLayout.CENTER);

        return row;
    }

    public static class ValidatedField extends JPanel {

        private final JTextField field;
        private final JPanel bar;
        private java.util.function.Predicate<String> validator = s -> !s.isBlank();

        ValidatedField(String placeholder, Runnable onChange) {
            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
            setOpaque(false);

            field = new JTextField();
            field.setBackground(ColorStyles.TRACK);
            field.setForeground(ColorStyles.TEXT_PRIMARY);
            field.setCaretColor(ColorStyles.TEXT_PRIMARY);
            field.setFont(field.getFont().deriveFont(Font.PLAIN, 13f));
            field.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(ColorStyles.DIVIDER, 1),
                    new EmptyBorder(5, 8, 5, 8)
            ));
            field.putClientProperty("JTextField.placeholderText", placeholder);
            field.setAlignmentX(LEFT_ALIGNMENT);
            add(field);

            bar = new JPanel();
            bar.setPreferredSize(new Dimension(0, 2));
            bar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 2));
            bar.setBackground(ColorStyles.TRACK);
            bar.setOpaque(true);
            bar.setAlignmentX(LEFT_ALIGNMENT);
            add(Box.createRigidArea(new Dimension(0, 2)));
            add(bar);

            field.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
                public void insertUpdate(javax.swing.event.DocumentEvent e) {
                    update();
                }

                public void removeUpdate(javax.swing.event.DocumentEvent e) {
                    update();
                }

                public void changedUpdate(javax.swing.event.DocumentEvent e) {
                    update();
                }

                private void update() {
                    refreshVisuals();
                    onChange.run();
                }
            });
        }

        private void refreshVisuals() {
            String text = getValue();
            if (text.isEmpty()) {
                bar.setBackground(ColorStyles.TRACK);
            } else if (validator.test(text)) {
                bar.setBackground(ColorStyles.HP_HEALTHY);
            } else {
                bar.setBackground(ColorStyles.HP_CRITICAL);
            }
        }

        public String getValue() {
            if (field == null) return "";

            return field.getText().trim();
        }

        public void setValidator(java.util.function.Predicate<String> v) {
            this.validator = v;
            refreshVisuals();
        }

        public boolean isValid() {
            if (validator == null) return false;

            return validator.test(getValue());
        }

        public void clear() {
            field.setText("");
        }
    }
}