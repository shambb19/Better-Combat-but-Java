package combat_menu.action_panel;

import __main.manager.EncounterManager;
import __main.manager.InspirationManager;
import _global_list.Resource;
import combat_object.combatant.Combatant;
import encounter_info.PlayerQueue;
import format.ColorStyles;
import lombok.*;
import lombok.experimental.*;
import org.intellij.lang.annotations.MagicConstant;
import swing.swing_comp.SwingComp;
import swing.swing_comp.SwingPane;
import util.Locators;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import static swing.swing_comp.SwingComp.button;
import static swing.swing_comp.SwingComp.modifiable;

@FieldDefaults(level = AccessLevel.PRIVATE)
public class ActionButtons extends JPanel {

    final JButton attackButton, healButton, inspirationButton, endTurnButton;

    static final int MAIN_ACTION = 0, BONUS_ACTION = 1, DONE = 2;
    final ActionPanel root;
    int turnStatus;
    JButton activeButton = null;

    private ActionButtons(ActionPanel root) {
        this.root = root;
        PlayerQueue queue = EncounterManager.getQueue();

        SwingPane.modifiable(this).withLayout(SwingPane.ONE_COLUMN);

        attackButton = button(Resource.ATTACK_BUTTON, () -> onTurnAction(ActionPanel.ATTACK_OPTION))
                .withToolTip("Attack")
                .applied(this::setupToolStyle)
                .in(this)
                .component();

        healButton = button(Resource.HEAL_BUTTON, () -> onTurnAction(ActionPanel.HEAL_OPTION))
                .withToolTip("Heal")
                .applied(this::setupToolStyle)
                .in(this)
                .component();

        inspirationButton = button(Resource.INSPIRATION_BUTTON, InspirationManager.MANAGER::useInspiration)
                .withToolTip("Use Inspiration")
                .applied(this::setupToolStyle)
                .in(this)
                .component();

        endTurnButton = button(Resource.END_TURN_BUTTON, queue::endCurrentTurn)
                .withAction(root::returnToButtons)
                .withToolTip("End Turn")
                .applied(this::setupToolStyle)
                .in(this)
                .component();
    }

    private void onTurnAction(
            @MagicConstant(stringValues = {ActionPanel.ATTACK_OPTION, ActionPanel.HEAL_OPTION}) String mode
    ) {
        List.of(attackButton, healButton, inspirationButton, endTurnButton).forEach(b -> b.setEnabled(false));
        root.switchTo(mode);
    }

    private void setupToolStyle(JButton button) {
        SwingComp.modifiable(button)
                .withBackground(ColorStyles.BACKGROUND)
                .withHighlight(Color.GRAY, SwingComp.RIGHT, false)
                .withAction(b -> {
                    activeButton = b;
                    modifiable(b).withHighlight(ColorStyles.SELECTION, SwingComp.RIGHT, false);
                })
                .applied(b -> b.setMargin(new Insets(20, 20, 20, 20)));

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (button.isEnabled())
                    modifiable(button).withHighlight(ColorStyles.SELECTION, SwingComp.RIGHT, false);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (button.equals(activeButton)) return;
                restoreIdleHighlight(button);
            }
        });
    }

    private void restoreIdleHighlight(JButton btn) {
        if (!btn.isEnabled())
            modifiable(btn).withHighlight(ColorStyles.LOCKED_FG, SwingComp.RIGHT, false);
        else if (btn == attackButton || btn == healButton)
            modifiable(btn).withHighlight(
                    turnStatus == BONUS_ACTION ? ColorStyles.GOLD : Color.GRAY,
                    SwingComp.RIGHT, false);
        else
            modifiable(btn).withHighlight(Color.GRAY, SwingComp.RIGHT, false);
    }

    public static ActionButtons newInstance(ActionPanel root) {
        return new ActionButtons(root);
    }

    public void onActionCancelled() {
        deselectAllButtons();
        refreshLockState();
    }

    public void deselectAllButtons() {
        List.of(attackButton, healButton, inspirationButton, endTurnButton).forEach(b -> b.setEnabled(true));
        for (int i = 0; i < getComponentCount(); i++)
            if (getComponent(i) instanceof JButton b)
                restoreIdleHighlight(b);
        activeButton = null;
    }

    private void refreshLockState() {
        boolean canHeal = !Locators.getTargetList(false).isEmpty();

        applyButtonState(attackButton, "Attack", "Bonus attack");
        applyButtonState(healButton, "Heal", "Bonus heal");

        if (!canHeal)
            modifiable(healButton)
                    .withHighlight(ColorStyles.LOCKED_FG, SwingComp.RIGHT, false)
                    .withBackground(ColorStyles.LOCKED)
                    .withToolTip("No available heal targets")
                    .disabled();

        if (EncounterManager.getCurrentCombatant().isEnemy())
            modifiable(inspirationButton)
                    .withHighlight(ColorStyles.LOCKED_FG, SwingComp.RIGHT, false)
                    .withBackground(ColorStyles.LOCKED)
                    .withToolTip("Enemies cannot use inspiration")
                    .disabled();
        else
            modifiable(inspirationButton)
                    .withHighlight(Color.GRAY, SwingComp.RIGHT, false)
                    .withBackground(ColorStyles.BACKGROUND)
                    .enabled();

        revalidate();
        repaint();
    }

    private void applyButtonState(JButton btn, String normalTip, String bonusTip) {
        switch (turnStatus) {
            case MAIN_ACTION -> modifiable(btn)
                    .withHighlight(
                            btn.equals(activeButton) ? ColorStyles.SELECTION : Color.GRAY,
                            SwingComp.RIGHT, false)
                    .withBackground(ColorStyles.BACKGROUND)
                    .withToolTip(normalTip)
                    .enabled();
            case BONUS_ACTION -> modifiable(btn)
                    .withHighlight(ColorStyles.GOLD, SwingComp.RIGHT, false)
                    .withBackground(ColorStyles.GOLD_TINT)
                    .withToolTip(bonusTip + " (bonus action)")
                    .enabled();
            case DONE -> modifiable(btn)
                    .withHighlight(ColorStyles.LOCKED_FG, SwingComp.RIGHT, false)
                    .withBackground(ColorStyles.LOCKED)
                    .withToolTip(normalTip + " (no more actions)")
                    .disabled();
        }
    }

    public void onMainActionConfirmed() {
        Combatant current = EncounterManager.getCurrentCombatant();

        if (turnStatus == MAIN_ACTION) {
            if (current != null && current.isEnemy())
                turnStatus = DONE;
            else
                turnStatus = BONUS_ACTION;
        } else if (turnStatus == BONUS_ACTION) {
            turnStatus = DONE;
        }

        deselectAllButtons();
        refreshLockState();
    }

    public void resetForNewTurn() {
        turnStatus = MAIN_ACTION;
        activeButton = null;
        deselectAllButtons();
        refreshLockState();
    }

    public void confirmButtonStates() {
        refreshLockState();
    }
}