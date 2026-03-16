package combat_menu;

import __main.CombatMain;
import combat_menu.popup.action_panel.ActionPanel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class CombatMenu extends JFrame {

    public static final String TITLE = "Combat";

    private final InspirationBar excessInspirationBar;
    private final EncounterListPanel initiativeListPanel;
    private final ActionPanel actionPanel;

    public CombatMenu() {
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setJMenuBar(CombatMenuBar.newInstance(this));

        initiativeListPanel = EncounterListPanel.newInstance();
        excessInspirationBar = InspirationBar.newInstance();
        actionPanel = ActionPanel.newInstance();

        add(actionPanel, BorderLayout.CENTER);
        add(initiativeListPanel.getScrollPane(), BorderLayout.EAST);
        add(excessInspirationBar, BorderLayout.SOUTH);

        ((JPanel) getContentPane()).setBorder(new EmptyBorder(10, 10, 10, 10));
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

        CombatMain.checkWinConditions();
    }

}