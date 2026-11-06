package swing.popup;

import __main.Main;
import swing.builder.ButtonBuilder;
import swing.builder.ComponentBuilder;
import swing.builder.Components;
import swing.builder.TextItemBuilder;
import swing.custom.ValidatedField;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.Optional;

import static swing.ColorStyles.*;
import static swing.builder.PanelBuilder.*;

public class PopupPrompt extends JDialog {

    private final JPanel contentArea;
    private String result = null;

    private PopupPrompt(String title, String message) {
        setModalityType(Dialog.DEFAULT_MODALITY_TYPE);
        setUndecorated(true);
        setLayout(new BorderLayout());
        setBackground(BACKGROUND);
        getRootPane().setBorder(BorderFactory.createLineBorder(TRACK, 1));
        setLocationRelativeTo(null);
        setAlwaysOnTop(true);

        JPanel topBar = createPanel(FLOW_LEFT)
                .border(BorderFactory.createEmptyBorder(2, 6, 2, 6))
                .background(BG_DARK).build().putIn(this, BorderLayout.NORTH);

        String titleString = Main.TITLE;
        if (!title.isEmpty()) titleString += " : " + title;
        ComponentBuilder.createLabel(titleString, Font.BOLD, 11f, FG_MUTED) // title
                .build().putIn(topBar);

        contentArea = createPanel(BORDER).vgap(8)
                .border(BorderFactory.createEmptyBorder(8, 4, 8, 4))
                .background(BACKGROUND)
                .build().putIn(this, BorderLayout.CENTER);

        TextItemBuilder.createTextArea(message)
                .fontBuilder(FontBuilder.of(Font.PLAIN, 13f))
                .alignmentX(LEFT_ALIGNMENT)
                .build().putIn(contentArea, BorderLayout.NORTH);

        pack();
    }

    private PopupPrompt(String title, String message, Color buttonColor) {
        this(title, message);

        ButtonBuilder.createButton("Acknowledge", buttonColor, this::dispose).build().putIn(contentArea, BorderLayout.CENTER);
        pack();
    }

    private PopupPrompt(String title, String message, String placeholder, boolean allowStrings) {
        this(title, message + " Press Enter to submit.");

        ValidatedField field;
        if (allowStrings) field = new ValidatedField(placeholder, null);
        else field = new ValidatedField(placeholder, null, Integer.MAX_VALUE);
        field.allowEnter(() -> {
            result = field.getText();
            dispose();
        });

        contentArea.add(field, BorderLayout.CENTER);
        pack();
    }

    private PopupPrompt(String title, String message, ActionButton... actionButtons) {
        this(title, message);

        JPanel buttonPanel = createPanel(HORIZONTAL_BOX).hgap(8)
                .border(BorderFactory.createEmptyBorder(0, 4, 0, 0))
                .build().putIn(contentArea, BorderLayout.CENTER);

        JButton[] buttons = Arrays.stream(actionButtons).map(ab -> {
            Runnable action = () -> {
                Optional.ofNullable(ab.action).ifPresent(Runnable::run);
                dispose();
            };
            return ButtonBuilder.createButton(ab.text, ab.bg, action).build().get();
        }).toList().toArray(new JButton[0]);

        for (JButton b : buttons) addConstraintlessComponentsTo(buttonPanel, b, Components.spacer(8, 0));
        pack();
    }

    public static void showAlert(String title, String message, Color buttonColor) {
        new PopupPrompt(title, message, buttonColor).setVisible(true);
    }

    public static void showActions(String title, String message, ActionButton... buttons) {
        new PopupPrompt(title, message, buttons).setVisible(true);
    }

    public static String showAndGetInput(String title, String message, String placeholder, boolean allowStrings) {
        PopupPrompt prompt = new PopupPrompt(title, message, placeholder, allowStrings);
        prompt.setVisible(true);
        return prompt.result;
    }

    public record ActionButton(String text, Color bg, Runnable action) {
    }

}