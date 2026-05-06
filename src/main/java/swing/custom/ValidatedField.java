package swing.custom;

import config.Config;
import swing.ColorStyles;
import swing.fluent.SwingPane;
import util.StringUtil;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.util.function.Predicate;

import static swing.fluent.SwingComp.fluent;
import static swing.fluent.SwingPane.newArrangedAs;

@lombok.experimental.ExtensionMethod(StringUtil.class)
public class ValidatedField extends JPanel {

    private final JTextComponent field;
    private final JPanel bar;
    private Predicate<String> validator = s -> !s.isBlank();

    public ValidatedField(String placeholder, Runnable onChange, int maxValue) {
        this(placeholder, onChange);
        validator = s -> {
            if (s.isBlank()) return false;
            int intValue = s.toInt();
            return intValue > 0 && intValue <= maxValue;
        };
    }

    public ValidatedField(String placeholder, Runnable onChange) {
        JTextComponent inputField = Config.isDamageHidden() ? new JPasswordField() : new JTextField();

        field = fluent(inputField)
                .withBackground(ColorStyles.TRACK)
                .withDerivedFont(Font.PLAIN, 13f)
                .withPaddedBorder(new LineBorder(ColorStyles.DIVIDER, 1), 5, 8, 5, 8)
                .applied(f -> f.putClientProperty("JTextField.placeholderText", placeholder))
                .onLeft()
                .in(this);

        Component gap = Box.createVerticalStrut(2);
        if (gap instanceof JComponent c) c.setAlignmentX(0.0f);

        bar = newArrangedAs(SwingPane.FLOW)
                .withPreferredSize(0, 2)
                .withMaximumSize(Integer.MAX_VALUE, 2)
                .withBackground(ColorStyles.TRACK)
                .onLeft()
                .component();

        SwingPane.fluent(this).arrangedAs(SwingPane.VERTICAL_BOX).collect(field, gap, bar).transparent();

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
                if (onChange != null)
                    onChange.run();
            }
        });
    }

    private void refreshVisuals() {
        String text = getValue();
        Color barColor;
        if (text.isEmpty())
            barColor = ColorStyles.TRACK;
        else if (validator.test(text))
            barColor = ColorStyles.HEALTHY;
        else
            barColor = ColorStyles.CRITICAL;
        bar.setBackground(barColor);
    }

    public void setEditable(boolean editable) {
        field.setEditable(editable);
    }

    public void setValue(String value) {
        field.setText(value);
    }

    public String getValue() {
        return field.withoutWhitespace().stringIfElseBlank(field != null);
    }

    public void setValidator(Predicate<String> v) {
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
