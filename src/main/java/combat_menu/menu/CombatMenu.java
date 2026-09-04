package combat_menu.menu;

import __main.Main;
import combat_menu.action_panel.ActionPanel;
import combat_menu.action_panel.form.ActionFormPanel;
import combat_menu.encounter_info.EncounterListPanel;
import combat_menu.encounter_info.HealthBarPanel;
import combat_menu.map.CombatMapView;
import lombok.*;
import lombok.experimental.*;
import org.intellij.lang.annotations.MagicConstant;
import swing.custom.MainFrame;
import swing.fluent.SwingPane;

import java.awt.*;

import static swing.fluent.SwingPane.*;

@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class CombatMenu extends MainFrame {

    public static String TITLE = "Combat" + Main.TITLE;

    EncounterListPanel encounterListPanel = new EncounterListPanel();
    @Getter ActionPanel actionPanel = new ActionPanel();
    @Getter CombatMapView map;

    {
        setTitle(TITLE);

        map = new CombatMapView(this);
        setJMenuBar(new CombatMenuBar(map.getMapMenuItem()));

        InspirationBar excessInspirationBar = new InspirationBar();

        encounterListPanel.setPreferredSize(new Dimension(300, 0));

        fluent(this).arrangedAs(SwingPane.BORDER)
                .borderCollect(
                        center(actionPanel), east(encounterListPanel),
                        south(excessInspirationBar)
                ).withEmptyBorder(10);

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