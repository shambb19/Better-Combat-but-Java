package combat_menu;

import admin.Admin;
import main.CombatMain;
import main.SystemMain;

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

        JMenuItem admin = new JMenuItem("Admin");
        admin.addActionListener(e -> {
            String result = JOptionPane.showInputDialog(
                    null,
                    "Enter admin code",
                    "Admin",
                    JOptionPane.INFORMATION_MESSAGE
            );
            Admin.logAdminRequest(result);
        });

        add(end);
        add(admin);
    }

}
