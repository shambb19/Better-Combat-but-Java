package combat_menu;

import __main.Main;
import combat_menu.action_panel.ActionPanel;
import lombok.*;
import lombok.experimental.*;
import org.intellij.lang.annotations.MagicConstant;
import swing.swing_comp.SwingComp;
import swing.swing_comp.SwingPane;

import javax.swing.*;
import java.awt.*;

@FieldDefaults(makeFinal = true)
@RequiredArgsConstructor(staticName = "newInstance")
public class CombatMenu extends JFrame {

    public static String TITLE = "Combat" + Main.TITLE;

    private EncounterListPanel encounterListPanel = EncounterListPanel.newInstance();
    @Getter private ActionPanel actionPanel = ActionPanel.newInstance();

    {
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setIconImage(Main.getAppIcon().getImage());
        setJMenuBar(CombatMenuBar.newInstance());

        InspirationBar excessInspirationBar = InspirationBar.newInstance();

        SwingPane.modifiable(this).withLayout(SwingPane.BORDER)
                .with(actionPanel, BorderLayout.CENTER)
                .with(SwingComp.scrollPane(encounterListPanel).withPreferredSize(300, 0), BorderLayout.EAST)
                .with(excessInspirationBar, BorderLayout.SOUTH)
                .withEmptyBorder(10, 10, 10, 10);

        pack();
        setLocationRelativeTo(null);
        setExtendedState(MAXIMIZED_BOTH);
        setResizable(false);
    }

    public void setActionMode(
            @MagicConstant(intValues = {CombatantPanel.TURN, CombatantPanel.ATTACK, CombatantPanel.HEAL}) int mode
    ) {
        encounterListPanel.setActionMode(mode);
    }

    public void startNewTurn() {
        actionPanel.startNewTurn();
    }

    public void update() {
        encounterListPanel.updateAll();
        actionPanel.update();
    }

}