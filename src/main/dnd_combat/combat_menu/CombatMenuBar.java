package combat_menu;

import __main.CombatMain;
import __main.SystemMain;
import util.Message;

import javax.swing.*;

public class CombatMenuBar extends JMenuBar {

    public static CombatMenuBar newInstance() {
        return new CombatMenuBar();
    }

    private CombatMenuBar() {
        JMenuItem end = new JMenuItem("End Encounter");
        end.addActionListener(e -> {
            int result = Message.confirmIf("end this combat");
            if (result == JOptionPane.YES_OPTION) {
                CombatMain.getBattle().reset();
                SystemMain.restartCombat();
            }
        });

        add(end);
    }

}
