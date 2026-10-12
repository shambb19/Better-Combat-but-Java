package swing.custom;

import exception.InvalidSyntaxError;
import lombok.AccessLevel;
import lombok.experimental.ExtensionMethod;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import swing.builder.TextItemBuilder;
import util.StringUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.Optional;
import java.util.function.Predicate;

import static swing.ColorStyles.*;
import static swing.builder.PanelBuilder.*;

@ExtensionMethod(StringUtil.class)
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class ValidatedField extends JTextField {

    @NonFinal Predicate<String> validator = s -> !s.isBlank();

    public ValidatedField(String placeholder, Runnable onChange) {
        TextItemBuilder.modifyingTextItem(this)
                .clientProperty("JTextField.placeholderText", placeholder)
                .clientProperty("JTextField.padding", new Insets(0, 8, 0, 8))
                .onEdit(() -> {
                    refreshVisuals();
                    Optional.ofNullable(onChange).ifPresent(Runnable::run);
                }).fontBuilder(FontBuilder.of(Font.PLAIN, 13f))
                .background(TRACK)
                .border(BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(0, 3, 0, 0, BG_DEEP),
                        BorderFactory.createEmptyBorder(6, 0, 6, 0)
                )).alignmentX(LEFT_ALIGNMENT)
                .fixedHeight(40)
                .editable(true)
                .focusable(true).build().validated();

        addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                setBackground(BORDER_LIGHT);
                setCaretColor(BORDER_LIGHT);
            }

            @Override
            public void focusLost(FocusEvent e) {
                setBackground(TRACK);
                setCaretColor(TRACK);
            }
        });
    }

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

    private void refreshVisuals() {
        Color color = isInputValid() ? HEALTHY : WARNING;
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 3, 0, 0, color),
                BorderFactory.createEmptyBorder(6, 0, 6, 0)
        ));
        repaint();
    }

    public void allowEnter(Runnable onEnter) {
        addActionListener(e -> onEnter.run());
    }

    public void setValue(String value) {
        setText(value);
        refreshVisuals();
    }

    public String getValue() {
        if (getText() == null) return "";
        return getText().trim();
    }

    public void setValidator(Predicate<String> v) {
        this.validator = v;
        refreshVisuals();
    }

    public boolean isInputValid() {
        return validator != null && validator.test(getValue());
    }

    public void clear() {
        setValue("");
    }
}