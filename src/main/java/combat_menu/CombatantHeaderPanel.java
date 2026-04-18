package combat_menu;

import combat_object.combatant.Combatant;
import combat_object.combatant.PC;
import format.ColorStyles;
import lombok.*;
import lombok.experimental.*;
import swing.swing_comp.SwingComp;
import swing.swing_comp.SwingPane;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;

import static format.ColorStyles.*;
import static swing.swing_comp.SwingComp.label;
import static swing.swing_comp.SwingPane.*;

@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class CombatantHeaderPanel extends JPanel {

    static Color BG_ENEMY = new Color(0x2A, 0x1E, 0x1E);

    static int TRACK_H = 6;
    static int PIP_D = 10;

    JLabel inspirationValue;
    RollTrack rollTrack;
    JLabel rollCaption;
    @Getter Combatant combatant;

    @NonFinal double luck = 0;

    public CombatantHeaderPanel(Combatant combatant) {
        this.combatant = combatant;

        SwingPane.fluent(this).arrangedAs(BORDER)
                .withBackground((combatant.isEnemy()) ? BG_ENEMY : BACKGROUND)
                .withEmptyBorder(20, 20, 20, 20);

        JPanel stack = SwingPane.panelIn(this, BorderLayout.CENTER).arrangedAs(VERTICAL_BOX)
                .transparent()
                .component();

        JPanel nameRow = row();
        stack.add(nameRow);
        stack.add(SwingComp.spacer(0, 10));

        label(combatant, Font.PLAIN, 26f, ColorStyles.TEXT_PRIMARY)
                .in(nameRow);
        nameRow.add(SwingComp.spacer(12, 0));

        String classText = (combatant instanceof PC pc) ? pc.getStats().getClass5e().toString() :
                (combatant.isEnemy() ? "Enemy" : "Ally");

        nameRow.add(badge(classText));
        nameRow.add(SwingComp.spacer(8, 0));

        JPanel statsRow = row();
        stack.add(statsRow);
        stack.add(SwingComp.spacer(0, 12));

        statChip(statsRow, "Initiative", "+" + combatant.getInitiative());
        statsRow.add(dividerLine());

        inspirationValue = statChip(statsRow, "Inspiration used", combatant.getNumInspirationUsed() + " / 2");
        statsRow.add(dividerLine());

        JLabel hpValue = statChip(statsRow, "HP", combatant.getHealthBarString());
        if (combatant.getHpRatio() < 0.35)
            hpValue.setForeground(ColorStyles.CRITICAL);

        JPanel rollRow = row();
        stack.add(rollRow);

        label("Roll luck", Font.PLAIN, 11f, ColorStyles.TEXT_MUTED)
                .in(rollRow);
        rollRow.add(SwingComp.spacer(12, 0));

        rollTrack = new RollTrack();
        rollRow.add(rollTrack);
        rollRow.add(SwingComp.spacer(10, 0));

        rollCaption = label("-", Font.PLAIN, 11f, TEXT_HINT).in(rollRow);

        refresh();
    }

    public void refresh() {
        inspirationValue.setText(combatant.getNumInspirationUsed() + " / 2");
        inspirationValue.setForeground(combatant.getNumInspirationUsed() > 2 ? ColorStyles.CRITICAL : ColorStyles.TEXT_PRIMARY);

        luck = combatant.getLuckScore();

        Color c;
        String caption;

        if (luck == 0) {
            c = ColorStyles.TEXT_HINT;
            caption = "Too early to tell";
        } else if (luck >= 3.0) {
            c = ColorStyles.PERFECT;
            caption = "Dice of the divine";
        } else if (luck >= 1.0) {
            c = ColorStyles.HEALTHY;
            caption = "'Tis a good day for " + combatant;
        } else if (luck >= -1.0) {
            c = ColorStyles.WARNING;
            caption = (luck >= -0.5) ? "Pretty solid" : "Kinda rough";
        } else {
            c = ColorStyles.CRITICAL;
            caption = "Straight to dice jail";
        }

        SwingUtilities.invokeLater(() -> {
            rollTrack.setLuck(luck, c);
            rollCaption.setText(caption);
            rollCaption.setForeground(c);
        });
    }

    private static JPanel row() {
        return newArrangedAs(HORIZONTAL_BOX).transparent().onLeft().component();
    }

    private static JLabel badge(String text) {
        return label(text, Font.PLAIN, 11f, ColorStyles.TEXT_MUTED)
                .withPaddedBorder(new LineBorder(DIVIDER, 1), 2, 8, 2, 8)
                .component();
    }

    private static JLabel statChip(JPanel parent, String labelText, String value) {
        JPanel chip = SwingPane.panelIn(parent).arrangedAs(VERTICAL_BOX)
                .withEmptyBorder(20, 20, 20, 20)
                .transparent().component();

        label(labelText.toUpperCase(), Font.PLAIN, 10f, ColorStyles.TEXT_MUTED)
                .onLeft().in(chip);

        return label(value, Font.BOLD, 14f, ColorStyles.TEXT_PRIMARY)
                .onLeft().in(chip);
    }

    private static Component dividerLine() {
        JPanel d = newArrangedAs(BORDER)
                .withPreferredSize(1, 28)
                .withMaximumSize(1, 28)
                .withBackground(DIVIDER)
                .component();

        return SwingPane.newArrangedAs(BORDER)
                .borderCollect(center(d))
                .transparent()
                .withEmptyBorder(0, 10, 0, 10)
                .withMaximumSize(21, 28)
                .applied(p -> p.setAlignmentY(CENTER_ALIGNMENT))
                .component();
    }

    private static class RollTrack extends JPanel {
        private final Timer animationTimer;
        private double luckValue = 0;
        private double displayLuck = 0;
        private Color fillColor = ColorStyles.HEALTHY;

        RollTrack() {
            setOpaque(false);
            setPreferredSize(new Dimension(200, PIP_D));
            setMaximumSize(new Dimension(Short.MAX_VALUE, PIP_D));

            animationTimer = new Timer(16, e -> {
                double diff = luckValue - displayLuck;
                if (Math.abs(diff) < 0.01) {
                    displayLuck = luckValue;
                    ((Timer) e.getSource()).stop();
                } else {
                    displayLuck += diff / 8.0;
                }
                repaint();
            });
        }

        void setLuck(double luck, Color color) {
            this.luckValue = luck;
            this.fillColor = color;
            if (!animationTimer.isRunning()) animationTimer.start();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();
            int cy = (h - TRACK_H) / 2;

            g2.setColor(TRACK);
            g2.fillRoundRect(0, cy, w, TRACK_H, TRACK_H, TRACK_H);

            double range = 5.0;
            double percentage = (displayLuck + range) / (range * 2.0);
            percentage = Math.max(0, Math.min(1.0, percentage));

            int fillW = (int) (percentage * w);

            g2.setColor(fillColor);
            g2.fillRoundRect(0, cy, fillW, TRACK_H, TRACK_H, TRACK_H);

            int pipX = fillW - PIP_D / 2;
            int pipY = (h - PIP_D) / 2;

            g2.fillOval(pipX, pipY, PIP_D, PIP_D);
            g2.setColor(BACKGROUND);
            g2.setStroke(new BasicStroke(2f));
            g2.drawOval(pipX, pipY, PIP_D, PIP_D);

            g2.dispose();
        }
    }
}