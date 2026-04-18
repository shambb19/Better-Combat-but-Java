package combat_menu.encounter_info;

import combat_object.combatant.Combatant;
import combat_object.combatant.PC;
import combat_object.combatant.info.AbilityModifier;
import combat_object.combatant.info.Stats;
import format.ColorStyles;
import swing.swing_comp.SwingComp;
import swing.swing_comp.SwingPane;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.border.MatteBorder;
import java.awt.*;

import static swing.swing_comp.SwingPane.*;

public class StatTooltipWindow extends JWindow {

    private static final int WIDTH = 200;

    private final Combatant combatant;

    public StatTooltipWindow(Window owner, Combatant combatant) {
        super(owner);
        this.combatant = combatant;

        setBackground(new Color(0, 0, 0, 0));

        JPanel root = newArrangedAs(SwingPane.BORDER)
                .borderCollect(
                        north(getHeader()), center(getBody())
                ).withBackground(ColorStyles.BG_DEEP)
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

    private JPanel getHeader() {
        JPanel header = newArrangedAs(VERTICAL_BOX)
                .withBackground(ColorStyles.BG_DEEP)
                .withPaddedMatteBorderOnSide(ColorStyles.DIVIDER, BOTTOM, 8, 12, 7, 12)
                .component();

        label(combatant, Font.PLAIN, 13f, ColorStyles.TEXT_PRIMARY).in(header);

        String subtitle = combatant.isEnemy() ? "Enemy"
                : (combatant instanceof PC pc
                ? pc.getStats().getClass5e().toString() + " · Level " + pc.getStats().getLevel()
                : "Ally");

        label(subtitle, Font.PLAIN, 10f, ColorStyles.TEXT_MUTED).in(header);

        return header;
    }

    private JPanel getBody() {
        JPanel statPanel = newArrangedAs(VERTICAL_BOX)
                .collect(
                        sectionLabel("Combat"), getStatRow("AC", String.valueOf(combatant.getArmorClass()))
                ).withBackground(ColorStyles.BG_DEEP)
                .withEmptyBorder(4, 12, 8, 12)
                .component();

        if (combatant.isEnemy())
            getStatRow("HP", roughHpLabel(combatant), combatant.getHealthBarColor()).in(statPanel);
        else
            getStatRow("HP", combatant.getHealthBarString(), combatant.getHealthBarColor()).in(statPanel);

        getStatRow("Initiative", "+" + combatant.getInitiative()).in(statPanel);

        if (combatant instanceof PC pc) {
            Stats stats = pc.getStats();

            sectionLabel("Ability scores").in(statPanel);
            for (AbilityModifier stat : AbilityModifier.values())
                if (!stat.equals(AbilityModifier.OPTION))
                    getStatRow(stat.name().toUpperCase(), String.valueOf(stats.get(stat))).in(statPanel);
        }

        return statPanel;
    }

    private JPanel buildFooter() {
        JPanel footer = newArrangedAs(FLOW_LEFT)
                .withBackground(ColorStyles.BG_DARK)
                .withPaddedMatteBorderOnSide(ColorStyles.DIVIDER, TOP, 5, 12, 5, 12)
                .component();

        label("HP unknown until defeated", Font.ITALIC, 10f, ColorStyles.TEXT_HINT).in(footer);

        return footer;
    }

    private SwingComp<JLabel> sectionLabel(String text) {
        return label(text.toUpperCase(), Font.PLAIN, 9f, ColorStyles.TEXT_HINT)
                .onLeft().withEmptyBorder(6, 0, 3, 0);
    }

    private SwingComp<JPanel> getStatRow(Object label, String value) {
        return getStatRow(label, value, ColorStyles.TEXT_PRIMARY);
    }

    private SwingComp<JPanel> getStatRow(Object label, String value, Color valueColor) {
        JLabel typeLabel = label(label, Font.PLAIN, 11f, ColorStyles.TEXT_MUTED).component();
        JLabel valueLabel = label(value, Font.PLAIN, 11f, valueColor).component();

        return newArrangedAs(BORDER)
                .borderCollect(
                        west(typeLabel), east(valueLabel)
                ).withBackground(ColorStyles.BG_DEEP)
                .onLeft()
                .withMaximumSize(Integer.MAX_VALUE, 22)
                .withBorder(new MatteBorder(0, 0, 1, 0, ColorStyles.BACKGROUND));
    }
}