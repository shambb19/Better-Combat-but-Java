package popup;

import manager.CombatManager;
import swing.custom.Popup;
import util.Message;

import javax.swing.*;
import java.awt.*;

import static swing.ColorStyles.*;
import static swing.fluent.SwingComp.label;
import static swing.fluent.SwingComp.scrollPane;
import static swing.fluent.SwingPane.*;

public class EventLogPopup extends Popup {

    {
        setTitle("Event Log");

        fluent(this).collect(scrollPane(getLogPanel())).spaced();

        pack();
        setVisible(true);
    }

    private JPanel getLogPanel() {
        JPanel panel = newArrangedAs(ONE_COLUMN, 0, 10).component();

        for (CombatManager.LoggedAction notice : CombatManager.getActionLog()) {
            JPanel eventPanel = panelIn(panel).arrangedAs(FLOW_LEFT, 10, 0).component();

            label(notice.getTimeLogged(), 11f).muted().in(eventPanel);

            String logMessage = notice.getLogMessage();

            if (logMessage.contains("heal"))
                label(logMessage, Font.PLAIN, 12f, HEALTHY).in(eventPanel);
            else if (logMessage.contains("damage"))
                label(logMessage, Font.PLAIN, 12f, UNKNOWN).in(eventPanel);
            else if (logMessage.contains("defeated"))
                label(logMessage, Font.BOLD, 14f, CRITICAL).in(eventPanel);
        }

        return panel;
    }

    public static void run() {
        if (CombatManager.getActionLog().isEmpty()) {
            Message.showAsInfoMessage("There are no actions to show yet.");
            return;
        }
        new EventLogPopup();
    }

}
