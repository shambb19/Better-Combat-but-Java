package combat_menu;

import __main.Main;
import __main.UploadMain;
import __main.manager.EncounterManager;
import combat_object.combatant.Combatant;
import combat_object.combatant.NPC;
import combat_object.scenario.Scenario;
import format.ColorStyles;
import lombok.*;
import lombok.experimental.*;
import swing.custom_component.ValidatedField;
import swing.swing_comp.SwingPane;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.border.MatteBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static format.ColorStyles.*;
import static swing.swing_comp.SwingComp.fluent;
import static swing.swing_comp.SwingComp.*;
import static swing.swing_comp.SwingPane.fluent;
import static swing.swing_comp.SwingPane.*;

@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class EncounterSelectionPanel extends JPanel {

    static Color BG_CARD_ABS = new Color(0x1C, 0x1E, 0x24);

    UploadMain root;

    @NonFinal boolean updatingScenario = false;

    List<NPC> quickAdds = new ArrayList<>();
    List<CombatantCard> activeCards = new ArrayList<>();
    JComboBox<Scenario> scenarioCombo;
    JPanel partyContainer, dynamicContainer;
    JButton beginButton;

    public static EncounterSelectionPanel newInstance(UploadMain root) {
        return new EncounterSelectionPanel(root);
    }

    private EncounterSelectionPanel(UploadMain root) {
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

        panelIn(topBar, BorderLayout.EAST).arrangedAs(FLOW)
                .collect(
                        button("Quick Combatant", BG_SURFACE, QuickCombatant::new).enabled(false), spacer(10, 0)
                ).withBackground(BG_DARK);

        partyContainer = newArrangedAs(VERTICAL_BOX).transparent().component();
        dynamicContainer = newArrangedAs(VERTICAL_BOX).transparent().component();

        JPanel scrollContent = newArrangedAs(TWO_COLUMN, 15, 0)
                .collect(partyContainer, dynamicContainer)
                .transparent()
                .withEmptyBorder(8, 12, 12, 12)
                .component();

        scrollPane(scrollContent)
                .withBorder(null)
                .withPreferredSize(500, 520)
                .applied(s -> s.getViewport().setBackground(ColorStyles.BACKGROUND))
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

    private void updateScenario() {
        if (updatingScenario) return;
        updatingScenario = true;

        activeCards.removeIf(card -> card.getCombatant() instanceof NPC);
        dynamicContainer.removeAll();

        Scenario scenario = (Scenario) scenarioCombo.getSelectedItem();
        if (scenario == null) return;

        ArrayList<NPC> friendlies = new ArrayList<>(scenario.list(true, false));
        quickAdds.stream().filter(c -> !c.isEnemy()).forEach(friendlies::add);

        ArrayList<NPC> enemies = new ArrayList<>(scenario.list(false, false));
        quickAdds.stream().filter(NPC::isEnemy).forEach(enemies::add);

        if (!friendlies.isEmpty()) {
            dynamicContainer.add(sectionLabel("Allies", ColorStyles.FRIENDLY));
            friendlies.forEach(npc -> addCombatantCard(npc, ColorStyles.FRIENDLY));
        }
        dynamicContainer.add(sectionLabel("Enemies", ColorStyles.ENEMY));
        enemies.forEach(npc -> addCombatantCard(npc, ColorStyles.ENEMY));

        dynamicContainer.revalidate();
        dynamicContainer.repaint();
        beginButton.setEnabled(true);

        updatingScenario = false;
    }

    private static JLabel sectionLabel(String text, Color color) {
        return label(text.toUpperCase(), Font.BOLD, 16f, color)
                .withEmptyBorder(10, 2, 6, 2)
                .onLeft().component();
    }

    private void logAndBegin() {
        var activeCombatants = activeCards.stream()
                .filter(c -> c.absentCheck == null || !c.absentCheck.isSelected())
                .map(CombatantCard::getCombatant)
                .collect(Collectors.partitioningBy(Combatant::isEnemy));

        List<Combatant> friendlies = new ArrayList<>(activeCombatants.get(false));
        quickAdds.stream().filter(c -> !c.isEnemy()).forEach(friendlies::add);

        List<Combatant> enemies = new ArrayList<>(activeCombatants.get(true));
        quickAdds.stream().filter(NPC::isEnemy).forEach(enemies::add);

        EncounterManager.getEncounter().setEnemies(enemies);
        EncounterManager.getEncounter().setFriendlies(friendlies);

        Main.closeUploadAndRun(Main.COMBAT, root);
    }

    private void initializeParty() {
        partyContainer.add(sectionLabel("Party", PARTY));
        EncounterManager.getParty().forEach(pc -> {
            CombatantCard card = new CombatantCard(pc, ColorStyles.PARTY, true);
            activeCards.add(card);
            fluent(partyContainer).collect(card, spacer(0, 6));
        });
    }

    private void addCombatantCard(NPC npc, Color accent) {
        CombatantCard card = new CombatantCard(npc, accent, false);
        activeCards.add(card);
        fluent(dynamicContainer).collect(card, spacer(0, 6));
    }

    @FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
    private static class CombatantCard extends JPanel {

        Combatant combatant;
        JSpinner spinner;
        JCheckBox absentCheck;
        JPanel accentBar;

        CombatantCard(Combatant combatant, Color accent, boolean showAbsent) {
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

            label(combatant, Font.BOLD, 14f, ColorStyles.TEXT_PRIMARY)
                    .withEmptyBorder(0, 10, 0, 0).in(this, BorderLayout.CENTER);

            JPanel right = new JPanel(new GridBagLayout());
            right.setOpaque(false);
            add(right, BorderLayout.EAST);

            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(0, 8, 0, 0);
            gbc.anchor = GridBagConstraints.CENTER;

            JLabel initLabel = label("Initiative", Font.PLAIN, 11f, ColorStyles.TEXT_MUTED).component();
            right.add(initLabel, gbc);

            SpinnerNumberModel model = new SpinnerNumberModel(10, 1, 20, 1);
            spinner = fluent(new JSpinner(model))
                    .withPreferredSize(60, 26)
                    .withPaddedBorder(new LineBorder(BORDER_LIGHT, 1), 0, 4, 0, 4)
                    .withBackground(TRACK)
                    .opaque().component();
            if (spinner.getEditor() instanceof JSpinner.DefaultEditor de) {
                fluent(de.getTextField())
                        .withBackground(TRACK)
                        .withEmptyBorder(0, 2, 0, 2);
            }
            right.add(spinner, gbc);

            absentCheck = fluent(new JCheckBox("Absent"))
                    .applied(b -> b.addActionListener(e -> updateAbsentState()))
                    .withDerivedFont(Font.PLAIN, 11f)
                    .withBackgroundAndForeground(BG_SURFACE, TEXT_MUTED)
                    .transparent()
                    .withoutPaintedFocus().component();
            if (showAbsent)
                right.add(absentCheck, gbc);
        }

        private static void setForegroundAlpha(Container c, float alpha) {
            for (Component child : c.getComponents()) {
                if (child instanceof JLabel l) {
                    Color fg = l.getForeground();
                    l.setForeground(new Color(
                            fg.getRed(), fg.getGreen(), fg.getBlue(),
                            Math.round(255 * alpha)));
                }
                if (child instanceof Container cont) setForegroundAlpha(cont, alpha);
            }
        }

        private void updateAbsentState() {
            boolean absent = absentCheck.isSelected();
            spinner.setEnabled(!absent);
            setBackground(absent ? BG_CARD_ABS : BG_SURFACE);
            absentCheck.setBackground(absent ? BG_CARD_ABS : BG_SURFACE);
            accentBar.setBackground(absent ? TRACK : combatant.getCombatantColor());

            float alpha = absent ? 0.5f : 1.0f;
            setForegroundAlpha(this, alpha);
            repaint();
        }

        public Combatant getCombatant() {
            combatant.setInitiative((int) spinner.getValue());
            return combatant;
        }
    }

    @FieldDefaults(makeFinal = true)
    @ExtensionMethod(util.StringUtils.class)
    private class QuickCombatant extends JDialog {
        ValidatedField nameField, hpField, acField;
        JToggleButton enemyToggle;

        {
            setTitle("Quick Combatant");
            setLocationRelativeTo(scenarioCombo);

            fluent(this).arrangedAs(BORDER, 0, 15)
                    .withEmptyBorder(20, 20, 20, 20)
                    .withPreferredSize(350, 180);

            nameField = new ValidatedField("Name");
            enemyToggle = fluent(new JToggleButton("Friendly"))
                    .withAction(t -> {
                        t.setText(t.isSelected() ? "Enemy" : "Friendly");
                        t.setBackground(t.isSelected() ? ENEMY : FRIENDLY);
                    }).withPaddedBorder(new LineBorder(BACKGROUND, 4), 10, 10, 10, 10)
                    .withBackground(FRIENDLY)
                    .withText(Font.BOLD, 16f, TEXT_HINT)
                    .component();
            hpField = new ValidatedField("Health");
            acField = new ValidatedField("Armor Class");

            panelIn(this, BorderLayout.CENTER).arrangedAs(TWO_COLUMN, 15, 15)
                    .collect(nameField, enemyToggle, hpField, acField);

            button("Quick Add", SUCCESS, this::addQuickCombatant).in(this, BorderLayout.SOUTH);

            pack();
            setVisible(true);
        }

        void addQuickCombatant() {
            boolean valid = Stream.of(nameField, hpField, acField).allMatch(ValidatedField::isValid);
            if (!valid) return;

            NPC quickAdd = NPC.builder()
                    .name(nameField.getValue())
                    .hp(hpField.getValue().toInt())
                    .armorClass(acField.getValue().toInt())
                    .isEnemy(enemyToggle.isSelected())
                    .build();
            quickAdds.add(quickAdd);
            updateScenario();
            dispose();
        }

    }
}