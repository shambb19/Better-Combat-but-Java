package combat_menu.action_panel;

import __main.EncounterInfo;
import __main.InspirationManager;
import _global_list.Resource;
import encounter_info.PlayerQueue;
import format.ColorStyles;
import swing.swing_comp.SwingComp;
import swing.swing_comp.SwingPane;
import util.Locators;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import static swing.swing_comp.SwingComp.button;
import static swing.swing_comp.SwingComp.modifiable;

public class ActionButtons extends JPanel {

    private JButton activeButton;

    JButton healButton, inspirationButton;

    private ActionButtons(ActionPanel root) {
        PlayerQueue queue = EncounterInfo.getQueue();

        SwingPane.modifiable(this).withLayout(SwingPane.ONE_COLUMN);

        button(Resource.ATTACK_BUTTON, () -> root.switchTo(ActionPanel.ATTACK_OPTION))
                .withToolTip("Attack")
                .applied(this::setupToolStyle)
                .in(this);

        healButton = button(Resource.HEAL_BUTTON, () -> root.switchTo(ActionPanel.HEAL_OPTION))
                .withToolTip("Heal")
                .applied(this::setupToolStyle)
                .enabledIf(() -> EncounterInfo.getCurrentCombatant().canHeal())
                .in(this)
                .build();

        inspirationButton = button(Resource.INSPIRATION_BUTTON, InspirationManager.MANAGER::useInspiration)
                .withToolTip("Use Inspiration")
                .applied(this::setupToolStyle)
                .enabledIf(() -> !EncounterInfo.getCurrentCombatant().isEnemy())
                .in(this)
                .build();

        button(Resource.END_TURN_BUTTON, queue::endCurrentTurn)
                .withAction(root::returnToButtons)
                .withToolTip("End Turn")
                .applied(this::setupToolStyle)
                .in(this);
    }

    private void setupToolStyle(JButton button) {
        SwingComp.modifiable(button)
                .withBackground(ColorStyles.BACKGROUND)
                .withHighlight(Color.GRAY, SwingComp.RIGHT, false)
                .withAction(b -> {
                    modifiable(b).withHighlight(ColorStyles.SELECTION, SwingComp.RIGHT, false);
                    activeButton = b;
                })
                .applied(b -> {
                    b.setMargin(new Insets(20, 20, 20, 20));
                    b.addMouseListener(new MouseAdapter() {
                        @Override
                        public void mouseEntered(MouseEvent e) {
                            if (b.isEnabled())
                                modifiable(b).withHighlight(ColorStyles.SELECTION, SwingComp.RIGHT, false);
                            super.mouseEntered(e);
                        }

                        @Override
                        public void mouseExited(MouseEvent e) {
                            super.mouseExited(e);
                            if (b.equals(activeButton)) return;
                            modifiable(b).withHighlight(Color.GRAY, SwingComp.RIGHT, false);
                        }
                    });
                });
    }

    public void confirmButtonStates() {
        healButton.setEnabled(!Locators.getTargetList(false).isEmpty());
        inspirationButton.setEnabled(!EncounterInfo.getCurrentCombatant().isEnemy());
    }

    public void deselectAllButtons() {
        for (int i = 0; i < getComponentCount(); i++)
            SwingComp.modifiable((JButton) getComponent(i)).withHighlight(Color.GRAY, SwingComp.RIGHT, false);
    }

    public static ActionButtons newInstance(ActionPanel root) {
        return new ActionButtons(root);
    }
}