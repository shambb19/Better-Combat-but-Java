package combat_menu;

import __main.CombatMain;
import __main.SystemMain;

import javax.swing.*;

public class CombatMenuBar extends JMenuBar {

    public CombatMenuBar(CombatMenu root) {
        JMenuItem end = new JMenuItem("End Encounter");
        end.addActionListener(e -> {
            int result = JOptionPane.showConfirmDialog(
                    root,
                    "Are you sure you want to end this combat? The action cannot be undone, " +
                            "but hps will be carried over.",
                    CombatMenu.TITLE,
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE
            );
            if (result == JOptionPane.YES_OPTION) {
                CombatMain.BATTLE.reset();
                SystemMain.restartCombat();
            }
        });

        add(end);
    }

}
