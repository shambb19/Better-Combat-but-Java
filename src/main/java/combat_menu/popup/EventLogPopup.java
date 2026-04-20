package combat_menu.popup;

import __main.manager.CombatManager;
import util.StringUtils;

import javax.swing.*;
import java.awt.*;

import static format.ColorStyles.*;
import static format.swing_comp.SwingComp.label;
import static format.swing_comp.SwingComp.scrollPane;
import static format.swing_comp.SwingPane.*;

public class EventLogPopup extends JDialog {

    {
        setTitle("Event Log");

        fluent(this).withEmptyBorder(20, 20, 20, 20);

        scrollPane(getLogPanel()).in(this);

        setIconImage(__main.Main.getAppIcon().getImage());
        setModal(false);
        setAlwaysOnTop(true);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        pack();
        setVisible(true);
    }

    private JPanel getLogPanel() {
        JPanel panel = newArrangedAs(ONE_COLUMN, 0, 10).component();

        for (String notice : CombatManager.getActionLog()) {
            JPanel eventPanel = panelIn(panel).arrangedAs(FLOW_LEFT, 10, 0).component();

            label(StringUtils.gameTimeString(), Font.PLAIN, 11f).muted().in(eventPanel);

            if (notice.contains("heal"))
                label(notice, Font.PLAIN, 12f, HEALTHY).in(eventPanel);
            else if (notice.contains("damage"))
                label(notice, Font.PLAIN, 12f, UNKNOWN).in(eventPanel);
            else if (notice.contains("defeated"))
                label(notice, Font.BOLD, 14f, CRITICAL).in(eventPanel);
        }

        return panel;
    }

}
