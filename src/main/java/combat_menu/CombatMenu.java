package combat_menu;

import __main.Main;
import _manager.EncounterManager;
import combat_menu.action_panel.ActionPanel;
import combat_menu.action_panel.form.ActionFormPanel;
import combat_menu.encounter_info.EncounterListPanel;
import combat_menu.encounter_info.HealthBarPanel;
import combat_object.combatant.Combatant;
import lombok.*;
import lombok.experimental.*;
import org.intellij.lang.annotations.MagicConstant;
import swing.custom.MainFrame;
import swing.fluent.SwingPane;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static swing.fluent.SwingPane.*;

@FieldDefaults(makeFinal = true)
/*
    The Claude torture test says this will cause irreparable crashes if anything is called before
    the menu is initialized. But idk when tf I'd ever want to run this headless, so I've elected
    to ignore that bit of warning until it becomes a problem.
 */
public class CombatMenu extends MainFrame {

    public static String TITLE = "Combat" + Main.TITLE;

    private EncounterListPanel encounterListPanel = new EncounterListPanel();
    @Getter private ActionPanel actionPanel = new ActionPanel();

    {
        setTitle(TITLE);

        JMenuItem mapItem = CombatMapView.createMenuItem();
        setJMenuBar(new CombatMenuBar());

        InspirationBar excessInspirationBar = new InspirationBar();

        encounterListPanel.setPreferredSize(new Dimension(300, 0));

        fluent(this).arrangedAs(SwingPane.BORDER)
                .borderCollect(
                        center(actionPanel), east(encounterListPanel),
                        south(excessInspirationBar)
                ).withEmptyBorder(10);

        List<Combatant> combatants = new ArrayList<>();
        combatants.addAll(EncounterManager.getParty());
        combatants.addAll(EncounterManager.getEncounter().getFriendlies());
        combatants.addAll(EncounterManager.getEncounter().getEnemies());
        CombatMapView.openPlacement(this, combatants, mapItem);

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