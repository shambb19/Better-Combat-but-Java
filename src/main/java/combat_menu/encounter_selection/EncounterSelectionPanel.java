package combat_menu.encounter_selection;

import __main.Main;
import __main.UploadMain;
import _manager.EncounterManager;
import combat_object.combatant.Combatant;
import combat_object.combatant.NPC;
import combat_object.scenario.Scenario;
import lombok.*;
import lombok.experimental.*;
import swing.ColorStyles;
import swing.custom.ValidatedField;
import swing.fluent.SwingPane;
import util.Filterable;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.border.MatteBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static swing.ColorStyles.*;
import static swing.fluent.SwingComp.fluent;
import static swing.fluent.SwingComp.*;
import static swing.fluent.SwingPane.fluent;
import static swing.fluent.SwingPane.*;

@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class EncounterSelectionPanel extends JPanel {

    static Color BG_CARD_ABS = new Color(0x1C, 0x1E, 0x24);

    UploadMain root;

    @NonFinal boolean updatingScenario = false;

    @Getter List<NPC> quickAdds = new ArrayList<>();
    @Getter List<Scenario> quickScenarios = new ArrayList<>();
    List<CombatantCard> activeCards = new ArrayList<>();
    @Getter JComboBox<Scenario> scenarioCombo;
    JButton quickCombatantButton;
    JPanel partyContainer, dynamicContainer;
    JButton beginButton;

    public EncounterSelectionPanel(UploadMain root) {
        this.root = root;

        fluent(this).arrangedAs(BORDER).withBorder(new LineBorder(TRACK, 1));

        JPanel topBar = panelIn(this, BorderLayout.NORTH).arrangedAs(BORDER)
                .withBackground(BG_DARK)
                .withPaddedMatteBorderOnSide(TRACK, BOTTOM, 8, 0, 8, 0)
                .component();

        scenarioCombo = new JComboBox<>(EncounterManager.getEncounter().getScenarios().toArray(new Scenario[0]));
        fluent(scenarioCombo)
                .withBackground(TRACK)
                .withPaddedBorder(new LineBorder(BORDER_LIGHT, 1), 2, 8, 2, 8)
                .applied(b -> {
                    b.setSelectedIndex(-1);
                    b.addActionListener(e -> updateScenario());
                });

        panelIn(topBar, BorderLayout.WEST).arrangedAs(FLOW, 10, 0)
                .collect(
                        "Combat Encounter", scenarioCombo
                ).withBackground(BG_DARK);

        quickCombatantButton = button("Quick Combatant", BG_SURFACE, () -> new QuickCombatant(this))
                .enabled(false).component();

        JButton quickScenarioButton = button("Quick Scenario", BG_SURFACE, () -> new QuickScenario(this)).component();

        panelIn(topBar, BorderLayout.EAST).arrangedAs(FLOW)
                .collect(quickCombatantButton, spacer(10, 0), quickScenarioButton, spacer(10, 0))
                .withBackground(BG_DARK);

        partyContainer = newArrangedAs(VERTICAL_BOX).transparent().component();
        dynamicContainer = newArrangedAs(VERTICAL_BOX).transparent().component();

        newArrangedAs(TWO_COLUMN, 15, 0)
                .collect(partyContainer, dynamicContainer)
                .transparent()
                .withEmptyBorder(8, 12, 12, 12)
                .toScroller()
                .withPreferredSize(500, 520)
                .in(this, BorderLayout.CENTER);

        JPanel footer = panelIn(this, BorderLayout.SOUTH).arrangedAs(FLOW_RIGHT, 12, 8)
                .withBackground(BG_DARK)
                .withBorder(new MatteBorder(1, 0, 0, 0, TRACK))
                .component();

        beginButton = button("Begin Encounter", SUCCESS, this::logAndBegin)
                .withDerivedFont(Font.PLAIN, 13f)
                .enabled(false)
                .in(footer);

        initializeParty();

        setVisible(true);

        SwingUtilities.invokeLater(beginButton::requestFocusInWindow);
    }

    void updateScenario() {
        if (updatingScenario) return;
        updatingScenario = true;

        activeCards.removeIf(card -> card.getCombatant() instanceof NPC);
        dynamicContainer.removeAll();

        quickScenarios.stream().filter(s -> {
            for (int i = 0; i < scenarioCombo.getItemCount(); i++) {
                if (scenarioCombo.getItemAt(i).equals(s)) return false;
            }
            return true;
        }).forEach(scenarioCombo::addItem);

        Scenario scenario = (Scenario) scenarioCombo.getSelectedItem();
        if (scenario == null) return;

        ArrayList<NPC> friendlies = new ArrayList<>(scenario.npcList(true, false));
        friendlies.addAll(Filterable.of(quickAdds).filteredByAsList(c -> !c.isEnemy()));

        ArrayList<NPC> enemies = new ArrayList<>(scenario.npcList(false, false));
        enemies.addAll(Filterable.of(quickAdds).filteredByAsList(Combatant::isEnemy));

        if (!friendlies.isEmpty()) {
            dynamicContainer.add(sectionLabel("Allies", ColorStyles.FRIENDLY));
            friendlies.forEach(this::addCombatantCard);
        }
        dynamicContainer.add(sectionLabel("Enemies", ColorStyles.ENEMY));
        enemies.forEach(this::addCombatantCard);

        dynamicContainer.revalidate();
        dynamicContainer.repaint();
        quickCombatantButton.setEnabled(true);
        beginButton.setEnabled(true);

        updatingScenario = false;
    }

    private void validateCards() {
        boolean allInputsValid = activeCards.stream().allMatch(CombatantCard::hasValidInput);
        beginButton.setEnabled(allInputsValid);
    }

    private static JLabel sectionLabel(String text, Color color) {
        return label(text.toUpperCase(), Font.BOLD, 16f, color)
                .withEmptyBorder(10, 2, 6, 2)
                .onLeft().component();
    }

    private void logAndBegin() {
        var activeCombatants = activeCards.stream()
                .filter(c -> c.absentCheck == null || !c.absentCheck.isSelected())
                .map(card -> {
                    Combatant c = card.getCombatant();
                    c.setInitiative(card.getInputValue());
                    return c;
                }).collect(Collectors.partitioningBy(Combatant::isEnemy));

        List<Combatant> friendlies = new ArrayList<>(activeCombatants.get(false));
        friendlies.addAll(Filterable.of(quickAdds).filteredByAsList(c -> !c.isEnemy()));

        List<Combatant> enemies = new ArrayList<>(activeCombatants.get(true));
        enemies.addAll(Filterable.of(quickAdds).filteredByAsList(Combatant::isEnemy));

        EncounterManager.getEncounter().setEnemies(enemies);
        EncounterManager.getEncounter().setFriendlies(friendlies);

        Main.closeAndSwitch(root, Main.COMBAT);
    }

    private void initializeParty() {
        partyContainer.add(sectionLabel("Party", PARTY));
        EncounterManager.getParty().forEach(pc -> {
            CombatantCard card = new CombatantCard(
                    pc, "Initiative", ColorStyles.PARTY, true, this::validateCards
            );
            activeCards.add(card);
            fluent(partyContainer).collect(card, spacer(0, 6));
        });
    }

    private void addCombatantCard(NPC npc) {
        Color accent = npc.isEnemy() ? ENEMY : FRIENDLY;
        CombatantCard card = new CombatantCard(
                npc, "Initiative", accent, false, this::validateCards
        );
        activeCards.add(card);
        fluent(dynamicContainer).collect(card, spacer(0, 6));
    }

    @FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
    @ExtensionMethod(util.StringUtil.class)
    static class CombatantCard extends JPanel {

        @Getter Combatant combatant;
        ValidatedField input;
        @Getter JCheckBox absentCheck;
        JPanel accentBar;

        CombatantCard(Combatant combatant, String inputType, Color accent, boolean showAbsent, Runnable validator) {
            this.combatant = combatant;

            SwingPane.fluent(this).arrangedAs(SwingPane.BORDER, 10, 0)
                    .withBackground(BG_SURFACE)
                    .withMaximumSize(Integer.MAX_VALUE, 52)
                    .withPreferredSize(0, 52)
                    .onLeft()
                    .withEmptyBorder(0, 0, 0, 12);

            accentBar = panelIn(this, BorderLayout.WEST)
                    .withPreferredSize(3, 0)
                    .withBackground(accent)
                    .component();

            label(combatant, Font.BOLD, 14f).withEmptyBorder(0, 10, 0, 0)
                    .in(this, BorderLayout.CENTER);

            JPanel right = new JPanel(new GridBagLayout());
            right.setOpaque(false);
            add(right, BorderLayout.EAST);

            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(0, 8, 0, 0);
            gbc.anchor = GridBagConstraints.CENTER;

            JLabel initLabel = label(inputType, 11f).muted().component();
            right.add(initLabel, gbc);

            input = new ValidatedField("0", validator, 30);
            fluent(input).withBackground(TRACK).opaque();
            right.add(input, gbc);

            absentCheck = fluent(new JCheckBox("Absent"))
                    .withAction(b -> updateAbsentState())
                    .withDerivedFont(Font.PLAIN, 11f)
                    .withBackgroundAndForeground(BG_SURFACE, FG_MUTED)
                    .transparent().component();
            if (showAbsent)
                right.add(absentCheck, gbc);
        }

        private static void setForegroundAlpha(Container container, float alpha) {
            for (Component child : container.getComponents()) {
                if (child instanceof JLabel l) {
                    Color fg = l.getForeground();
                    l.setForeground(new Color(
                            fg.getRed(), fg.getGreen(), fg.getBlue(),
                            Math.round(255 * alpha)));
                }
                if (child instanceof Container c) setForegroundAlpha(c, alpha);
            }
        }

        private void updateAbsentState() {
            boolean absent = absentCheck.isSelected();
            input.setEnabled(!absent);
            setBackground(absent ? BG_CARD_ABS : BG_SURFACE);
            absentCheck.setBackground(absent ? BG_CARD_ABS : BG_SURFACE);
            accentBar.setBackground(absent ? TRACK : combatant.getCombatantColor());

            float alpha = absent ? 0.5f : 1.0f;
            setForegroundAlpha(this, alpha);
            repaint();
        }

        public int getInputValue() {
            return input.getValue().toInt();
        }

        public boolean hasValidInput() {
            return input.isValid();
        }

    }

}