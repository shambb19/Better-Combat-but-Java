package campaign_creator_menu;

import __main.Main;
import __main.MainFrame;
import _global_list.Combatants;
import campaign_creator_menu.input.CombatantInputPanel;
import campaign_creator_menu.input.ScenarioInputPanel;
import combat_object.CombatObject;
import combat_object.combatant.Combatant;
import combat_object.scenario.Scenario;
import encounter.Encounter;
import lombok.*;
import lombok.experimental.*;

import javax.swing.*;

@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class CampaignCreatorMenu extends MainFrame {

    public static final String TITLE = "Campaign Creator" + Main.TITLE;

    CompletedElementsList completedList;

    HostPanel hostPanel;
    CombatantInputPanel inputPanel;
    ScenarioInputPanel scenarioPanel;
    DownloadDocDisplayPanel displayPanel;

    public CampaignCreatorMenu() {
        setTitle(TITLE);

        Encounter encounter = Combatants.toBattle();

        completedList = new CompletedElementsList(encounter);
        inputPanel = new CombatantInputPanel();
        scenarioPanel = new ScenarioInputPanel(completedList);
        LevelUpPanel levelUpPanel = new LevelUpPanel(encounter);
        displayPanel = new DownloadDocDisplayPanel(encounter);
        hostPanel = new HostPanel(inputPanel, scenarioPanel, levelUpPanel, completedList, displayPanel);

        add(hostPanel);

        setVisible(true);
    }

    public void logEdit(CombatObject selection, boolean isNew) {
        if (selection instanceof Combatant c)
            editCombatant(c, isNew);
        else if (selection instanceof Scenario s)
            editScenario(s, isNew);
    }

    public void editCombatant(Combatant selection, boolean isNew) {
        if (isNew)
            inputPanel.openNew(selection.isEnemy());
        else
            inputPanel.openExisting(selection);
    }

    public void editScenario(Scenario selection, boolean isNew) {
        if (isNew)
            scenarioPanel.openNew();
        else
            scenarioPanel.openExisting(selection);
    }

    public void logCombatantCompleted(Combatant combatant) {
        completedList.findAndLocateCopy(combatant);
        completedList.addCombatant(combatant);
        displayPanel.addElement(combatant);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
    }

    public void logScenarioCompleted(Scenario scenario) {
        completedList.addScenario(scenario);
        displayPanel.addElement(scenario);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
    }

    public void setInputPanelEnabled(boolean isEnabled) {
        hostPanel.changeInputPanel(HostPanel.COMBATANT_INPUT, isEnabled);
    }

    public void setScenarioPanelEnabled(boolean isEnabled) {
        hostPanel.changeInputPanel(HostPanel.SCENARIO_INPUT, isEnabled);
    }

    public void finishLevelUpProcess() {
        hostPanel.endLevelUp();
    }

}