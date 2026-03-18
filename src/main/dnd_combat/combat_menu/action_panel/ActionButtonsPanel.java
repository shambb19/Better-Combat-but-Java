package combat_menu.action_panel;

import __main.CombatMain;
import encounter_info.PlayerQueue;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.Map;
import java.util.Objects;

public class ActionButtonsPanel extends JPanel {

    private final PlayerQueue queue;

    private final JButton healButton;
    private final JButton inspirationButton;

    private static final Map<icons, String> buttonPics = Map.of(
            icons.ATTACK, "/attack-button.png",
            icons.HEAL, "/heal-button.png",
            icons.INSPIRATION, "/inspiration-button.png",
            icons.END_TURN, "/end-turn-button.png"
    );

    public static ActionButtonsPanel newInstance(ActionPanel root) {
        return new ActionButtonsPanel(root);
    }

    private ActionButtonsPanel(ActionPanel root) {
        queue = CombatMain.getQueue();

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(new EmptyBorder(10, 10, 10, 10));

        JButton attackButton = new JButton();
        attackButton.addActionListener(e -> root.switchTo(ActionPanel.ATTACK_OPTION));
        attackButton.setToolTipText("Attack");
        attackButton.putClientProperty("JButton.buttonType", "toolBarButton");
        setIcon(attackButton, icons.ATTACK);

        healButton = new JButton();
        healButton.addActionListener(e -> root.switchTo(ActionPanel.HEAL_OPTION));
        healButton.setToolTipText("Heal");
        healButton.putClientProperty("JButton.buttonType", "toolBarButton");
        setIcon(healButton, icons.HEAL);

        inspirationButton = new JButton();
        inspirationButton.addActionListener(e -> {
            boolean isExcess = queue.getCurrentCombatant().useInspirationAndCheckExcess();
            if (isExcess) {
                root.switchTo(ActionPanel.INSPIRATION_OPTION);
            }
            CombatMain.logAction();
        });
        inspirationButton.setToolTipText("Use Inspiration");
        inspirationButton.putClientProperty("JButton.buttonType", "toolBarButton");
        setIcon(inspirationButton, icons.INSPIRATION);

        JButton endTurnButton = new JButton();
        endTurnButton.addActionListener(e -> {
            queue.endCurrentTurn();
            updateTurnInformation();
            CombatMain.logAction();
        });
        endTurnButton.setToolTipText("End Turn");
        endTurnButton.putClientProperty("JButton.buttonType", "toolBarButton");
        setIcon(endTurnButton, icons.END_TURN);

        addPanel(attackButton, healButton, inspirationButton, endTurnButton);

        updateTurnInformation();
    }

    private void addPanel(JComponent... comps) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        for (JComponent comp : comps) {
            panel.add(comp);
            comp.setAlignmentX(Component.CENTER_ALIGNMENT);
        }

        add(panel);
    }

    public void updateTurnInformation() {
        inspirationButton.setEnabled(!queue.getCurrentCombatant().isEnemy());
        healButton.setEnabled(queue.getCurrentCombatant().canHeal());
    }

    private void setIcon(JButton button, icons name) {
        Image image = new ImageIcon(Objects.requireNonNull(getClass().getResource(buttonPics.get(name)))).getImage();
        Image resized = image.getScaledInstance(80, 80, Image.SCALE_SMOOTH);

        button.setIcon(new ImageIcon(resized));
    }

    private enum icons {ATTACK, HEAL, INSPIRATION, END_TURN}
}