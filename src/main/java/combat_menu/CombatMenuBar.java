package combat_menu;

import combat_menu.popup.CombatEndPopup;

import javax.swing.*;

public class CombatMenuBar extends JMenuBar {

    public static CombatMenuBar newInstance() {
        return new CombatMenuBar();
    }

    private CombatMenuBar() {
        JMenuItem end = new JMenuItem("End Encounter");
        end.addActionListener(e -> CombatEndPopup.quit("restart"));

        add(end);
    }

}
