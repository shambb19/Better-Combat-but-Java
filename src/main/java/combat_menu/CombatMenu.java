package combat_menu;

import __main.Main;
import combat_menu.action_panel.ActionPanel;
import format.swing_comp.SwingComp;
import format.swing_comp.SwingPane;

import javax.swing.*;
import java.awt.*;

public class CombatMenu extends JFrame {

    public static final String TITLE = "Combat";

    private final InspirationBar excessInspirationBar;
    private final EncounterListPanel initiativeListPanel;
    private final ActionPanel actionPanel;

    public static CombatMenu newInstance() {
        return new CombatMenu();
    }

    private CombatMenu() {
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setIconImage(Main.getImage());
        setJMenuBar(CombatMenuBar.newInstance());

        initiativeListPanel = EncounterListPanel.newInstance();
        excessInspirationBar = InspirationBar.newInstance();
        actionPanel = ActionPanel.newInstance();

        SwingPane.modifiable(this).withLayout(SwingPane.BORDER)
                .with(actionPanel, BorderLayout.CENTER)
                .with(SwingComp.scrollPane(initiativeListPanel), BorderLayout.EAST)
                .with(excessInspirationBar, BorderLayout.SOUTH)
                .withEmptyBorder(10);

        pack();
        setLocationRelativeTo(null);
    }

    public void logInspiration(int d4Roll) {
        excessInspirationBar.logInspirationRolls(d4Roll);
    }

    public void update() {
        initiativeListPanel.refresh();
        actionPanel.updateTurnInformation();
    }

}