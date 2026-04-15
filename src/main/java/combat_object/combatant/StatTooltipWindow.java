package combat_object.combatant;

import format.ColorStyles;
import swing.swing_comp.SwingComp;
import swing.swing_comp.SwingPane;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.border.MatteBorder;
import java.awt.*;

public class StatTooltipWindow extends JWindow {

    private static final int WIDTH = 200;

    public StatTooltipWindow(Window owner, Combatant combatant) {
        super(owner);
        setBackground(new Color(0, 0, 0, 0));

        JPanel root = SwingPane.panel().withLayout(SwingPane.BORDER)
                .with(buildHeader(combatant), BorderLayout.NORTH)
                .with(buildBody(combatant), BorderLayout.CENTER)
                .withBackground(ColorStyles.BG_DEEP)
                .withBorder(new LineBorder(ColorStyles.DIVIDER, 1))
                .applied(this::setContentPane)
                .component();

        if (combatant.isEnemy())
            root.add(buildFooter(), BorderLayout.SOUTH);

        pack();
        setSize(WIDTH, getPreferredSize().height);
    }

    private static String roughHpLabel(Combatant c) {
        if (!c.getLifeStatus().isConscious()) return "Defeated";
        double ratio = c.getHpRatio();
        if (ratio > 0.7) return "Healthy";
        if (ratio > 0.50) return "Injured";
        if (ratio > 0.25) return "Bloodied";
        return "Critical";
    }

    private JPanel buildHeader(Combatant c) {
        JPanel header = SwingPane.panel().withLayout(SwingPane.VERTICAL_BOX)
                .withBackground(ColorStyles.BG_DEEP)
                .withPaddedMatteBorderOnSide(ColorStyles.DIVIDER, SwingComp.BOTTOM, 8, 12, 7, 12)
                .component();

        SwingComp.label(c.getName()).withDerivedFont(Font.PLAIN, 13f)
                .withForeground(ColorStyles.TEXT_PRIMARY)
                .in(header);

        String subtitle = c.isEnemy() ? "Enemy"
                : (c instanceof PC pc
                ? pc.getStats().getClass5e().toString() + " · Level " + pc.getStats().getLevel()
                : "Ally");

        SwingComp.label(subtitle).withDerivedFont(Font.PLAIN, 10f)
                .withForeground(ColorStyles.TEXT_MUTED)
                .in(header);

        return header;
    }

    private JPanel buildBody(Combatant c) {
        JPanel body = SwingPane.panel().withLayout(SwingPane.VERTICAL_BOX)
                .withBackground(ColorStyles.BG_DEEP)
                .withEmptyBorder(4, 12, 8, 12)
                .component();

        buildStats(body, c);

        return body;
    }

    private void buildStats(JPanel body, Combatant c) {
        sectionLabel(body, "Combat");
        statRow(body, "AC", String.valueOf(c.getArmorClass()));

        if (c.isEnemy())
            statRow(body, "HP", roughHpLabel(c), c.getHealthBarColor());
        else
            statRow(body, "HP", c.getHealthBarString(), c.getHealthBarColor());

        statRow(body, "Initiative", "+" + c.getInitiative());

        if (c instanceof PC pc) {
            Stats stats = pc.getStats();

            sectionLabel(body, "Ability scores");
            for (AbilityModifier stat : AbilityModifier.values())
                if (!stat.equals(AbilityModifier.OPTION))
                    statRow(body, stat.name().toUpperCase(), String.valueOf(stats.get(stat)));
        }
    }

    private JPanel buildFooter() {
        JPanel footer = SwingPane.panel().withLayout(SwingPane.FLOW_LEFT)
                .withBackground(ColorStyles.BG_DARK)
                .withPaddedMatteBorderOnSide(ColorStyles.DIVIDER, SwingComp.TOP, 5, 12, 5, 12)
                .component();

        SwingComp.label("HP unknown until defeated")
                .withDerivedFont(Font.ITALIC, 10f)
                .withForeground(ColorStyles.FG_HINT)
                .in(footer);

        return footer;
    }

    private void sectionLabel(JPanel parent, String text) {
        SwingComp.label(text.toUpperCase()).withDerivedFont(Font.PLAIN, 9f)
                .withForeground(ColorStyles.FG_HINT)
                .onLeft()
                .withEmptyBorder(6, 0, 3, 0)
                .in(parent);
    }

    private void statRow(JPanel parent, String label, String value) {
        statRow(parent, label, value, ColorStyles.TEXT_PRIMARY);
    }

    private void statRow(JPanel parent, String label, String value, Color valueColor) {
        JPanel row = SwingPane.panelIn(parent).withLayout(SwingPane.BORDER)
                .opaque()
                .withBackground(ColorStyles.BG_DEEP)
                .onLeft()
                .withMaximumSize(Integer.MAX_VALUE, 22)
                .withBorder(new MatteBorder(0, 0, 1, 0, ColorStyles.BACKGROUND))
                .component();

        SwingComp.label(label).withDerivedFont(Font.PLAIN, 11f)
                .withForeground(ColorStyles.TEXT_MUTED).in(row, BorderLayout.WEST);

        SwingComp.label(value).withDerivedFont(Font.BOLD, 11f)
                .withForeground(valueColor).in(row, BorderLayout.EAST);
    }
}