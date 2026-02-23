package gui;

import combat.Main;

import javax.swing.*;
import java.awt.*;

public class Menu extends JFrame {

    private final InspirationBar excessInspirationBar;
    private final InitiativeListPanel initiativeListPanel;
    private final ActionPanel actionPanel;

    public Menu() {
        setTitle(Main.TITLE);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

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
        actionPanel.copyHealthBar(Main.queue.getCurrentCombatant().getHealthBar());
        actionPanel.getHealButton().setEnabled(Main.queue.getCurrentCombatant().canHeal());

        Main.checkWinConditions();
    }

}
