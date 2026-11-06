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

    /**
     * Applies all changes added in the builder to this TextItemBuilder's
     * component. This method, or one of the other inherited ComponentBuilder
     * validator methods (putIn, validated, get), are required for the
     * TextItemBuilder class to function
     */
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

    /**
     * Returns a fluent TextItemBuilderBuilder object with the component set equal
     * to the given JTextComponent. This method should only be used when a specific
     * method does not exist for the JTextComponent being created. For example, this
     * method should not be used to create a JTextArea.
     * @param comp the button to be set as the hosted component of
     *               the new TextItemBuilderBuilder object
     * @return the new ButtonBuilderBuilder object
     * @param <T> the subclass of JTextComponent that is the type of the button
     *           to be added to the new TextItemBuilderBuilder object
     */
    public static <T extends JTextComponent> TextItemBuilderBuilder<T, ?, ?> modifyingTextItem(T comp) {
        return TextItemBuilder.<T>buildTextItem().component(comp);
    }

    /**
     * Returns a fluent TextItemBuilderBuilder object with the component set to a new
     * JTextArea created with the given text. The text area is automatically set with
     * setLineWrap=true and setWrapStyleWord=true
     * @param text the text to be displayed in the text area
     * @return the new TextItemBuilderBuilder object
     */
    public static TextItemBuilderBuilder<JTextArea, ?, ?> createTextArea(String text) {
        JTextArea area = new JTextArea(text);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);

        return TextItemBuilder.<JTextArea>buildTextItem().component(area);
    }

    /**
     * Returns a fluent TextItemBuilderBuilder object with the component set to a new
     * JTextArea created with the given text and foreground. The text area is automatically
     * set with setLineWrap=true and setWrapStyleWord=true
     * @param text the text to be displayed in the text area
     * @param foreground the foreground color of the text area
     * @return the new TextItemBuilderBuilder object
     */
    public static TextItemBuilderBuilder<JTextArea, ?, ?> createTextArea(String text, Color foreground) {
        return createTextArea(text).foreground(foreground);
    }

}