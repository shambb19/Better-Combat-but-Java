package combat_menu;

import __main.CombatMain;

import javax.swing.*;
import java.awt.*;

public class CombatMenu extends JFrame {

    public static final String TITLE = "Combat";

    private final InspirationBar excessInspirationBar;
    private final CombatantListPanel initiativeListPanel;
    private final CurrentCombatantPanel currentCombatantPanel;

    public CombatMenu() {
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setJMenuBar(new CombatMenuBar(this));

        initiativeListPanel = new CombatantListPanel();
        excessInspirationBar = new InspirationBar();
        currentCombatantPanel = new CurrentCombatantPanel();

        add(currentCombatantPanel, BorderLayout.CENTER);
        add(initiativeListPanel.getScrollPane(), BorderLayout.EAST);
        add(excessInspirationBar, BorderLayout.NORTH);

        pack();
        setLocationRelativeTo(null);
    }

    public void logInspiration(int d4Roll) {
        excessInspirationBar.logInspirationRolls(d4Roll);
    }

    public void update() {
        initiativeListPanel.refresh();
        currentCombatantPanel.updateTurnInformation();
        currentCombatantPanel.copyHealthBar(CombatMain.QUEUE.getCurrentCombatant().getHealthBar());
        currentCombatantPanel.getHealButton().setEnabled(CombatMain.QUEUE.getCurrentCombatant().canHeal());

        CombatMain.checkWinConditions();
    }

}
