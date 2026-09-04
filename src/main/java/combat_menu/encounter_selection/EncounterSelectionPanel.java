package combat_menu.encounter_selection;

import __main.Main;
import __main.UploadMain;
import _manager.EncounterManager;
import combat_object.combatant.Combatant;
import combat_object.combatant.info.AbilityModifier;
import combat_object.scenario.Scenario;
import lombok.*;
import lombok.experimental.*;
import util.Filterable;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.border.MatteBorder;
import java.awt.*;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static swing.ColorStyles.*;
import static swing.fluent.SwingComp.*;
import static swing.fluent.SwingPane.*;

@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class EncounterSelectionPanel extends JPanel {

    UploadMain root;

    @NonFinal boolean updatingScenario = false;

    @Getter JComboBox<Scenario> scenarioCombo;
    CombatantScroller partyContainer, dynamicContainer;
    JButton beginButton;

    public EncounterSelectionPanel(UploadMain root) {
        this.root = root;

        fluent(this).arrangedAs(BORDER).withBorder(new LineBorder(TRACK, 1));

        JPanel topBar = panelIn(this, BorderLayout.NORTH).arrangedAs(BORDER)
                .withBackground(BG_DARK)
                .withPaddedMatteBorderOnSide(TRACK, BOTTOM, 8, 0, 8, 0)
                .component();

        List<Scenario> scenariosList = EncounterManager.getEncounter().getScenarios();
        scenariosList.addFirst(Scenario.emptyScenario());

        scenarioCombo = new JComboBox<>(scenariosList.toArray(new Scenario[0]));
        fluent(scenarioCombo)
                .withBackground(TRACK)
                .withPaddedBorder(new LineBorder(BORDER_LIGHT, 1), 2, 8, 2, 8)
                .applied(b -> {
                    b.setSelectedItem(Scenario.emptyScenario());
                    b.addActionListener(e -> updateScenario());
                });

        panelIn(topBar, BorderLayout.WEST).arrangedAs(FLOW, 10, 0)
                .collect(
                        "(Optional) Select an Existing Scenario:", scenarioCombo
                ).withBackground(BG_DARK);

        partyContainer = CombatantScroller.partyPanel(
                this, "Initiative", true, true, this::validateCards);
        dynamicContainer = CombatantScroller.npcPanel(
                this, "Initiative", false, true, this::validateCards);

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

        button("Randomize Initiatives", BG_SURFACE, this::randomizeInitiatives)
                .withDerivedFont(Font.PLAIN, 12f)
                .enabled(true)
                .in(footer);

        beginButton = button("Begin Encounter", SUCCESS, this::logAndBegin)
                .withDerivedFont(Font.PLAIN, 13f)
                .enabled(false)
                .in(footer);

        setVisible(true);

        SwingUtilities.invokeLater(beginButton::requestFocusInWindow);

        updateScenario();
    }

    void updateScenario() {
        if (updatingScenario) return;
        updatingScenario = true;

        Scenario scenario = (Scenario) scenarioCombo.getSelectedItem();
        if (scenario == null) return;

        dynamicContainer.setScenario(scenario);

        dynamicContainer.revalidate();
        dynamicContainer.repaint();

        validateCards();

        updatingScenario = false;
    }

    private void validateCards() {
        boolean allInputsValid = partyContainer.areAllCardsValid() && dynamicContainer.areAllCardsValid();
        beginButton.setEnabled(allInputsValid && dynamicContainer.containsEnemies());
    }

    private void randomizeInitiatives() {
        for (CombatantCard c : getAllActiveCards()) {
            int initiative = (int) (Math.random() * 20) + 1;
            initiative += c.getCombatant().getStats().mod(AbilityModifier.DEX);
            c.input.setValue(String.valueOf(initiative));
        }
    }

    private void logAndBegin() {
        var activeCombatants = Arrays.stream(getAllActiveCards())
                .filter(c -> c.checkBox == null || !c.checkBox.isSelected())
                .map(card -> {
                    Combatant c = card.getCombatant();
                    c.setInitiative(card.getInputValue());
                    return c;
                }).collect(Collectors.partitioningBy(Combatant::isEnemy));

        EncounterManager.getEncounter().setEnemies(activeCombatants.get(true));
        EncounterManager.getEncounter().setFriendlies(activeCombatants.get(false));

        Main.closeAndSwitch(root, Main.COMBAT);
    }

    private CombatantCard[] getAllActiveCards() {
        return Filterable.ofArrays(partyContainer.getCombatantCards(), dynamicContainer.getCombatantCards())
                .filteredBy(card -> !card.isEmpty)
                .toList().toArray(new CombatantCard[0]);
    }

}