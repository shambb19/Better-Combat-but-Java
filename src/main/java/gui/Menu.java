package gui;

import javax.swing.*;
import java.awt.*;

public class Menu extends JFrame {

    private final InspirationBar excessInspirationBar;
    private final InitiativeListPanel initiativeListPanel;

    public Menu() {
        setTitle("Better Combat but Java");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        initiativeListPanel = new InitiativeListPanel();
        excessInspirationBar = new InspirationBar();

        add(new ActionPanel(), BorderLayout.CENTER);
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
    }

}
