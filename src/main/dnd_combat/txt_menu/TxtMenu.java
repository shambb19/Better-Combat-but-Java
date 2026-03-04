package txt_menu;

import character_info.Combatant;
import scenario_info.Battle;
import txt_input.BattleReader;

import javax.swing.*;
import java.awt.*;
import java.io.File;

public class TxtMenu extends JFrame {

    public static final String TITLE = "Campaign Creator";

    private final CompletedCombatantLists completedList;

    private final LowerSplitPane splitPane;
    private final CombatantInputPanel inputPanel;
    private final DownloadDocDisplayPanel displayPanel;

    public TxtMenu() {
        initialize();

        completedList = new CompletedCombatantLists(this);

        inputPanel = new CombatantInputPanel(this);
        displayPanel = new DownloadDocDisplayPanel();
        splitPane = new LowerSplitPane(inputPanel, displayPanel);

        construct();
    }

    public TxtMenu(File input) {
        initialize();

        Battle battle = new BattleReader(input).getBattle();

        completedList = new CompletedCombatantLists(battle, this);

        inputPanel = new CombatantInputPanel(this);
        displayPanel = new DownloadDocDisplayPanel(battle);
        splitPane = new LowerSplitPane(inputPanel, displayPanel);

        construct();
    }

    private void initialize() {
        setTitle(TITLE);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
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

    public void logCombatantCompleted(Combatant combatant) {
        System.out.println(combatant.toTxt());
        completedList.findAndLocateCopy(combatant);
        if (combatant.isEnemy()) {
            completedList.addEnemy(combatant);
        } else {
            completedList.addFriendly(combatant);
        }
        displayPanel.addCombatant(combatant);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
    }

    public void setInputPanelEnabled(boolean isEnabled) {
        splitPane.setInputPanelEnabled(isEnabled);
    }

}