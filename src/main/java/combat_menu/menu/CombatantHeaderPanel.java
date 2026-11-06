package combat_menu.menu;

import combat_object.combatant.Combatant;
import combat_object.combatant.PC;
import lombok.*;
import lombok.experimental.*;
import swing.ColorStyles;
import swing.builder.ComponentBuilder;
import util.Filterable;

import javax.swing.*;
import java.awt.*;

import static swing.ColorStyles.*;
import static swing.builder.Components.*;
import static swing.builder.PanelBuilder.*;

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

        modifyBasicPanel(this, VERTICAL_BOX, SPACER_BORDER);
        setAlignmentX(LEFT_ALIGNMENT);

        String classText;
        if (combatant instanceof PC pc) classText = pc.getStats().getClass5e().toString();
        else classText = combatant.isEnemy() ? "Enemy" : "Ally";

        //
        // name row
        Color fg = combatant.isEnemy() ? UNKNOWN : FOREGROUND;
        JLabel nameLabel = ComponentBuilder.getBasicLabel(combatant, Font.PLAIN, 26f, fg);

        JPanel nameRow = row(
                nameLabel, spacer(12, 0), badge(classText), spacer(8, 0)
        );

        //
        // stat row
        inspirationChip = new JPanel();
        hpChip = new JPanel();

        statRow = row(
                statChip("Initiative", "+" + combatant.getInitiative(), false), dividerLine(),
                inspirationChip, dividerLine(),
                hpChip
        );

        //
        // roll row
        rollTrack = new RollTrack();
        rollCaption = ComponentBuilder.getBasicLabel("-", Font.PLAIN, 11f, FG_HINT);

        JLabel luckLabel = ComponentBuilder.getBasicLabel("Roll Luck", Font.PLAIN, 11f, FG_MUTED);

        JPanel rollRow = row(
                luckLabel, spacer(12, 0), rollTrack, spacer(10, 0), rollCaption
        );

        addConstraintlessComponentsTo(this,
                nameRow, spacer(0, 10),
                statRow, spacer(0, 12),
                rollRow
        );

        Filterable.of(getComponents(), JComponent.class).toList().forEach(j -> j.setAlignmentX(LEFT_ALIGNMENT));

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

    private static JPanel row(Object... components) {
        return createPanel(HORIZONTAL_BOX, components).alignmentX(LEFT_ALIGNMENT).build().get();
    }

    private JLabel badge(String text) {
        Color fg = combatant.isEnemy() ? UNKNOWN : FG_MUTED;
        Color border = combatant.isEnemy() ? UNKNOWN : DIVIDER;

        return ComponentBuilder.createLabel(text, Font.PLAIN, 11f, fg)
                .border(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(border, 1),
                        BorderFactory.createEmptyBorder(2, 8, 2, 8)
                )).build().get();
    }

    private static JPanel statChip(String labelText, String value, boolean criticalCondition) {
        JLabel statName = ComponentBuilder.createLabel(labelText.toUpperCase(), Font.PLAIN, 10f, FG_MUTED)
                .alignmentX(LEFT_ALIGNMENT)
                .build().get();

        Color fg = criticalCondition ? CRITICAL : FOREGROUND;
        JLabel statValue = ComponentBuilder.createLabel(value, Font.BOLD, 14f, fg)
                .alignmentX(LEFT_ALIGNMENT)
                .build().get();

        return createPanel(VERTICAL_BOX, statName, statValue).border(SPACER_BORDER).build().get();
    }

    private static Component dividerLine() {
        JPanel d = createPanel(BORDER).background(DIVIDER)
                .fixedSize(new Dimension(1, 28)).build().get();

        return createPanel(BORDER).hostedComponent(d, BorderLayout.CENTER)
                .border(BorderFactory.createEmptyBorder(0, 10, 0, 10))
                .maximumSize(new Dimension(21, 28))
                .alignmentY(CENTER_ALIGNMENT).build().get();
    }

    @FieldDefaults(level = AccessLevel.PRIVATE)
    private static class RollTrack extends JPanel {
        final Timer animationTimer;
        double luckValue = 0;
        double displayLuck = 0;
        Color fillColor = ColorStyles.HEALTHY;

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
            percentage = Math.clamp(percentage, 0, 1.0);

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