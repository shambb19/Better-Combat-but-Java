package campaign_creator;

import character_info.combatant.Combatant;
import scenario_info.Battle;
import scenario_info.Scenario;
import txt_input.CampaignReader;

import javax.swing.*;
import java.awt.*;
import java.io.File;

public class TxtMenu extends JFrame {

    public static final String TITLE = "Campaign Creator";

    private final CompletedElementsList completedList;

    private final LowerSplitPane splitPane;
    private final CombatantInputPanel inputPanel;
    private final ScenarioInputPanel scenarioPanel;
    private final DownloadDocDisplayPanel displayPanel;

    public TxtMenu() {
        initialize();

        completedList = new CompletedElementsList(this);

        inputPanel = new CombatantInputPanel(this);
        scenarioPanel = new ScenarioInputPanel(completedList, this);
        displayPanel = new DownloadDocDisplayPanel();
        splitPane = new LowerSplitPane(inputPanel, scenarioPanel, displayPanel);
        construct();
    }

    public TxtMenu(File input) {
        initialize();

        Battle battle = new CampaignReader(input).getBattle();

        completedList = new CompletedElementsList(battle, this);

        inputPanel = new CombatantInputPanel(this);
        scenarioPanel = new ScenarioInputPanel(completedList, this);
        displayPanel = new DownloadDocDisplayPanel(battle);
        splitPane = new LowerSplitPane(inputPanel, scenarioPanel, displayPanel);

        construct();
    }

    private void initialize() {
        setTitle(TITLE);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setJMenuBar(new TextMenuBar());
    }

    private void construct() {
        add(completedList, BorderLayout.NORTH);
        add(splitPane, BorderLayout.CENTER);

        pack();
        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setResizable(false);
        setInputPanelEnabled(false);
    }

    public void editCombatant(Combatant selection, boolean isNew) {
        if (isNew) {
            inputPanel.openNew(selection.isEnemy());
        } else {
            inputPanel.openExisting(selection);
        }
    }

    public void editScenario(Scenario selection, boolean isNew) {
        if (isNew) {
            scenarioPanel.openNew();
        } else {
            scenarioPanel.openExisting(selection);
        }
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
        if (isEnabled) {
            splitPane.changeInputPanel(LowerSplitPane.COMBATANT_INPUT);
        } else {
            splitPane.changeInputPanel(LowerSplitPane.DISABLED);
        }
    }

    public void setScenarioPanelEnabled(boolean isEnabled) {
        if (isEnabled) {
            splitPane.changeInputPanel(LowerSplitPane.SCENARIO_INPUT);
        } else {
            splitPane.changeInputPanel(LowerSplitPane.DISABLED);
        }
    }

}