package combat_menu;

import __main.manager.InspirationManager;
import format.ColorStyles;
import lombok.*;
import lombok.experimental.*;
import swing.swing_comp.SwingComp;

import javax.swing.*;
import java.awt.*;

@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@NoArgsConstructor(staticName = "newInstance", force = true)
public class InspirationBar extends JPanel implements InspirationManager.Listener {

    static int
            BAR_H = 8,
            TICKS = 10,
            ANIM_MS = 400,
            ANIM_FPS = 60;

    AnimatedBar bar = new AnimatedBar();
    JLabel valueLabel = new JLabel();

    {
        InspirationManager.MANAGER.addListener(this);

        setBackground(ColorStyles.BG_DEEP);
        setOpaque(true);
        setLayout(new BorderLayout(12, 0));
        setPreferredSize(new Dimension(0, 40));

        SwingComp.label("Excess Inspiration 1d4 Points")
                .asStandardTextSize()
                .withForeground(ColorStyles.TEXT_MUTED)
                .in(this, BorderLayout.WEST);

        add(bar, BorderLayout.CENTER);

        SwingComp.modifiable(valueLabel)
                .asStandardTextSize()
                .withForeground(new Color(0xAF, 0xA9, 0xEC))
                .withPreferredSize(20, 0)
                .applied(l -> l.setHorizontalAlignment(SwingConstants.RIGHT))
                .in(this, BorderLayout.EAST);
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

    @FieldDefaults(level = AccessLevel.PRIVATE)
    private static class AnimatedBar extends JPanel {

        float displayRatio = 0f, fromRatio = 0f, toRatio = 0f;
        long animStart = 0;
        Timer timer = null;
        Runnable onComplete = null;

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
                g2.setColor(ColorStyles.PURPLE_FILL);
                g2.fillRoundRect(0, cy, fillW, BAR_H, BAR_H, BAR_H);
            }

            g2.setColor(new Color(0, 0, 0, 100)); // Tick color
            for (int i = 1; i < TICKS; i++) {
                int x = Math.round((float) i / TICKS * w);
                g2.fillRect(x, cy, 1, BAR_H);
            }

            g2.dispose();
        }
    }
}