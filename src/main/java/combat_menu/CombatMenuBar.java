package combat_menu;

import popup.CombatEndPopup;
import popup.EventLogPopup;
import popup.SpellManagerPopup;

import javax.swing.*;

public class CombatMenuBar extends JMenuBar {

    {
        addMenuItem("Start New Encounter", "End the current encounter without any saved progress", CombatEndPopup::restart);
        addMenuItem("Spell Manager", "Manually end concentration/effects from any dealt spell", SpellManagerPopup::run);
        addMenuItem("Event Log", "View actions taken in this combat", EventLogPopup::run);
        addMenuItem("Quit", "You know this one", CombatEndPopup::fireQuit);
    }

    private void addMenuItem(String name, String toolTip, Runnable action) {
        JMenuItem item = new JMenuItem(name);
        item.setToolTipText(toolTip);
        item.addActionListener(e -> action.run());
        add(item);
    }

}
