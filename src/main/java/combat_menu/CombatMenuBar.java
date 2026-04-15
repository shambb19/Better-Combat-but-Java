package combat_menu;

import combat_menu.popup.CombatEndPopup;
import lombok.*;

import javax.swing.*;

@RequiredArgsConstructor(staticName = "newInstance")
public class CombatMenuBar extends JMenuBar {

    {
        JMenuItem restart = new JMenuItem("Restart Encounter");
        restart.addActionListener(e -> CombatEndPopup.quit("restart"));

        JMenuItem endEarly = new JMenuItem("End Now");
        endEarly.addActionListener(e -> CombatEndPopup.run(true));

        add(restart);
    }

}
