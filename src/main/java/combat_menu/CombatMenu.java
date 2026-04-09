package combat_menu;

import __main.Main;
import __main.manager.InspirationManager;
import combat_menu.action_panel.ActionPanel;
import org.intellij.lang.annotations.MagicConstant;
import swing.swing_comp.SwingComp;
import swing.swing_comp.SwingPane;

import javax.swing.*;
import java.awt.*;

public class CombatMenu extends JFrame {

    public static final String TITLE = "Combat" + Main.TITLE;

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
        InspirationBar excessInspirationBar = new InspirationBar(InspirationManager.MANAGER);
        actionPanel = ActionPanel.newInstance();

        SwingPane.modifiable(this).withLayout(SwingPane.BORDER)
                .with(actionPanel, BorderLayout.CENTER)
                .with(SwingComp.scrollPane(initiativeListPanel).withSize(300, 0), BorderLayout.EAST)
                .with(excessInspirationBar, BorderLayout.SOUTH)
                .withEmptyBorder(10);

        pack();
        setLocationRelativeTo(null);
        setExtendedState(MAXIMIZED_BOTH);
        setResizable(false);
    }

    public void setActionMode(
            @MagicConstant(intValues = {CombatantPanel.TURN, CombatantPanel.ATTACK, CombatantPanel.HEAL}) int mode
    ) {
        initiativeListPanel.setActionMode(mode);
    }

    public void update() {
        initiativeListPanel.updateAll();
        actionPanel.update();
    }

}