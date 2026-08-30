package combat_menu;

import popup.CombatEndPopup;
import popup.EventLogPopup;
import popup.SpellManagerPopup;

import javax.swing.*;
import java.util.List;

public class CombatMenuBar extends JMenuBar {

    public CombatMenuBar(JMenuItem mapItem) {
        class Item extends JMenuItem {
            Item(String name, String toolTip, Runnable action) {
                setText(name);
                setToolTipText(toolTip);
                addActionListener(e -> action.run());
            }
        }
        List.of(
                mapItem,
                new Item("Start New Encounter", "End the current encounter without saving", CombatEndPopup::restart),
                new Item("Spell Manager", "Manually adjust status/effects of dealt spells", SpellManagerPopup::run),
                new Item("Event Log", "View a log of actions in this combat", EventLogPopup::run),
                new Item("Quit", "You know this one", CombatEndPopup::fireQuit)
        ).forEach(this::add);
    }

}
