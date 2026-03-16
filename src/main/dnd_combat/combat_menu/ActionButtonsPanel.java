package combat_menu;

import __main.CombatMain;
import character_info.combatant.Combatant;
import combat_menu.popup.action_panel.ActionPanel;
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
        queue = CombatMain.QUEUE;

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
        inspirationButton.addActionListener(e -> useInspiration());
        inspirationButton.setToolTipText("Use Inspiration");
        inspirationButton.putClientProperty("JButton.buttonType", "toolBarButton");
        setIcon(inspirationButton, icons.INSPIRATION);

        JButton endTurnButton = new JButton();
        endTurnButton.addActionListener(e -> endTurn());
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

    private static int promptValueFromRoll(String rollMeaning, int dieSize) {
        int collectedValue = -1;
        while (collectedValue < 0 || collectedValue > dieSize) {
            try {
                String input = JOptionPane.showInputDialog(
                        CombatMain.COMBAT_MENU,
                        "Enter " + "1d" + dieSize + " roll for " + rollMeaning + ".",
                        "Better Combat but Java",
                        JOptionPane.QUESTION_MESSAGE
                );
                collectedValue = Integer.parseInt(input);
            } catch (Exception ignored) {
            }
        }
        return collectedValue;
    }

    private void useInspiration() {
        boolean isExcessInspiration = queue.getCurrentCombatant().useInspirationAndCheckExcess();
        updateTurnInformation();

        if (isExcessInspiration) {
            int excessInspirationRoll = promptValueFromRoll("Inspiration", 4);
            CombatMain.COMBAT_MENU.logInspiration(excessInspirationRoll);
        }
    }

    private void endTurn() {
        Combatant newCurrentCombatant = queue.endTurnAndGetNext();
        updateTurnInformation();

        if (!newCurrentCombatant.lifeStatus().isConscious()) {
            int deathSaveThrow = promptValueFromRoll("Death Save", 20);
            newCurrentCombatant.lifeStatus().rollDeathSave(deathSaveThrow);
        }

        CombatMain.COMBAT_MENU.update();
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