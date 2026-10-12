package combat_menu.encounter_selection;

import __main.Main;
import __main.UploadMain;
import _global_list.Scenarios;
import _manager.EncounterManager;
import combat_object.combatant.Combatant;
import combat_object.combatant.info.AbilityModifier;
import combat_object.scenario.Scenario;
import encounter.Encounter;
import lombok.*;
import lombok.experimental.*;
import swing.builder.ButtonBuilder;
import swing.builder.Components;
import util.Filterable;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.List;

import static swing.ColorStyles.*;
import static swing.builder.PanelBuilder.*;

@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class EncounterSelectionPanel extends JPanel {

    UploadMain root;

    @NonFinal boolean updatingScenario = false;

    CombatantScroller partyContainer, dynamicContainer;
    JButton beginButton;

    public EncounterSelectionPanel(UploadMain root) {
        this.root = root;

        setLayout(new BorderLayout());
        setBackground(BG_DARK);

        List<Scenario> scenariosList = Scenarios.toList();
        scenariosList.addFirst(Scenario.emptyScenario());

        JTabbedPane scenarioOptions = new JTabbedPane(SwingConstants.TOP, JTabbedPane.SCROLL_TAB_LAYOUT);
        for (Scenario s : scenariosList) {
            scenarioOptions.addTab(s.getName(), null);
        }
        scenarioOptions.addChangeListener(e -> {
            Scenario s = scenariosList.get(scenarioOptions.getSelectedIndex());
            updateScenario(s);
        });
        scenarioOptions.putClientProperty("JTabbedPane.tabsPopupPolicy", "never");
        scenarioOptions.putClientProperty("JTabbedPane.scrollButtonsPolicy", "asNeeded");
        scenarioOptions.putClientProperty("TabbedPane.underlineColor", SCENARIO);

        createPanel(HORIZONTAL_BOX,
                "(Optional) Select an Existing Scenario:", Components.spacer(6, 0),
                scenarioOptions
                ).border(BorderFactory.createEmptyBorder(8, 8, 14, 8))
                .build().putIn(this, BorderLayout.NORTH);

        partyContainer = CombatantScroller.createForEncounterSelectionPanel(this, this::validateCards, true);
        dynamicContainer = CombatantScroller.createForEncounterSelectionPanel(this, this::validateCards, false);

        JScrollPane scrollPane = createPanel(TWO_COLUMN, partyContainer, dynamicContainer).hgap(10)
                .border(BorderFactory.createEmptyBorder(8, 12, 12, 12))
                .background(BG_DARK)
                .build().getAsScrollerIn(this, BorderLayout.CENTER);
        scrollPane.setPreferredSize(new Dimension(500, 520));

        JPanel footer = createPanel(FLOW_RIGHT).hgap(12).vgap(8).build().putIn(this, BorderLayout.SOUTH);

        ButtonBuilder.createButton("Randomize Initiatives", BG_SURFACE, this::randomizeInitiatives)
                .fontBuilder(FontBuilder.of(Font.PLAIN, 12f))
                .build().putIn(footer);

        ButtonBuilder.createButton("Refresh Inputs", BG_SURFACE, this::validateCards)
                .toolTipText("Forces an input validation check")
                .build().putIn(footer);

        beginButton = ButtonBuilder.createButton("Begin Encounter", SUCCESS, this::logAndBegin)
                .fontBuilder(FontBuilder.of(Font.PLAIN, 13f))
                .enabled(false)
                .build().putIn(footer);

        setVisible(true);

        SwingUtilities.invokeLater(beginButton::requestFocusInWindow);

        updateScenario(scenariosList.getFirst());
    }

    void updateScenario(Scenario scenario) {
        if (updatingScenario) return;
        updatingScenario = true;

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
            initiative += c.combatant.getStats().mod(AbilityModifier.DEX);
            c.input.setValue(String.valueOf(initiative));
        }
    }

    private void logAndBegin() {
        var activeCombatantsList = Arrays.stream(getAllActiveCards())
                .filter(c -> c.checkBox == null || !c.checkBox.isSelected())
                .map(card -> {
                    Combatant c = card.combatant;
                    c.setInitiative(card.getInputValue());
                    return c;
                }).toList();
        var activeCombatants = Combatant.enemyPartition(activeCombatantsList);

        EncounterManager.setEncounter(new Encounter(Scenarios.toList(), activeCombatants));

        Main.closeAndSwitch(root, Main.COMBAT);
    }

    private CombatantCard[] getAllActiveCards() {
        return Filterable.ofArrays(partyContainer.getCombatantCards(), dynamicContainer.getCombatantCards())
                .filteredBy(card -> !card.isEmpty)
                .toList().toArray(new CombatantCard[0]);
    }

}