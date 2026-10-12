package combat_menu.menu;

import __main.Main;
import combat_menu.action_panel.ActionPanel;
import combat_menu.action_panel.form.ActionFormPanel;
import combat_menu.encounter_info.EncounterListPanel;
import combat_menu.encounter_info.HealthBarPanel;
import lombok.*;
import lombok.experimental.*;
import org.intellij.lang.annotations.MagicConstant;
import swing.ColorStyles;
import swing.custom.MainFrame;
import swing.builder.PanelBuilder;

import javax.swing.*;
import java.awt.*;

@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class CombatMenu extends MainFrame {

    public static String TITLE = "Combat" + Main.TITLE;

    EncounterListPanel encounterListPanel = new EncounterListPanel();
    @Getter ActionPanel actionPanel = new ActionPanel();

    {
        setTitle(TITLE);
        setJMenuBar(new CombatMenuBar());

        InspirationBar excessInspirationBar = new InspirationBar();

        encounterListPanel.setPreferredSize(new Dimension(300, 0));

        PanelBuilder.modifyingPanel(this).layoutType(PanelBuilder.BORDER)
                .hostedComponent(actionPanel, BorderLayout.CENTER)
                .hostedComponent(encounterListPanel, BorderLayout.EAST)
                .hostedComponent(excessInspirationBar, BorderLayout.SOUTH)
                .border(BorderFactory.createEmptyBorder(10, 10, 10, 10))
                .background(ColorStyles.BACKGROUND)
                .build().validated();

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