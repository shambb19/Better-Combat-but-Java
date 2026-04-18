package combat_menu;

import __main.Main;
import combat_menu.action_panel.ActionPanel;
import combat_menu.action_panel.form.ActionFormPanel;
import combat_menu.encounter_info.EncounterListPanel;
import combat_menu.encounter_info.HealthBarPanel;
import lombok.*;
import lombok.experimental.*;
import org.intellij.lang.annotations.MagicConstant;
import swing.swing_comp.SwingPane;

import javax.swing.*;
import java.awt.*;

import static swing.swing_comp.SwingPane.*;

@FieldDefaults(makeFinal = true)
@RequiredArgsConstructor(staticName = "newInstance")
public class CombatMenu extends JFrame {

    public static String TITLE = "Combat" + Main.TITLE;

    private EncounterListPanel encounterListPanel = new EncounterListPanel();
    @Getter private ActionPanel actionPanel = new ActionPanel();

    {
        setTitle(TITLE);
        setIconImage(__main.Main.getAppIcon().getImage());
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        setJMenuBar(CombatMenuBar.newInstance());

        InspirationBar excessInspirationBar = InspirationBar.newInstance();

        encounterListPanel.setPreferredSize(new Dimension(300, 0));

        fluent(this).arrangedAs(SwingPane.BORDER)
                .borderCollect(
                        center(actionPanel), east(encounterListPanel),
                        south(excessInspirationBar)
                ).withEmptyBorder(10, 10, 10, 10);

        pack();
        setLocationRelativeTo(null);
        setExtendedState(Frame.MAXIMIZED_BOTH);
        setResizable(false);
        setVisible(true);
    }

    public void endActionState() {
        encounterListPanel.endActionState();
    }

    public void setActionMode(
            @MagicConstant(intValues = {HealthBarPanel.ATTACK, HealthBarPanel.HEAL}) int mode, ActionFormPanel dest
    ) {
        encounterListPanel.setActionMode(mode, dest);
    }

    public void startNewTurn() {
        actionPanel.startNewTurn();
    }

    public void update() {
        encounterListPanel.updateAll();
        actionPanel.update();
    }

}