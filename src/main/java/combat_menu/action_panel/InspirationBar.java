package combat_menu.action_panel;

import __main.InspirationManager;
import format.ColorStyles;

import javax.swing.*;
import java.awt.*;

public class InspirationBar extends JPanel implements InspirationManager.Listener {

    private static final Color BG = new Color(0x16, 0x18, 0x1E);
    private static final Color FILL = new Color(0x7F, 0x77, 0xDD);
    private static final Color TICK_CLR = new Color(0x00, 0x00, 0x00, 100);
    private static final Color FG_LABEL = new Color(0x6B, 0x70, 0x80);
    private static final Color FG_VAL = new Color(0xAF, 0xA9, 0xEC);

    private static final int BAR_H = 8;
    private static final int TICKS = 10;
    private static final int ANIM_MS = 400;
    private static final int ANIM_FPS = 60;

    private final AnimatedBar bar;
    private final JLabel valueLabel;

    public InspirationBar(InspirationManager manager) {
        manager.addListener(this);

        setBackground(BG);
        setOpaque(true);
        setLayout(new BorderLayout(12, 0));
        setPreferredSize(new Dimension(0, 40));

        JLabel title = new JLabel("Excess Inspiration 1d4 Points");
        title.setFont(title.getFont().deriveFont(Font.PLAIN, 12f));
        title.setForeground(FG_LABEL);
        add(title, BorderLayout.WEST);

        bar = new AnimatedBar();
        add(bar, BorderLayout.CENTER);

        valueLabel = new JLabel("0");
        valueLabel.setFont(valueLabel.getFont().deriveFont(Font.PLAIN, 12f));
        valueLabel.setForeground(FG_VAL);
        valueLabel.setPreferredSize(new Dimension(20, 0));
        valueLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        add(valueLabel, BorderLayout.EAST);
    }

    @Override
    public void onCountChanged(int used, int max) {
    }

    @Override
    public void onBarChanged(int total, int max) {
        SwingUtilities.invokeLater(() -> {
            bar.animateTo((float) total / max);
            valueLabel.setText(String.valueOf(total));
        });
    }

    @Override
    public void onBarReset() {
        SwingUtilities.invokeLater(() -> {
            bar.animateTo(1f, () -> {
                bar.animateTo(0f);
                valueLabel.setText("0");
            });
            JOptionPane.showMessageDialog(
                    SwingUtilities.getWindowAncestor(this),
                    "Excess inspiration pool reached 10 — resetting!",
                    "Inspiration Reset",
                    JOptionPane.INFORMATION_MESSAGE
            );
        });
    }

    private static class AnimatedBar extends JPanel {

        private float displayRatio = 0f;   // what is currently painted
        private float fromRatio = 0f;
        private float toRatio = 0f;
        private long animStart = 0;
        private Timer timer = null;
        private Runnable onComplete = null;

        AnimatedBar() {
            setOpaque(false);
            setPreferredSize(new Dimension(0, BAR_H + 4));
        }

        void animateTo(float target) {
            animateTo(target, null);
        }

        void animateTo(float target, Runnable callback) {
            if (timer != null && timer.isRunning()) timer.stop();
            fromRatio = displayRatio;
            toRatio = Math.max(0f, Math.min(1f, target));
            animStart = System.currentTimeMillis();
            onComplete = callback;

            int intervalMs = 1000 / ANIM_FPS;
            timer = new Timer(intervalMs, e -> tick());
            timer.start();
        }

        private void tick() {
            long elapsed = System.currentTimeMillis() - animStart;
            float t = Math.min(1f, (float) elapsed / ANIM_MS);

            float ease = 1f - (float) Math.pow(1f - t, 3);
            displayRatio = fromRatio + (toRatio - fromRatio) * ease;
            repaint();

            if (t >= 1f) {
                timer.stop();
                displayRatio = toRatio;
                if (onComplete != null) {
                    Runnable cb = onComplete;
                    onComplete = null;
                    SwingUtilities.invokeLater(cb);
                }
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();
            int cy = (h - BAR_H) / 2;

            g2.setColor(ColorStyles.TRACK);
            g2.fillRoundRect(0, cy, w, BAR_H, BAR_H, BAR_H);

            int fillW = Math.round(displayRatio * w);
            if (fillW > 0) {
                g2.setColor(FILL);
                g2.fillRoundRect(0, cy, fillW, BAR_H, BAR_H, BAR_H);
            }

            g2.setColor(TICK_CLR);
            for (int i = 1; i < TICKS; i++) {
                int x = Math.round((float) i / TICKS * w);
                g2.fillRect(x, cy, 1, BAR_H);
            }

            g2.dispose();
        }
    }
}