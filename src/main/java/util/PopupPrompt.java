package util;

import format.ColorStyles;
import swing.swing_comp.SwingPane;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class PopupPrompt extends JDialog {
    private static final Color BG_DIALOG = new Color(0x1E, 0x21, 0x28);
    private static final Color BG_BAR = new Color(0x19, 0x1C, 0x22);
    private static final Color BORDER = new Color(0x2A, 0x2E, 0x3A);

    protected JPanel contentArea;
    protected JPanel footer;
    private int result = -1;

    public PopupPrompt(String title) {
        setModal(true);
        setTitle(title);
        setUndecorated(true);
        setLayout(new BorderLayout());
        setBackground(BG_DIALOG);
        getRootPane().setBorder(BorderFactory.createLineBorder(BORDER, 1));

        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        topBar.setBackground(BG_BAR);

        JLabel lblTitle = new JLabel(title.toUpperCase());
        lblTitle.setFont(lblTitle.getFont().deriveFont(Font.BOLD, 11f));
        lblTitle.setForeground(ColorStyles.TEXT_MUTED);
        topBar.add(lblTitle);
        add(topBar, BorderLayout.NORTH);

        contentArea = SwingPane.panelIn(this, BorderLayout.CENTER)
                .withLayout(SwingPane.VERTICAL_BOX)
                .withBackground(BG_DIALOG)
                .withEmptyBorder(20)
                .build();

        footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        footer.setBackground(BG_BAR);
        footer.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, BORDER));
        add(footer, BorderLayout.SOUTH);
    }

    protected void addMessage(String text) {
        JLabel lbl = new JLabel("<html><body style='width: 300px'>" + text + "</body></html>");
        lbl.setForeground(ColorStyles.TEXT_PRIMARY);
        lbl.setFont(lbl.getFont().deriveFont(13f));
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentArea.add(lbl);
    }

    protected JButton createButton(String text, Color bg, int resultToSet) {
        JButton btn = new JButton(text);
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(new EmptyBorder(8, 16, 8, 16));
        btn.addActionListener(e -> {
            this.result = resultToSet;
            dispose();
        });
        return btn;
    }

    public int getResult() {
        return result;
    }
}