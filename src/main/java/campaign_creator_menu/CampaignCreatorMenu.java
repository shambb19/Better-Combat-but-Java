package campaign_creator_menu;

import __main.Main;
import _global_list.Combatants;
import campaign_creator_menu.input.CombatantInputPanel;
import campaign_creator_menu.input.ScenarioInputPanel;
import combat_object.CombatObject;
import combat_object.combatant.Combatant;
import combat_object.scenario.Scenario;
import encounter.Encounter;
import format.ColorStyles;
import lombok.*;
import lombok.experimental.*;

import javax.swing.*;
import java.awt.*;

@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class CampaignCreatorMenu extends JFrame {

    public static final String TITLE = "Campaign Creator" + Main.TITLE;

    CompletedElementsList completedList;

    HostPanel hostPanel;
    CombatantInputPanel inputPanel;
    ScenarioInputPanel scenarioPanel;
    DownloadDocDisplayPanel displayPanel;

    public static CampaignCreatorMenu newInstance() {
        return new CampaignCreatorMenu();
    }

    private CampaignCreatorMenu() {
        setTitle(TITLE);
        setIconImage(__main.Main.getAppIcon().getImage());
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        setResizable(false);
        setBackground(ColorStyles.BACKGROUND);

        Encounter encounter = Combatants.toBattle();

        completedList = new CompletedElementsList(encounter, this);
        inputPanel = new CombatantInputPanel(this);
        scenarioPanel = new ScenarioInputPanel(completedList, this);
        LevelUpPanel levelUpPanel = new LevelUpPanel(encounter, this);
        displayPanel = new DownloadDocDisplayPanel(encounter);
        hostPanel = new HostPanel(inputPanel, scenarioPanel, levelUpPanel, completedList, displayPanel);

        add(hostPanel);

        GraphicsConfiguration config = getGraphicsConfiguration();
        Rectangle bounds = config.getBounds();
        Insets insets = Toolkit.getDefaultToolkit().getScreenInsets(config);

        int SHADOW = 8;

        int x = bounds.x + insets.left - SHADOW;
        int y = bounds.y + insets.top;
        int width = bounds.width - insets.left - insets.right + (SHADOW * 2);
        int height = bounds.height - insets.top - insets.bottom + SHADOW;

        setBounds(x, y, width, height);

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