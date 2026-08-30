package swing.custom;

import exception.InvalidSyntaxError;
import swing.fluent.SwingPane;
import util.StringUtil;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.util.function.Predicate;

import static swing.ColorStyles.*;
import static swing.fluent.SwingComp.fluent;
import static swing.fluent.SwingPane.*;

@lombok.experimental.ExtensionMethod(StringUtil.class)
public class ValidatedField extends JPanel {

    private final JTextComponent field;
    private final JPanel bar;
    private Predicate<String> validator = s -> !s.isBlank();

    public ValidatedField(String placeholder, Runnable onChange, int maxValue) {
        this(placeholder, onChange);
        validator = s -> {
            if (s.isBlank()) return false;
            try {
                int intValue = s.toInt();
                return intValue > -5 && intValue <= maxValue;
            } catch (InvalidSyntaxError ignored) {
                return false;
            }
        };
    }

    public ValidatedField(String placeholder, Runnable onChange) {
        field = fluent(new JTextField())
                .withBackground(TRACK)
                .withDerivedFont(Font.PLAIN, 13f)
                .withPaddedBorder(new LineBorder(DIVIDER, 1), 5, 8, 5, 8)
                .applied(f -> f.putClientProperty("JTextField.placeholderText", placeholder))
                .onLeft()
                .in(this);

        Component gap = Box.createVerticalStrut(2);
        if (gap instanceof JComponent c) c.setAlignmentX(0.0f);

        bar = newArrangedAs(FLOW)
                .withPreferredSize(0, 2)
                .withMaximumSize(Integer.MAX_VALUE, 2)
                .withBackground(TRACK)
                .onLeft()
                .component();

        SwingPane.fluent(this).arrangedAs(VERTICAL_BOX).collect(field, gap, bar).transparent();

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
            barColor = TRACK;
        else if (validator.test(text))
            barColor = HEALTHY;
        else
            barColor = CRITICAL;
        bar.setBackground(barColor);
    }

    @Override public void setEnabled(boolean enabled) {
        field.setEnabled(enabled);
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
        return validator != null && validator.test(getValue());
    }

    public void clear() {
        field.setText("");
    }
}
