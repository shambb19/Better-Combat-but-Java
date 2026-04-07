package combat_menu.popup;

import __main.EncounterInfo;
import __main.Main;
import _global_list.Combatants;
import character_info.combatant.Combatant;
import character_info.combatant.NPC;
import character_info.combatant.PC;
import com.formdev.flatlaf.FlatClientProperties;
import encounter_info.Scenario;
import format.ColorStyle;
import format.swing_comp.SwingComp;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static format.swing_comp.SwingComp.*;
import static format.swing_comp.SwingPane.modifiable;
import static format.swing_comp.SwingPane.*;

public class EncounterFinalizationPopup extends JDialog {

    private final List<CombatantCard> activeCards = new ArrayList<>();

    private final JPanel partyContainer;
    private final JPanel dynamicContainer;

    private EncounterFinalizationPopup() {
        setTitle("Finalize Combat Information");
        setModal(false);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setAlwaysOnTop(true);
        setIconImage(Main.getImage());

        modifiable(this).withLayout(BORDER).withGaps(15, 15).withEmptyBorder(15);

        partyContainer = panel().withLayout(VERTICAL_BOX).onLeft().transparent().build();
        dynamicContainer = panel().withLayout(VERTICAL_BOX).onLeft().transparent().build();

        JPanel scenarioPanel = panelIn(this, BorderLayout.NORTH).onLeft().build();

        label("Select Combat Scenario:")
                .withFont(SUB_HEADER)
                .withForeground(ColorStyle.SCENARIO.getColor())
                .onLeft()
                .in(scenarioPanel);

        gapIn(15, scenarioPanel);

        comboBox(EncounterInfo.getBattle().scenarios())
                .withAction(b -> updateScenario((Scenario) b.getSelectedItem()))
                .withSelection(Combatants.toScenario())
                .onLeft()
                .in(scenarioPanel);

        gapIn(15, scenarioPanel);

        panelIn(this, BorderLayout.CENTER).collect(
                        getSectionHeader("THE PARTY", ColorStyle.PARTY.getColor()),
                        partyContainer,
                        gap(20),
                        dynamicContainer
                ).withLayout(VERTICAL_BOX)
                .toScroller()
                .withEmptyBorder(10)
                .withSize(500, 600);

        JPanel confirmButton = button("Begin Encounter", this::logAndBegin)
                .withBackground(ColorStyle.DARKER_GREEN.getColor())
                .toPanel(FLOW_RIGHT)
                .in(this, BorderLayout.SOUTH)
                .build();

        initializeParty();

        updateScenario(Combatants.toScenario());
        pack();
        setLocationRelativeTo(null);

        SwingUtilities.invokeLater(confirmButton::requestFocusInWindow);
    }

    public static void run() {
        new EncounterFinalizationPopup().setVisible(true);
    }

    private JLabel getSectionHeader(String title, Color accent) {
        return label(title.toUpperCase()).withFont(HEADER)
                .withForeground(accent)
                .withEmptyBorder(10)
                .onLeft()
                .build();
    }

    private void initializeParty() {
        EncounterInfo.getParty().forEach(pc -> {
            CombatantCard card = new CombatantCard(pc, ColorStyle.PARTY.getColor());
            activeCards.add(card);
            partyContainer.add(card);
            partyContainer.add(gap(8));
        });
    }

    private void updateScenario(Scenario scenario) {
        if (scenario == null) return;

        activeCards.removeIf(card -> card.combatant instanceof NPC);

        dynamicContainer.removeAll();

        if (scenario.containsFriendlies()) {
            dynamicContainer.add(getSectionHeader("ALLIES", ColorStyle.NPC.getColor()));
            scenario.withListAllOccurrences().forEach(npc ->
                    addCombatantToUI((NPC) npc, dynamicContainer, ColorStyle.NPC.getColor()));
        }

        dynamicContainer.add(getSectionHeader("ENEMIES", ColorStyle.ENEMY.getColor()));
        scenario.againstListAllOccurrences().forEach(npc ->
                addCombatantToUI((NPC) npc, dynamicContainer, ColorStyle.ENEMY.getColor()));

        dynamicContainer.revalidate();
        dynamicContainer.repaint();
    }

    private void addCombatantToUI(NPC npc, JPanel container, Color accent) {
        CombatantCard card = new CombatantCard(npc.copy(), accent);
        activeCards.add(card);
        modifiable(container).collect(card, gap(8));
    }

    private void logAndBegin() {
        EncounterInfo.getFriendlies().clear();
        EncounterInfo.getEnemies().clear();

        activeCards.stream()
                .filter(CombatantCard::isPresent)
                .map(CombatantCard::getCombatant)
                .forEach(EncounterInfo::addCombatant);

        dispose();
        Main.finalizeCombat();
    }

    private static class CombatantCard extends JPanel {

        private final Combatant combatant;
        private final JSpinner spinner;
        private final JCheckBox absentCheck;

        public CombatantCard(Combatant combatant, Color accentColor) {
            this.combatant = combatant;

            modifiable(this)
                    .withLayout(BORDER)
                    .withGaps(15, 15)
                    .withHighlight(accentColor, LEFT)
                    .onLeft();
            putClientProperty(FlatClientProperties.STYLE, "arc: 12; background: lighten($Panel.background, 3%)");

            label(combatant.name())
                    .bold(13f)
                    .withFont(HEADER)
                    .in(this, BorderLayout.CENTER)
                    .build();

            JLabel initiativeLabel = label("Initiative:")
                    .withFont(SUB_HEADER)
                    .withForeground(new Color(136, 136, 136))
                    .build();

            spinner = spinner(1, 20, 10)
                    .withSize(60, 28)
                    .build();

            JPanel spinnerGroup = flowPair(initiativeLabel, spinner, true).transparent().build();

            absentCheck = SwingComp.checkBox("Absent")
                    .withAction(this::updateVisualState)
                    .enabledIf(combatant instanceof PC)
                    .build();

            panelIn(this, BorderLayout.EAST).collect(spinnerGroup, absentCheck).transparent();
        }

        private void updateVisualState() {
            boolean isAbsent = absentCheck.isSelected();
            spinner.setEnabled(!isAbsent);
            putClientProperty(FlatClientProperties.STYLE, isAbsent ?
                    "arc: 12; background: darken($Panel.background, 5%)" :
                    "arc: 12; background: lighten($Panel.background, 3%)");
            repaint();
        }

        public Combatant getCombatant() {
            combatant.setInitiative((int) spinner.getValue());
            return combatant;
        }

        public boolean isPresent() {
            return !absentCheck.isSelected();
        }
    }
}