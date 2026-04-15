package swing;

import lombok.*;

import javax.swing.*;
import java.awt.*;

@RequiredArgsConstructor
public class RoundPanel extends JPanel {

    private final int radius;
    @NonNull private Color bg;

    private Timer timer;

    {
        setOpaque(false);
    }

    public void setFill(Color c) {
        this.bg = c;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(bg);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius * 2, radius * 2);
        g2.dispose();
    }

    public void fireSlideAdjust(int targetWidth, int height, JComponent track) {
        if (timer != null && timer.isRunning()) {
            timer.stop();
        }

        timer = new Timer(16, e -> {
            int current = getWidth();

            if (current == targetWidth) {
                ((Timer) e.getSource()).stop();
                return;
            }

            int diff = Math.abs(targetWidth - current);
            int step = Math.max(1, diff / 6);
            int next = current < targetWidth ? current + step : current - step;

            setBounds(0, 0, next, height);

            if (track != null) {
                track.repaint();
            }
        });

        timer.start();
    }
}
