package combat_menu;

import character_info.combatant.Combatant;
import character_info.combatant.PC;
import format.ColorStyles;
import org.intellij.lang.annotations.MagicConstant;
import swing.swing_comp.SwingPane;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class CombatantHeaderPanel extends JPanel {

    private static final Color BG_DEFAULT = new Color(0x1E, 0x21, 0x28);
    private static final Color BG_ENEMY = new Color(0x2A, 0x1E, 0x1E);

    private static final Color FG_PRIMARY = new Color(0xD8, 0xDC, 0xE8);
    private static final Color FG_MUTED = new Color(0x50, 0x55, 0x68);
    private static final Color FG_HINT = new Color(0x6B, 0x70, 0x80);
    private static final Color DIVIDER = new Color(0x2A, 0x2E, 0x3A);

    private static final Color COLOR_WARN = new Color(0xEF, 0x9F, 0x27);
    private static final Color COLOR_CRITICAL = new Color(0xE2, 0x4B, 0x4A);
    private static final Color COLOR_GOOD = new Color(0x1D, 0x9E, 0x75);

    private static final Color TRACK_BG = new Color(0x2A, 0x2E, 0x3A);
    private static final int TRACK_H = 6;
    private static final int PIP_D = 10;

    private double rollQuality = -1;

    private final JLabel inspirationValue;
    private final RollTrack rollTrack;
    private final JLabel rollCaption;

    private final Combatant combatant;

    public CombatantHeaderPanel(Combatant combatant) {
        this.combatant = combatant;

        SwingPane.modifiable(this).withLayout(SwingPane.BORDER)
                .withBackground((combatant.isEnemy()) ? BG_ENEMY : BG_DEFAULT)
                .opaque()
                .withEmptyBorder(20);

        JPanel stack = SwingPane.panelIn(this, BorderLayout.CENTER)
                .withLayout(SwingPane.VERTICAL_BOX)
                .transparent()
                .build();

        JPanel nameRow = row();
        stack.add(nameRow);
        stack.add(vgap(10));

        JLabel nameLabel = label(combatant.name(), 26f, Font.PLAIN, FG_PRIMARY);
        nameRow.add(nameLabel);
        nameRow.add(hgap(12));

        String classText;
        if (combatant instanceof PC pc)
            classText = pc.stats().class5e().toString();
        else if (combatant.isEnemy())
            classText = "Enemy";
        else
            classText = "Ally";

        JLabel classBadge = badge(classText);
        nameRow.add(classBadge);
        nameRow.add(hgap(8));

        rollQuality = combatant.getRollQuality();

        JPanel statsRow = row();
        stack.add(statsRow);
        stack.add(vgap(12));

        statChip(statsRow, "Initiative", "+" + combatant.initiative());
        statsRow.add(dividerLine());

        inspirationValue = statChip(statsRow, "Inspiration used", combatant.numInspirationUsed() + " / 2");
        if (combatant.numInspirationUsed() > 2) inspirationValue.setForeground(COLOR_CRITICAL);
        statsRow.add(dividerLine());

        JLabel hpValue = statChip(statsRow, "HP", combatant.getHealthBarString());
        if ((double) combatant.hp() / combatant.maxHp() < 0.35) hpValue.setForeground(COLOR_CRITICAL);

        JPanel rollRow = row();
        stack.add(rollRow);

        JLabel rollLabel = label("Roll quality", 11f, Font.PLAIN, FG_MUTED);
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
        inspirationValue.setText(combatant.numInspirationUsed() + " / 2");
        if (combatant.numInspirationUsed() > 2)
            inspirationValue.setForeground(COLOR_CRITICAL);

        rollQuality = combatant.getRollQuality();

        if (rollQuality < 0) {
            rollTrack.setQuality(-1, TRACK_BG);
            rollCaption.setText("Too early to tell");
            rollCaption.setForeground(FG_HINT);
            return;
        }
        Color c;
        String caption;

        if (rollQuality >= 0.9) {
            c = ColorStyles.PARTY;
            caption = "Dice of the divine";
        } else if (rollQuality >= 0.65) {
            c = COLOR_GOOD;
            caption = "'Tis a good day for " + combatant.name();
        } else if (rollQuality >= 0.35) {
            c = COLOR_WARN;
            caption = "Solid";
        } else {
            c = COLOR_CRITICAL;
            caption = "Straight to dice jail";
        }

        SwingUtilities.invokeLater(() -> {
            rollTrack.setQuality(rollQuality, c);
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

    private static JLabel label(
            String text,
            float size,
            @MagicConstant(intValues = {Font.PLAIN, Font.BOLD, Font.ITALIC}) int style,
            Color fg
    ) {
        JLabel l = new JLabel(text);
        l.setFont(l.getFont().deriveFont(style, size));
        l.setForeground(fg);
        return l;
    }

    private static JLabel statChip(JPanel parent, String labelText, String value) {
        JPanel chip = SwingPane.panel()
                .withLayout(SwingPane.VERTICAL_BOX)
                .withEmptyBorder(20)
                .transparent().build();

        JLabel lbl = label(labelText.toUpperCase(), 10f, Font.PLAIN, FG_MUTED);
        lbl.setAlignmentX(LEFT_ALIGNMENT);
        chip.add(lbl);

        JLabel val = label(value, 14f, Font.BOLD, FG_PRIMARY);
        val.setAlignmentX(LEFT_ALIGNMENT);
        chip.add(val);

        parent.add(chip);
        return val;
    }

    private static JLabel badge(String text) {
        JLabel l = new JLabel(text);
        l.setFont(l.getFont().deriveFont(Font.PLAIN, 11f));
        l.setForeground(CombatantHeaderPanel.FG_MUTED);
        l.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(CombatantHeaderPanel.DIVIDER, 1),
                new EmptyBorder(2, 8, 2, 8)
        ));
        return l;
    }

    private static Component dividerLine() {
        JPanel d = new JPanel();
        d.setPreferredSize(new Dimension(1, 28));
        d.setMaximumSize(new Dimension(1, 28));
        d.setBackground(DIVIDER);
        d.setOpaque(true);

        JPanel wrap = new JPanel(new BorderLayout());
        wrap.setOpaque(false);
        wrap.setBorder(new EmptyBorder(0, 10, 0, 10));
        wrap.setMaximumSize(new Dimension(21, 28));
        wrap.setAlignmentY(CENTER_ALIGNMENT);
        wrap.add(d, BorderLayout.CENTER);
        return wrap;
    }

    private static Component hgap(int w) {
        return Box.createRigidArea(new Dimension(w, 0));
    }

    private static Component vgap(int h) {
        return Box.createRigidArea(new Dimension(0, h));
    }

    public Combatant getCombatant() {
        return combatant;
    }

    private static class RollTrack extends JPanel {

        private double targetQuality = -1.0;
        private double currentDisplayWidth = 0;
        private Color fillColor = TRACK_BG;

        private final Timer animationTimer;

        RollTrack() {
            setOpaque(false);
            setPreferredSize(new Dimension(200, PIP_D));
            setMaximumSize(new Dimension(Short.MAX_VALUE, PIP_D));
            setMinimumSize(new Dimension(60, PIP_D));

            animationTimer = new Timer(16, e -> {
                int targetPixels = (int) Math.round(targetQuality * getWidth());
                double diff = targetPixels - currentDisplayWidth;

                if (Math.abs(diff) < 0.5) {
                    currentDisplayWidth = targetPixels;
                    ((Timer) e.getSource()).stop();
                } else {
                    currentDisplayWidth += diff / 6.0;
                }
                repaint();
            });
        }

        void setQuality(double q, Color color) {
            this.targetQuality = q;
            this.fillColor = color;

            if (q < 0) {
                if (animationTimer.isRunning()) animationTimer.stop();
                currentDisplayWidth = 0;
                repaint();
                return;
            }

            if (!animationTimer.isRunning()) {
                animationTimer.start();
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int cy = (getHeight() - TRACK_H) / 2;
            int tw = getWidth();

            g2.setColor(TRACK_BG);
            g2.fillRoundRect(0, cy, tw, TRACK_H, TRACK_H, TRACK_H);

            if (targetQuality >= 0f) {
                int fillW = (int) Math.round(currentDisplayWidth);

                fillW = Math.max(0, Math.min(fillW, tw));

                g2.setColor(fillColor);
                g2.fillRoundRect(0, cy, fillW, TRACK_H, TRACK_H, TRACK_H);

                int px = fillW - PIP_D / 2;
                int py = (getHeight() - PIP_D) / 2;

                g2.setColor(fillColor);
                g2.fillOval(px, py, PIP_D, PIP_D);

                g2.setColor(new Color(0x1E, 0x21, 0x28));
                g2.setStroke(new BasicStroke(2f));
                g2.drawOval(px, py, PIP_D, PIP_D);
            }

            g2.dispose();
        }
    }
}
