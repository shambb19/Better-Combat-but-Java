package combat_menu;

import __main.Main;
import combat_menu.action_panel.ActionPanel;
import combat_menu.action_panel.form.ActionFormPanel;
import combat_menu.encounter_info.EncounterListPanel;
import combat_menu.encounter_info.HealthBarPanel;
import format.swing_comp.SwingPane;
import lombok.*;
import lombok.experimental.*;
import org.intellij.lang.annotations.MagicConstant;

import javax.swing.*;
import java.awt.*;

import static format.swing_comp.SwingPane.*;

@FieldDefaults(makeFinal = true)
public class CombatMenu extends JFrame {

    public static String TITLE = "Combat" + Main.TITLE;

    private EncounterListPanel encounterListPanel = new EncounterListPanel();
    @Getter private ActionPanel actionPanel = new ActionPanel();

    {
        setTitle(TITLE);
        setIconImage(__main.Main.getAppIcon().getImage());
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        setJMenuBar(new CombatMenuBar());

        InspirationBar excessInspirationBar = new InspirationBar();

        encounterListPanel.setPreferredSize(new Dimension(300, 0));

        fluent(this).arrangedAs(SwingPane.BORDER)
                .borderCollect(
                        center(actionPanel), east(encounterListPanel),
                        south(excessInspirationBar)
                ).withEmptyBorder(10, 10, 10, 10);

        GraphicsConfiguration config = getGraphicsConfiguration();
        Rectangle bounds = config.getBounds();
        Insets insets = Toolkit.getDefaultToolkit().getScreenInsets(config);

        int SHADOW = 8;

        int x = bounds.x + insets.left - SHADOW;
        int y = bounds.y + insets.top;
        int width = bounds.width - insets.left - insets.right + (SHADOW * 2);
        int height = bounds.height - insets.top - insets.bottom + SHADOW;

        setBounds(x, y, width, height);

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