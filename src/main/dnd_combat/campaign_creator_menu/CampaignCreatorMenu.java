package campaign_creator_menu;

import __main.Main;
import _global_list.Combatants;
import _global_list.Scenarios;
import character_info.combatant.Combatant;
import encounter_info.Battle;
import encounter_info.Scenario;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.net.URL;

public class CampaignCreatorMenu extends JFrame {

    public static final String TITLE = "Campaign Creator";

    private final CompletedElementsList completedList;

    private final LowerSplitPane splitPane;
    private final CombatantInputPanel inputPanel;
    private final ScenarioInputPanel scenarioPanel;
    private final DownloadDocDisplayPanel displayPanel;

    public static CampaignCreatorMenu newInstance(URL input) {
        return new CampaignCreatorMenu(input);
    }

    private CampaignCreatorMenu(URL input) {
        initialize();

        Combatants.init(input);
        Scenarios.init(input);
        Battle battle = Combatants.toBattle();

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
        setIconImage(Main.getImage());
    }

    private void construct() {
        add(completedList, BorderLayout.NORTH);
        add(splitPane, BorderLayout.CENTER);

        pack();
        ((JPanel) getContentPane()).setBorder(new EmptyBorder(10, 10, 10, 10));
        setLocationRelativeTo(null);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setResizable(false);
        setInputPanelEnabled(false);

        setVisible(true);
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