package combat_menu;

import combat_object.combatant.Combatant;
import combat_object.combatant.PC;
import format.ColorStyles;
import format.swing_comp.SwingPane;
import lombok.*;
import lombok.experimental.*;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;

import static format.ColorStyles.*;
import static format.swing_comp.SwingComp.label;
import static format.swing_comp.SwingComp.spacer;
import static format.swing_comp.SwingPane.*;

@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class CombatantHeaderPanel extends JPanel {

    static int INSPIRATION_INDEX = 2;
    static int HP_INDEX = 4;

    static int TRACK_H = 6;
    static int PIP_D = 10;

    JPanel statRow;
    @NonFinal JPanel inspirationChip;
    @NonFinal JPanel hpChip;
    RollTrack rollTrack;
    JLabel rollCaption;
    @Getter Combatant combatant;

    @NonFinal double luck = 0;

    public CombatantHeaderPanel(Combatant combatant) {
        this.combatant = combatant;

        fluent(this).arrangedAs(VERTICAL_BOX)
                .withBackground((combatant.isEnemy()) ? UNKNOWN : BACKGROUND)
                .withEmptyBorder(20, 20, 20, 20)
                .onLeft();

        String classText = (combatant instanceof PC pc) ? pc.getStats().getClass5e().toString() :
                (combatant.isEnemy() ? "Enemy" : "Ally");

        JPanel nameRow = row().collect(
                label(combatant, Font.PLAIN, 26f, combatant.isEnemy() ? UNKNOWN : FOREGROUND),
                spacer(12, 0), badge(classText), spacer(8, 0)
        ).transparent().component();

        inspirationChip = new JPanel();
        hpChip = new JPanel();

        statRow = row().collect(
                statChip("Initiative", "+" + combatant.getInitiative(), false), dividerLine(),
                inspirationChip, dividerLine(),
                hpChip
        ).transparent().component();

        rollTrack = new RollTrack();
        rollCaption = label("-", Font.PLAIN, 11f, FG_HINT).component();

        JPanel rollRow = row().collect(
                label("Roll Luck", Font.PLAIN, 11f, FG_MUTED), spacer(12, 0),
                rollTrack, spacer(10, 0),
                rollCaption
        ).transparent().component();

        fluent(this).collect(
                nameRow, spacer(0, 10),
                statRow, spacer(0, 12),
                rollRow
        );
        // yet another absolutely heinous solution. Hey, as long as it works??
        for (Component c : getComponents())
            if (c instanceof JComponent j)
                j.setAlignmentX(LEFT_ALIGNMENT);

        refresh();
    }

    public void refresh() {
        statRow.remove(inspirationChip);
        int numInspirations = combatant.getNumInspirationUsed();
        inspirationChip = statChip("Inspiration Used", numInspirations + " / 2", numInspirations > 2);
        statRow.add(inspirationChip, INSPIRATION_INDEX);

        statRow.remove(hpChip);
        hpChip = statChip("HP", combatant.getHealthBarString(), combatant.getHpRatio() < 0.35);
        statRow.add(hpChip, HP_INDEX);

        luck = combatant.getLuckScore();

        Color c;
        String caption;

        if (luck == 0) {
            c = ColorStyles.FG_HINT;
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

    private static SwingPane row() {
        return newArrangedAs(HORIZONTAL_BOX).transparent().onLeft().toPane();
    }

    private JLabel badge(String text) {
        Color fg = combatant.isEnemy() ? UNKNOWN : FG_MUTED;
        Color border = combatant.isEnemy() ? UNKNOWN : DIVIDER;

        return label(text, Font.PLAIN, 11f, fg)
                .withPaddedBorder(new LineBorder(border, 1), 2, 8, 2, 8)
                .component();
    }

    private static JPanel statChip(String labelText, String value, boolean criticalCondition) {
        JPanel chip = newArrangedAs(VERTICAL_BOX)
                .withEmptyBorder(20, 20, 20, 20)
                .transparent().component();

        label(labelText.toUpperCase(), Font.PLAIN, 10f, ColorStyles.FG_MUTED)
                .onLeft().in(chip);

        label(value, Font.BOLD, 14f, criticalCondition ? CRITICAL : FOREGROUND)
                .onLeft()
                .in(chip);

        return chip;
    }

    private static Component dividerLine() {
        JPanel d = newArrangedAs(BORDER)
                .withPreferredSize(1, 28)
                .withMaximumSize(1, 28)
                .withBackground(DIVIDER)
                .component();

        return newArrangedAs(BORDER)
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

        {
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