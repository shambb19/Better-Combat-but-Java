package combat_menu.action_panel;

import __main.EncounterInfo;
import _global_list.Resource;
import encounter_info.PlayerQueue;
import format.swing_comp.SwingPane;

import javax.swing.*;

import static format.swing_comp.SwingComp.button;

public class ActionButtons extends JPanel {

    public static ActionButtons newInstance(ActionPanel root) {
        return new ActionButtons(root);
    }

    private ActionButtons(ActionPanel root) {
        PlayerQueue queue = EncounterInfo.getQueue();

        SwingPane.modifiable(this).withLayout(SwingPane.TWO_COLUMN).withEmptyBorder(10);

        button(Resource.ATTACK_BUTTON, () -> root.switchTo(ActionPanel.ATTACK_OPTION))
                .withToolTip("Attack")
                .applied(b -> b.putClientProperty("JButton.buttonType", "toolBarButton"))
                .in(this);

        button(Resource.HEAL_BUTTON, () -> root.switchTo(ActionPanel.HEAL_OPTION))
                .withToolTip("Heal")
                .applied(b -> b.putClientProperty("JButton.buttonType", "toolBarButton"))
                .enabledIf(() -> EncounterInfo.getCurrentCombatant().canHeal()).in(this);

        button(Resource.INSPIRATION_BUTTON, () -> {
            if (EncounterInfo.getCurrentCombatant().useInspirationAndCheckExcess())
                root.switchTo(ActionPanel.INSPIRATION_OPTION);
        })
                .withToolTip("Use Inspiration")
                .applied(b -> b.putClientProperty("JButton.buttonType", "toolBarButton"))
                .enabledIf(() -> !EncounterInfo.getCurrentCombatant().isEnemy())
                .in(this);

        button(Resource.END_TURN_BUTTON, queue::endCurrentTurn)
                .withToolTip("End Turn")
                .applied(b -> b.putClientProperty("JButton.buttonType", "toolBarButton"))
                .in(this);
    }
}