package combat_menu;

import __main.EncounterInfo;
import __main.Main;
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
                EncounterInfo.getBattle().reset();
                Main.restartCombat();
            }
        });

        add(end);
    }

}
