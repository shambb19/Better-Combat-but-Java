package util;

import format.ColorStyles;
import lombok.*;
import swing.swing_comp.SwingComp;
import swing.swing_comp.SwingPane;

import javax.swing.*;
import javax.swing.border.MatteBorder;
import java.awt.*;

public class PopupPrompt extends JDialog {

    private static final Color
            BG_DIALOG = new Color(0x1E, 0x21, 0x28),
            BG_BAR = new Color(0x19, 0x1C, 0x22),
            BORDER = new Color(0x2A, 0x2E, 0x3A);

    protected JPanel contentArea;
    protected JPanel footer;
    @Getter private int result = -1;

    private PopupPrompt(String title, String message, PromptButton... buttons) {
        setModal(true);
        setTitle(title);
        setUndecorated(true);
        setLayout(new BorderLayout());
        setBackground(BG_DIALOG);
        getRootPane().setBorder(BorderFactory.createLineBorder(BORDER, 1));
        setLocationRelativeTo(null);

        JPanel topBar = SwingPane.panelIn(this, BorderLayout.NORTH).arrangedAs(SwingPane.FLOW_LEFT)
                .withBackground(BG_BAR).component();

        SwingComp.label(title.toUpperCase(), Font.BOLD, 11f, ColorStyles.TEXT_MUTED).in(topBar);

        contentArea = SwingPane.panelIn(this, BorderLayout.CENTER).arrangedAs(SwingPane.VERTICAL_BOX)
                .withBackground(BG_DIALOG)
                .withEmptyBorder(20, 20, 20, 20)
                .component();

        footer = SwingPane.panelIn(this, BorderLayout.SOUTH).arrangedAs(SwingPane.FLOW_RIGHT)
                .withBackground(BG_BAR)
                .withBorder(new MatteBorder(1, 0, 0, 0, BORDER))
                .component();

        SwingComp.label("<html><body style='width: 300px'>" + message + "</body></html>",
                        Font.PLAIN, 13f, ColorStyles.TEXT_PRIMARY)
                .onLeft().in(contentArea);

        for (PromptButton button : buttons) {
            SwingComp.button(button.text, button.bg,
                    () -> {
                        this.result = button.resultToSet;
                        dispose();
                    }).in(footer);
        }

        pack();
        setVisible(true);
    }

    public static PopupPrompt of(String title, String message, PromptButton... buttons) {
        return new PopupPrompt(title, message, buttons);
    }

    @Value @AllArgsConstructor public static class PromptButton {
        String text;
        Color bg;
        int resultToSet;
    }

}