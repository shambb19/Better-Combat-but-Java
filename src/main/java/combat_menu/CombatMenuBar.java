package combat_menu;

import combat_menu.popup.CombatEndPopup;
import combat_menu.popup.EffectManagerPopup;
import lombok.*;

import javax.swing.*;

@RequiredArgsConstructor(staticName = "newInstance")
public class CombatMenuBar extends JMenuBar {

    {
        addMenuItem("Start New Encounter", "End the current encounter without any saved progress",
                () -> CombatEndPopup.quit("restart"));
        addMenuItem("Effect Manager", "Manually end the effects of any dealt spell",
                EffectManagerPopup::run);
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
