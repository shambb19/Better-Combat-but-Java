package campaign_creator_menu;

import __main.Main;
import _global_list.Combatants;
import _global_list.Scenarios;
import combat_object.CombatObject;
import combat_object.combatant.Combatant;
import combat_object.scenario.Scenario;
import encounter_info.Encounter;
import lombok.*;
import lombok.experimental.*;

import javax.swing.*;
import java.net.URL;

@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class CampaignCreatorMenu extends JFrame {

    public static final String TITLE = "Campaign Creator" + Main.TITLE;

    CompletedElementsList completedList;

    HostPanel hostPanel;
    CombatantInputPanel inputPanel;
    ScenarioInputPanel scenarioPanel;
    DownloadDocDisplayPanel displayPanel;

    public static CampaignCreatorMenu newInstance(URL input) {
        return new CampaignCreatorMenu(input);
    }

    private CampaignCreatorMenu(URL input) {
        setTitle(TITLE);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setIconImage(Main.getAppIcon().getImage());

        Combatants.init(input);
        Scenarios.init(input);
        Encounter encounter = Combatants.toBattle();

        completedList = new CompletedElementsList(encounter, this);
        inputPanel = new CombatantInputPanel(this);
        scenarioPanel = new ScenarioInputPanel(completedList, this);
        LevelUpPanel levelUpPanel = new LevelUpPanel(encounter, this);
        displayPanel = new DownloadDocDisplayPanel(encounter);
        hostPanel = new HostPanel(inputPanel, scenarioPanel, levelUpPanel, completedList, displayPanel);

        add(hostPanel);

        pack();
        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setResizable(false);
        setInputPanelEnabled(false);

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