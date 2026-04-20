package combat_menu;

import combat_menu.popup.CombatEndPopup;
import combat_menu.popup.EventLogPopup;
import combat_menu.popup.SpellManagerPopup;

import javax.swing.*;

public class CombatMenuBar extends JMenuBar {

    {
        addMenuItem("Start New Encounter", "End the current encounter without any saved progress",
                () -> CombatEndPopup.quit("restart"));
        addMenuItem("Spell Manager", "Manually end concentration and/or the effects of any dealt spell",
                SpellManagerPopup::run);
        addMenuItem("Event Log", "View a list of damage, defeats, and heals from this combat",
                EventLogPopup::new);
        addMenuItem("Quit", "You know this one",
                () -> CombatEndPopup.run(CombatEndPopup.QUIT));
    }

    private void addMenuItem(String name, String toolTip, Runnable action) {
        JMenuItem item = new JMenuItem(name);
        item.setToolTipText(toolTip);
        item.addActionListener(e -> action.run());
        add(item);
    }

}
