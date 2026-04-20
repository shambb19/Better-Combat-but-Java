package combat_menu.action_panel;

import __main.manager.EncounterManager;
import __main.manager.InspirationManager;
import _global_list.Resource;
import format.swing_comp.SwingPane;
import lombok.*;
import lombok.experimental.*;
import org.intellij.lang.annotations.MagicConstant;
import util.Locators;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import static _global_list.Resource.*;
import static combat_menu.action_panel.ActionPanel.ATTACK_OPTION;
import static combat_menu.action_panel.ActionPanel.HEAL_OPTION;
import static format.ColorStyles.*;

@FieldDefaults(level = AccessLevel.PRIVATE)
@ExtensionMethod(ActionButtons.class)
public class ActionButtons extends JPanel {

    static final int
            MAIN_ACTION = 0, BONUS_ACTION = 1, DONE = 2,
            CONFIRM = 3, CANCEL = 4, NEW_TURN = 5;

    final ActionButton attackButton, healButton, inspirationButton, endTurnButton;
    final ActionPanel root;

    int turnStatus;
    ActionButton activeButton = null;

    private ActionButtons(ActionPanel root) {
        this.root = root;

        SwingPane.fluent(this).arrangedAs(SwingPane.ONE_COLUMN);

        attackButton = new ActionButton("Attack", ATTACK_BUTTON, () -> onTurnAction(ATTACK_OPTION));
        healButton = new ActionButton("Heal", HEAL_BUTTON, () -> onTurnAction(HEAL_OPTION));
        inspirationButton = new ActionButton("Use Inspiration", INSPIRATION_BUTTON, InspirationManager.MANAGER::useInspiration);
        endTurnButton = new ActionButton("End Turn", END_TURN_BUTTON, EncounterManager::endCurrentTurn);
    }

    private void onTurnAction(@MagicConstant(stringValues = {ATTACK_OPTION, HEAL_OPTION}) String mode) {
        if (mode.equals(ATTACK_OPTION)) activeButton = attackButton;
        else if (mode.equals(HEAL_OPTION)) activeButton = healButton;

        refreshLockState();
        root.switchTo(mode);
    }

    public void refreshLockState() {
        List.of(attackButton, healButton, inspirationButton, endTurnButton).forEach(ActionButton::refreshState);
        revalidate();
        repaint();
    }

    public static ActionButtons newInstance(ActionPanel root) {
        return new ActionButtons(root);
    }

    public void logActionChange(@MagicConstant(intValues = {CONFIRM, CANCEL, NEW_TURN}) int changeType) {
        if (changeType == NEW_TURN) {
            turnStatus = MAIN_ACTION;
        } else if (changeType == CONFIRM) {
            if (turnStatus == MAIN_ACTION)
                turnStatus = EncounterManager.getCurrentCombatant().isEnemy() ? DONE : BONUS_ACTION;
            else if (turnStatus == BONUS_ACTION)
                turnStatus = DONE;
        }

        activeButton = null;
        refreshLockState();
    }

    @EqualsAndHashCode(callSuper = true) @Value class ActionButton extends JButton {
        String buttonType;

        ActionButton(String buttonType, Resource icon, Runnable runnable) {
            this.buttonType = buttonType;

            Image image = new ImageIcon(icon.getUrl()).getImage().getScaledInstance(80, 80, Image.SCALE_SMOOTH);
            Image resized = image.getScaledInstance(80, 80, Image.SCALE_SMOOTH);

            setIcon(new ImageIcon(resized));
            setBackground(BACKGROUND);
            setMargin(new Insets(20, 20, 20, 20));
            addActionListener(e -> {
                addHighlight(SELECTION);
                runnable.run();
            });

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    if (activeButton == null && isButtonAvailable()) addHighlight(SELECTION);
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    if (!ActionButton.this.equals(activeButton)) refreshState();
                }
            });

            ActionButtons.this.add(this);
            refreshState();
        }

        void refreshState() {
            Color highlightColor = Color.GRAY;
            Color backgroundColor = BACKGROUND;
            String toolTipText = buttonType;
            boolean enabled = true;

            if (activeButton != null) {
                toolTipText = "Finish action first";
                if (equals(activeButton)) {
                    highlightColor = SELECTION;
                } else {
                    enabled = false;
                }
            } else if (buttonType.equals("Attack") || buttonType.equals("Heal")) {
                if (!isButtonAvailable()) {
                    toolTipText += " (No Actions Available)";
                    enabled = false;
                } else if (turnStatus == BONUS_ACTION) {
                    highlightColor = GOLD;
                    backgroundColor = GOLD_TINT;
                    toolTipText += " (Bonus Action)";
                }
            } else if (buttonType.equals("Use Inspiration") && EncounterManager.getCurrentCombatant().isEnemy()) {
                toolTipText = "Inspiration use is for the friendly team only";
                enabled = false;
            }

            setToolTipText(toolTipText);
            if (enabled) {
                addHighlight(highlightColor);
                setBackground(backgroundColor);
            } else {
                addHighlight(TEXT_LOCKED);
                setBackground(BG_LOCKED);
            }
        }

        private boolean isButtonAvailable() {
            boolean isAnyTargets = !Locators.getTargetList(buttonType.equals("Attack")).isEmpty();
            return switch (buttonType) {
                case "Attack", "Heal" -> isAnyTargets && turnStatus != DONE;
                case "Use Inspiration" -> !EncounterManager.getCurrentCombatant().isEnemy();
                case "End Turn" -> true;
                default -> false;
            };
        }

        private void addHighlight(Color color) {
            setBorder(BorderFactory.createCompoundBorder(
                    new MatteBorder(0, 0, 0, 6, color),
                    new EmptyBorder(10, 15, 10, 15)
            ));
        }
    }
}