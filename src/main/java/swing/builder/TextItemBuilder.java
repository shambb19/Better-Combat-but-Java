package swing.builder;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import swing.ColorStyles;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.JTextComponent;
import java.awt.*;

@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@SuperBuilder(builderMethodName = "buildTextItem")
public class TextItemBuilder<S extends JTextComponent> extends ComponentBuilder<S> {

    @Builder.Default String text = "";
    @Builder.Default Color caretColor = ColorStyles.FOREGROUND;

    @Builder.Default Runnable onEdit = () -> {};

    @Builder.Default boolean editable = false;
    @Builder.Default boolean focusable = false;

    @Override public void validated() {
        super.validated();

        if (component.getText() == null || component.getText().isEmpty()) component.setText(text);
        component.setCaretColor(caretColor);

        component.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                onEdit.run();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                onEdit.run();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                onEdit.run();
            }
        });

        component.setEditable(editable);
        component.setFocusable(focusable);
    }

    public static <T extends JTextComponent> TextItemBuilderBuilder<T, ?, ?> modifyingTextItem(T comp) {
        return TextItemBuilder.<T>buildTextItem().component(comp);
    }

    public static TextItemBuilderBuilder<JTextArea, ?, ?> createTextArea(String text) {
        JTextArea area = new JTextArea(text);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);

        return TextItemBuilder.<JTextArea>buildTextItem().component(area);
    }

    public static TextItemBuilderBuilder<JTextArea, ?, ?> createTextArea(String text, Color foreground) {
        return createTextArea(text).foreground(foreground);
    }

}