package combat_menu;

import _main.CombatMain;

import javax.swing.*;
import java.awt.*;

public class CombatMenu extends JFrame {

    public static final String TITLE = "Combat";

    private final InspirationBar excessInspirationBar;
    private final InitiativeListPanel initiativeListPanel;
    private final ActionPanel actionPanel;

    public CombatMenu() {
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setJMenuBar(new CombatMenuBar(this));

        initiativeListPanel = new InitiativeListPanel();
        excessInspirationBar = new InspirationBar();
        actionPanel = new ActionPanel();

        add(actionPanel, BorderLayout.CENTER);
        add(initiativeListPanel, BorderLayout.EAST);
        add(excessInspirationBar, BorderLayout.NORTH);

        pack();
        setLocationRelativeTo(null);
    }

    public void logInspiration(int d4Roll) {
        excessInspirationBar.logInspirationRolls(d4Roll);
    }

    public void update() {
        initiativeListPanel.refresh();
        actionPanel.updateTurnInformation();
        actionPanel.copyHealthBar(CombatMain.QUEUE.getCurrentCombatant().getHealthBar());
        actionPanel.getHealButton().setEnabled(CombatMain.QUEUE.getCurrentCombatant().canHeal());

        CombatMain.checkWinConditions();
    }

}
