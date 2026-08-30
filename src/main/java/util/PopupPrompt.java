package util;

import lombok.*;

import javax.swing.*;
import javax.swing.border.MatteBorder;
import java.awt.*;
import java.util.Optional;

import static swing.ColorStyles.*;
import static swing.fluent.SwingComp.*;
import static swing.fluent.SwingPane.fluent;
import static swing.fluent.SwingPane.*;

public class PopupPrompt extends JDialog {

    protected JPanel contentArea;
    protected JPanel footer;
    @Getter private int result = -1;

    private PopupPrompt(String title, String message) {
        setModalityType(Dialog.DEFAULT_MODALITY_TYPE);
        setTitle(title);
        setUndecorated(true);
        setLayout(new BorderLayout());
        setBackground(BACKGROUND);
        getRootPane().setBorder(BorderFactory.createLineBorder(TRACK, 1));
        setLocationRelativeTo(null);

        JPanel topBar = panelIn(this, BorderLayout.NORTH).arrangedAs(FLOW_LEFT)
                .withBackground(BG_DARK).component();

        label(title.toUpperCase(), Font.BOLD, 11f).muted().in(topBar);

        contentArea = panelIn(this, BorderLayout.CENTER).arrangedAs(VERTICAL_BOX).spaced()
                .withBackground(BACKGROUND)
                .component();

        footer = panelIn(this, BorderLayout.SOUTH).arrangedAs(FLOW_RIGHT, 5, 0)
                .withBackground(BG_DARK)
                .withBorder(new MatteBorder(1, 0, 0, 0, TRACK))
                .component();

        textArea(message)
                .withDerivedFont(Font.PLAIN, 13f)
                .withMaximumSize(350, Integer.MAX_VALUE)
                .withPreferredSize(350, 60)
                .onLeft()
                .in(contentArea);
    }

    private PopupPrompt(String title, String message, ResultButton... buttons) {
        this(title, message);

        for (ResultButton button : buttons) {
            button(button.text, button.bg,
                    () -> {
                        this.result = button.resultToSet;
                        dispose();
                    }).in(footer);
        }

        pack();
        setVisible(true);
    }

    private PopupPrompt(String title, String message, ActionButton... buttons) {
        this(title, message);

        for (ActionButton button : buttons) {
            Runnable action = () -> {
                Optional.ofNullable(button.action).ifPresent(Runnable::run);
                dispose();
            };
            button(button.text, button.bg, action).in(footer);
        }

        pack();
        setVisible(true);
    }

    private PopupPrompt(String title, String message, JTextField field) {
        this(title, message);

        button("Submit", SUCCESS, this::dispose).in(footer);

        fluent(contentArea).collect(spacer(0, 15), field);

        contentArea.add(Box.createRigidArea(new Dimension(0, 15)));
        contentArea.add(field);

        pack();
        setVisible(true);
    }

    public static PopupPrompt of(String title, String message, ResultButton... buttons) {
        return new PopupPrompt(title, message, buttons);
    }

    public static PopupPrompt of(String title, String message, ActionButton... buttons) {
        return new PopupPrompt(title, message, buttons);
    }

    public static PopupPrompt ofInput(String title, String message, JTextField field) {
        return new PopupPrompt(title, message, field);
    }

    public record ResultButton(String text, Color bg, int resultToSet) {
    }

    public record ActionButton(String text, Color bg, Runnable action) {
    }

}