package swing.popup;

import combat_object.combatant.Combatant;
import combat_object.combatant.NPC;
import combat_object.combatant.PC;
import combat_object.combatant.info.AbilityModifier;
import swing.builder.ComponentBuilder;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.Objects;

import static swing.ColorStyles.*;
import static swing.builder.PanelBuilder.*;

public class StatTooltipPopup extends JWindow {

    private static final int WIDTH = 200;

    private final Combatant combatant;

    public StatTooltipPopup(Window owner, Combatant combatant) {
        super(owner);
        this.combatant = combatant;

        setBackground(new Color(0, 0, 0, 0));

        JPanel root = createPanel(BORDER)
                .hostedComponent(getHeader(), BorderLayout.NORTH)
                .hostedComponent(getBody(), BorderLayout.CENTER)
                .background(BG_DEEP)
                .border(BorderFactory.createLineBorder(DIVIDER, 1)).build().get();
        setContentPane(root);

        if (combatant.isEnemy())
            root.add(buildFooter(), BorderLayout.SOUTH);

        pack();
        setSize(WIDTH, getPreferredSize().height);
    }

    private static String roughHpLabel(Combatant c) {
        if (c.isUnconscious()) return "Defeated";
        double ratio = c.getHpRatio();
        if (ratio > 0.7) return "Healthy";
        if (ratio > 0.50) return "Injured";
        if (ratio > 0.25) return "Bloodied";
        return "Critical";
    }

    private JPanel getHeader() {
        JPanel header = createPanel(VERTICAL_BOX)
                .background(BG_DEEP)
                .border(BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(0, 0, 6, 0, DIVIDER),
                        BorderFactory.createEmptyBorder(8, 12, 7, 12)
                )).build().get();

        ComponentBuilder.createLabel(combatant.toString(), Font.PLAIN, 13f, FOREGROUND)
                .build().putIn(header);

        String class5e = combatant.getStats().getClass5e().toString();
        String subtitle = switch (combatant) {
            case NPC npc when npc.isEnemy() -> "Enemy " + class5e;
            case NPC ignored -> "Allied " + class5e;
            case PC pc -> "Level " + pc.getStats().getLevel() + " " + class5e;
            default -> throw new RuntimeException("StatTooltipPopup.getHeader: unexpected combatant");
        };

        ComponentBuilder.createLabel(subtitle, Font.PLAIN, 10f, FG_MUTED).build().putIn(header);

        return header;
    }

    private JPanel getBody() {
        JPanel statPanel = createPanel(VERTICAL_BOX)
                .background(BG_DEEP)
                .border(BorderFactory.createEmptyBorder(4, 12, 8, 12)).build().get();

        var combatSection = sectionLabel("Combat");

        var ac = getStatRow("AC", String.valueOf(combatant.getArmorClass()));
        var hp = combatant.isEnemy()
                ? getStatRow("HP", roughHpLabel(combatant), combatant.getHealthBarColor())
                : getStatRow("HP", combatant.getHealthBarString(), combatant.getHealthBarColor());
        var initiative = getStatRow("Initiative", "+" + combatant.getInitiative());

        addConstraintlessComponentsTo(statPanel, combatSection, ac, hp, initiative);

        if (!(combatant instanceof PC pc)) return statPanel;

        var abilitySection = sectionLabel("Ability Scores");
        var abilityRows = Arrays.stream(AbilityModifier.values()).map(stat -> {
            if (stat.equals(AbilityModifier.OPTION)) return null;
            String statVal = String.valueOf(pc.getStats().get(stat));
            return getStatRow(stat.name().toUpperCase(), statVal);
        }).filter(Objects::nonNull).toList();

        addConstraintlessComponentsTo(statPanel, abilitySection, abilityRows);

        return statPanel;
    }

    private JPanel buildFooter() {
        JPanel footer = createPanel(FLOW_LEFT)
                .background(BG_DARK)
                .border(BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(6, 0, 0, 0, DIVIDER),
                        BorderFactory.createEmptyBorder(5, 12, 5, 12)
                )).build().get();

        ComponentBuilder.createLabel("HP unknown until defeated", Font.ITALIC, 10f, FG_HINT)
                .build().putIn(footer);

        return footer;
    }

    private ComponentBuilderBuilder<JLabel, ?, ?> sectionLabel(String text) {
        return ComponentBuilder.createLabel(text.toUpperCase(), Font.PLAIN, 9f, FG_HINT)
                .alignmentX(LEFT_ALIGNMENT)
                .border(BorderFactory.createEmptyBorder(6, 0, 3, 0));
    }

    private PanelBuilderBuilder<JPanel, ?, ?> getStatRow(Object label, String value) {
        return getStatRow(label, value, FOREGROUND);
    }

    private PanelBuilderBuilder<JPanel, ?, ?> getStatRow(Object label, String value, Color valueColor) {
        JLabel typeLabel = ComponentBuilder.getBasicLabel(label.toString(), Font.PLAIN, 11f, FG_MUTED);
        JLabel valueLabel = ComponentBuilder.getBasicLabel(value, Font.PLAIN, 11f, valueColor);

        return createPanel(BORDER)
                .hostedComponent(typeLabel, BorderLayout.WEST)
                .hostedComponent(valueLabel, BorderLayout.EAST)
                .alignmentX(LEFT_ALIGNMENT)
                .fixedHeight(20)
                .border(BorderFactory.createMatteBorder(0, 0, 1, 0, BACKGROUND));
    }
}