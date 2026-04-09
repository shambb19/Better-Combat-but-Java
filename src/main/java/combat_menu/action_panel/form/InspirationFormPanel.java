package combat_menu.action_panel.form;

import __main.manager.InspirationManager;
import format.ColorStyles;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class InspirationFormPanel extends JPanel {

    private static final Color BG = new Color(0x1E, 0x21, 0x28);
    private static final Color BG_BTN = new Color(0x25, 0x29, 0x30);
    private static final Color BG_BTN_HOV = new Color(0x2E, 0x32, 0x40);
    private static final Color BG_BTN_ACT = new Color(0x3A, 0x34, 0x60);
    private static final Color BORDER_BTN = new Color(0x32, 0x36, 0x40);
    private static final Color BORDER_HOV = new Color(0x50, 0x48, 0xA0);
    private static final Color FG_HINT = new Color(0x60, 0x58, 0x68);
    private static final Color FG_TITLE = new Color(0x6B, 0x70, 0x80);
    private static final Color FG_SUBTITLE = new Color(0x50, 0x55, 0x68);

    private final InspirationManager manager;

    public static InspirationFormPanel newInstance() {
        return new InspirationFormPanel();
    }

    public InspirationFormPanel() {
        this.manager = InspirationManager.MANAGER;
        setBackground(BG);
        setOpaque(true);
        setBorder(new EmptyBorder(18, 20, 18, 20));
        setLayout(new BorderLayout(0, 0));

        add(buildHeader(), BorderLayout.NORTH);
        add(buildGrid(), BorderLayout.CENTER);
    }

    private JPanel buildHeader() {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setOpaque(false);
        p.setBorder(new EmptyBorder(0, 0, 16, 0));

        JLabel title = new JLabel("Excess inspiration");
        title.setFont(title.getFont().deriveFont(Font.PLAIN, 12f));
        title.setForeground(FG_TITLE);
        title.setAlignmentX(LEFT_ALIGNMENT);

        JLabel sub = new JLabel("Roll 1d4 and select your result");
        sub.setFont(sub.getFont().deriveFont(Font.PLAIN, 11f));
        sub.setForeground(FG_SUBTITLE);
        sub.setAlignmentX(LEFT_ALIGNMENT);

        p.add(title);
        p.add(Box.createRigidArea(new Dimension(0, 3)));
        p.add(sub);
        return p;
    }

    private JPanel buildGrid() {
        JPanel grid = new JPanel(new GridLayout(2, 2, 8, 8));
        grid.setOpaque(false);

        for (int i = 1; i <= 4; i++) {
            grid.add(dieButton(i));
        }
        return grid;
    }

    private JButton dieButton(int value) {
        DieButton btn = new DieButton();

        btn.setLayout(new BorderLayout());
        btn.setForeground(ColorStyles.TEXT_PRIMARY);

        JLabel numLabel = new JLabel(String.valueOf(value), SwingConstants.CENTER);
        numLabel.setFont(numLabel.getFont().deriveFont(Font.PLAIN, 28f));
        numLabel.setForeground(ColorStyles.TEXT_PRIMARY);
        btn.add(numLabel, BorderLayout.CENTER);

        JPanel hintWrap = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        hintWrap.setOpaque(false);
        JLabel hint = new JLabel("d4");
        hint.setFont(hint.getFont().deriveFont(Font.PLAIN, 10f));
        hint.setForeground(FG_HINT);
        hint.setBorder(new EmptyBorder(0, 0, 6, 8));
        hintWrap.add(hint);
        btn.add(hintWrap, BorderLayout.SOUTH);

        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                btn.setBg(BG_BTN_ACT);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                btn.setBg(BG_BTN_HOV);
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                btn.setBg(BG_BTN_HOV);
                btn.setBorder(BORDER_HOV);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                btn.setBg(BG_BTN);
                btn.setBorder(BORDER_BTN);
            }
        });

        btn.addActionListener(e -> manager.submitExcessRoll(value));
        return btn;
    }

    private static class DieButton extends JButton {

        private Color bgColor = BG_BTN;
        private Color borderColor = BORDER_BTN;

        DieButton() {
            setContentAreaFilled(false);
            setBorderPainted(false);
            setFocusPainted(false);
            setOpaque(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setPreferredSize(new Dimension(0, 80));
        }

        void setBg(Color c) {
            bgColor = c;
            repaint();
        }

        void setBorder(Color c) {
            borderColor = c;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(bgColor);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
            g2.dispose();
            super.paintComponent(g);
        }

        @Override
        protected void paintBorder(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(borderColor);
            g2.setStroke(new BasicStroke(0.5f));
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
            g2.dispose();
        }
    }
}