package swing.popup;

import _manager.CombatManager;
import swing.custom.Popup;
import swing.builder.ComponentBuilder;
import util.Message;

import javax.swing.*;
import java.awt.*;

import static swing.ColorStyles.*;
import static swing.builder.PanelBuilder.*;

public class EventLogPopup extends Popup {

    {
        setTitle("Event Log");
        createLogPanel();
        pack();
        setVisible(true);
    }

    private void createLogPanel() {
        JPanel panel = createPanel(ONE_COLUMN).hgap(10).build().get();

        for (CombatManager.LoggedAction notice : CombatManager.getActionLog()) {
            JPanel eventPanel = createPanel(FLOW_LEFT).hgap(10).build().putIn(panel);

            ComponentBuilder.createLabel(notice.getTimeLogged(), Font.PLAIN, 11f, FG_MUTED)
                    .build().putIn(eventPanel);

            String logMessage = notice.getLogMessage();

            int font = Font.PLAIN;
            float size = 12f;
            Color foreground = HEALTHY;
            if (logMessage.contains("damage")) foreground = UNKNOWN;
            else if (logMessage.contains("defeated")) {
                font = Font.BOLD;
                size = 14f;
                foreground = CRITICAL;
            }
            ComponentBuilder.createLabel(logMessage, font, size, foreground).build().putIn(eventPanel);
        }

        add(new JScrollPane(panel));
        modifyingPanel(this).border(SPACER_BORDER).build().validated();
    }

    public static void run() {
        if (CombatManager.getActionLog().isEmpty()) {
            Message.showAsInfoMessage("There are no actions to show yet.");
            return;
        }
        new EventLogPopup();
    }

}
