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

    public PopupPrompt(String title) {
        setModal(true);
        setTitle(title);
        setUndecorated(true);
        setLayout(new BorderLayout());
        setBackground(BG_DIALOG);
        getRootPane().setBorder(BorderFactory.createLineBorder(BORDER, 1));

        JPanel topBar = SwingPane.panelIn(this, BorderLayout.NORTH).withLayout(SwingPane.FLOW_LEFT)
                .withBackground(BG_BAR).component();

        SwingComp.label(title.toUpperCase()).withDerivedFont(Font.BOLD, 11f)
                .withForeground(ColorStyles.TEXT_MUTED)
                .in(topBar);

        contentArea = SwingPane.panelIn(this, BorderLayout.CENTER)
                .withLayout(SwingPane.VERTICAL_BOX)
                .withBackground(BG_DIALOG)
                .withEmptyBorder(20, 20, 20, 20)
                .component();

        footer = SwingPane.panelIn(this, BorderLayout.SOUTH).withLayout(SwingPane.FLOW_RIGHT)
                .withBackground(BG_BAR)
                .withBorder(new MatteBorder(1, 0, 0, 0, BORDER))
                .component();
    }

    protected void addMessage(String text) {
        SwingComp.label("<html><body style='width: 300px'>", text, "</body></html>").withDerivedFont(Font.PLAIN, 13f)
                .withForeground(ColorStyles.TEXT_PRIMARY).onLeft()
                .in(contentArea);
    }

    protected JButton createButton(String text, Color bg, int resultToSet) {
        return SwingComp.button(text, () -> {
                    this.result = resultToSet;
                    dispose();
                })
                .withBackgroundAndForeground(bg, ColorStyles.TEXT_PRIMARY)
                .withEmptyBorder(8, 16, 8, 16)
                .component();
    }

}