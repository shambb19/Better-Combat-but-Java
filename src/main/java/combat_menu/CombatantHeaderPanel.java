package combat_menu;

import combat_object.combatant.Combatant;
import combat_object.combatant.PC;
import format.ColorStyles;
import lombok.*;
import lombok.experimental.*;
import org.intellij.lang.annotations.MagicConstant;
import swing.swing_comp.SwingComp;
import swing.swing_comp.SwingPane;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class CombatantHeaderPanel extends JPanel {

    static Color BD_STANDARD = new Color(0x1E, 0x21, 0x28);
    static Color BG_ENEMY = new Color(0x2A, 0x1E, 0x1E);

    static Color FG_HINT = new Color(0x6B, 0x70, 0x80);
    static Color DIVIDER = new Color(0x2A, 0x2E, 0x3A);

    static Color TRACK_BG = new Color(0x2A, 0x2E, 0x3A);
    static int TRACK_H = 6;
    static int PIP_D = 10;

    JLabel inspirationValue;
    RollTrack rollTrack;
    JLabel rollCaption;
    @Getter Combatant combatant;

    @NonFinal double luck = 0;

    public CombatantHeaderPanel(Combatant combatant) {
        this.combatant = combatant;

        SwingPane.modifiable(this).withLayout(SwingPane.BORDER)
                .withBackground((combatant.isEnemy()) ? BG_ENEMY : BD_STANDARD)
                .opaque()
                .withEmptyBorder(20, 20, 20, 20);

        JPanel stack = SwingPane.panelIn(this, BorderLayout.CENTER)
                .withLayout(SwingPane.VERTICAL_BOX)
                .transparent()
                .component();

        JPanel nameRow = row();
        stack.add(nameRow);
        stack.add(vgap(10));

        JLabel nameLabel = label(combatant.getName(), 26f, Font.PLAIN, ColorStyles.TEXT_PRIMARY);
        nameRow.add(nameLabel);
        nameRow.add(hgap(12));

        String classText = (combatant instanceof PC pc) ? pc.getStats().getClass5e().toString() :
                (combatant.isEnemy() ? "Enemy" : "Ally");

        nameRow.add(badge(classText));
        nameRow.add(hgap(8));

        JPanel statsRow = row();
        stack.add(statsRow);
        stack.add(vgap(12));

        statChip(statsRow, "Initiative", "+" + combatant.getInitiative());
        statsRow.add(dividerLine());

        inspirationValue = statChip(statsRow, "Inspiration used", combatant.getNumInspirationUsed() + " / 2");
        statsRow.add(dividerLine());

        JLabel hpValue = statChip(statsRow, "HP", combatant.getHealthBarString());
        if (combatant.getHpRatio() < 0.35)
            hpValue.setForeground(ColorStyles.CRITICAL);

        JPanel rollRow = row();
        stack.add(rollRow);

        JLabel rollLabel = label("Roll luck", 11f, Font.PLAIN, ColorStyles.TEXT_MUTED);
        rollRow.add(rollLabel);
        rollRow.add(hgap(12));

        rollTrack = new RollTrack();
        rollRow.add(rollTrack);
        rollRow.add(hgap(10));

        rollCaption = label("—", 11f, Font.PLAIN, FG_HINT);
        rollRow.add(rollCaption);

        refresh();
    }

    public void refresh() {
        inspirationValue.setText(combatant.getNumInspirationUsed() + " / 2");
        inspirationValue.setForeground(combatant.getNumInspirationUsed() > 2 ? ColorStyles.CRITICAL : ColorStyles.TEXT_PRIMARY);

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
            caption = "'Tis a good day for " + combatant.getName();
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
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.X_AXIS));
        p.setOpaque(false);
        p.setAlignmentX(LEFT_ALIGNMENT);
        return p;
    }

    private static Component vgap(int h) {
        return Box.createRigidArea(new Dimension(0, h));
    }

    private static Component hgap(int w) {
        return Box.createRigidArea(new Dimension(w, 0));
    }

    private static JLabel label(String text, float size, @MagicConstant(intValues = {Font.PLAIN, Font.BOLD, Font.ITALIC}) int style, Color fg) {
        return SwingComp.label(text).withDerivedFont(style, size).withForeground(fg).component();
    }

    private static JLabel badge(String text) {
        JLabel l = new JLabel(text);
        l.setFont(l.getFont().deriveFont(Font.PLAIN, 11f));
        l.setForeground(ColorStyles.TEXT_MUTED);
        l.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(DIVIDER, 1),
                new EmptyBorder(2, 8, 2, 8)
        ));
        return l;
    }

    private static JLabel statChip(JPanel parent, String labelText, String value) {
        JPanel chip = SwingPane.panel()
                .withLayout(SwingPane.VERTICAL_BOX)
                .withEmptyBorder(20, 20, 20, 20)
                .transparent().component();

        JLabel lbl = label(labelText.toUpperCase(), 10f, Font.PLAIN, ColorStyles.TEXT_MUTED);
        lbl.setAlignmentX(LEFT_ALIGNMENT);
        chip.add(lbl);

        JLabel val = label(value, 14f, Font.BOLD, ColorStyles.TEXT_PRIMARY);
        val.setAlignmentX(LEFT_ALIGNMENT);
        chip.add(val);

        parent.add(chip);
        return val;
    }

    private static Component dividerLine() {
        JPanel d = SwingPane.panel()
                .withPreferredSize(1, 28)
                .withMaximumSize(1, 28)
                .withBackground(DIVIDER)
                .opaque()
                .component();

        return SwingPane.panel().withLayout(SwingPane.BORDER)
                .with(d, BorderLayout.CENTER)
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

            g2.setColor(TRACK_BG);
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
            g2.setColor(BD_STANDARD);
            g2.setStroke(new BasicStroke(2f));
            g2.drawOval(pipX, pipY, PIP_D, PIP_D);

            g2.dispose();
        }
    }
}