package swing.popup;

import _manager.CombatManager;
import swing.custom.Popup;
import util.Message;

import javax.swing.*;
import java.awt.*;

import static swing.ColorStyles.*;
import static swing.builder.PanelBuilder.*;

public class EventLogPopup extends Popup {

    {
        setTitle("Event Log");
        getRootPane().setBorder(SPACER_BORDER);

        JPanel[] events = CombatManager.getActionLog().stream().map(notice -> createPanel(FLOW_LEFT,
                createLabel(notice.getTimeLogged(), Font.PLAIN, 11f, FG_MUTED),
                createLabel(notice.getLogMessage()).fontBuilder(notice.getDisplayFont())
        ).hgap(10).build().get()).toList().toArray(new JPanel[0]);

        createPanel(ONE_COLUMN, (Object[]) events).vgap(10).build().getAsScrollerIn(this, null);

        pack();
        setVisible(true);
    }

    public static void run() {
        if (CombatManager.getActionLog().isEmpty()) {
            Message.showAsInfoMessage("There are no actions to show yet.");
            return;
        }
        new EventLogPopup();
    }

}