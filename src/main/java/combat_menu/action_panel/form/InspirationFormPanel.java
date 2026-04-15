package combat_menu.action_panel.form;

import __main.manager.InspirationManager;
import format.ColorStyles;
import lombok.*;
import lombok.experimental.*;
import swing.swing_comp.SwingComp;
import swing.swing_comp.SwingPane;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import static format.ColorStyles.*;

@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@NoArgsConstructor(staticName = "newInstance")
public class InspirationFormPanel extends JPanel {

    static final Color FG_HINT = new Color(0x60, 0x58, 0x68);

    {
        SwingPane.modifiable(this).withLayout(SwingPane.BORDER)
                .withBackground(BACKGROUND)
                .opaque()
                .withEmptyBorder(18, 20, 18, 20);

        JPanel header = SwingPane.panelIn(this, BorderLayout.NORTH).withLayout(SwingPane.VERTICAL_BOX)
                .transparent()
                .withEmptyBorder(0, 0, 16, 0)
                .component();

        SwingComp.label("Excess inspiration")
                .asStandardTextSize()
                .withForeground(TEXT_MUTED)
                .onLeft()
                .in(header);

        SwingComp.gapIn(3, header);

        SwingComp.label("Roll 1d4 and select your result")
                .withDerivedFont(Font.PLAIN, 11f)
                .withForeground(ColorStyles.FG_HINT)
                .onLeft()
                .in(header);

        JPanel grid = SwingPane.panelIn(this, BorderLayout.CENTER)
                .withLayout(SwingPane.TWO_COLUMN)
                .transparent()
                .component();

        for (int i = 1; i <= 4; i++)
            grid.add(dieButton(i));
    }

    private JButton dieButton(int value) {
        DieButton btn = new DieButton();

        btn.setLayout(new BorderLayout());
        btn.setForeground(ColorStyles.TEXT_PRIMARY);

        JLabel numLabel = SwingComp.label(String.valueOf(value))
                .withDerivedFont(Font.PLAIN, 28f)
                .withForeground(ColorStyles.TEXT_PRIMARY)
                .component();

        btn.add(numLabel, BorderLayout.CENTER);

        JPanel hintWrap = SwingPane.panelIn(this, BorderLayout.SOUTH)
                .withLayout(SwingPane.FLOW_RIGHT)
                .transparent()
                .component();

        SwingComp.label("d4")
                .withDerivedFont(Font.PLAIN, 10f)
                .withForeground(FG_HINT)
                .withEmptyBorder(0, 0, 6, 8)
                .in(hintWrap);

        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                btn.setBg(ACTION_PRIMARY);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                btn.setBg(DIVIDER);
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                btn.setBg(DIVIDER);
                btn.setBorder(ACTION_HOVER);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                btn.setBg(BG_SURFACE);
                btn.setBorder(DIVIDER);
            }
        });

        btn.addActionListener(e -> InspirationManager.MANAGER.submitExcessRoll(value));
        return btn;
    }

    private static class DieButton extends JButton {

        private Color bgColor = BG_SURFACE;
        private Color borderColor = ACTION_HOVER;

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