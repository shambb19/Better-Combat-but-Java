package combat_menu.action_panel.form;

import _manager.InspirationManager;
import lombok.*;
import lombok.experimental.*;
import swing.builder.ComponentBuilder;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import static swing.ColorStyles.*;
import static swing.builder.PanelBuilder.*;

@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class InspirationFormPanel extends JPanel {

    {
        modifyBasicPanel(this, BORDER, SPACER_BORDER);

        ComponentBuilder.createLabel("Roll Spooky 1d4 Inspiration", Font.PLAIN, 12f, FOREGROUND)
                .alignmentX(LEFT_ALIGNMENT)
                .border(BorderFactory.createEmptyBorder(0, 0, 16, 0))
                .build().putIn(this, BorderLayout.NORTH);

        JPanel grid = createPanel(TWO_COLUMN).build().putIn(this, BorderLayout.CENTER);

        for (int i = 1; i <= 4; i++)
            grid.add(dieButton(i));
    }

    private JButton dieButton(int value) {
        DieButton dieButton = new DieButton();

        dieButton.setLayout(new BorderLayout());
        dieButton.setForeground(FOREGROUND);

        JLabel label = ComponentBuilder.createLabel(String.valueOf(value), Font.PLAIN, 28f, FOREGROUND)
                .build().putIn(dieButton, BorderLayout.CENTER);

        JPanel hintWrap = createPanel(FLOW_RIGHT).build().putIn(this, BorderLayout.SOUTH);

        ComponentBuilder.createLabel("d4", Font.PLAIN, 10f, FG_HINT)
                .border(BorderFactory.createEmptyBorder(0, 0, 6, 0))
                .build().putIn(hintWrap);

        MouseListener mouseListener = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                InspirationManager.MANAGER.submitExcessRoll(value);
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                dieButton.setBg(DIVIDER);
                dieButton.setBorder(ACTION_HOVER);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                dieButton.setBg(BG_SURFACE);
                dieButton.setBorder(DIVIDER);
            }
        };
        dieButton.addMouseListener(mouseListener);
        label.addMouseListener(mouseListener);

        return dieButton;
    }

    private static class DieButton extends JButton {
        private Color bgColor = BG_SURFACE;
        private Color borderColor = DIVIDER;

        DieButton() {
            setPreferredSize(new Dimension(0, 80));
            setContentAreaFilled(false);
            setBorderPainted(false);
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