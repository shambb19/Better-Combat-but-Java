package combat_menu;

import main.CombatMain;
import scenario_info.PlayerQueue;
import character_info.Combatant;
import combat_menu.popup.damage.DamagePromptPopup;
import combat_menu.popup.HealPromptPopup;

import javax.swing.*;
import java.awt.*;
import java.util.Map;
import java.util.Objects;

public class ActionPanel extends JPanel {

    private final PlayerQueue queue;

    private final JTextArea turnInformation = new JTextArea();
    private final JProgressBar currentCombatantHealthBar;
    private final JButton healButton;

    private enum icons {ATTACK, HEAL, INSPIRATION, END_TURN}
    Map<icons, String> buttonPics = Map.of(
            icons.ATTACK, "/attack-button.png",
            icons.HEAL, "/heal-button.png",
            icons.INSPIRATION, "/inspiration-button.png",
            icons.END_TURN, "/end-turn-button.png"
    );

    public ActionPanel() {
        queue = CombatMain.QUEUE;

        setLayout(new GridLayout(0, 1));

        updateTurnInformation();

        currentCombatantHealthBar = new JProgressBar();
        currentCombatantHealthBar.setStringPainted(true);
        currentCombatantHealthBar.setMinimum(0);
        copyHealthBar(queue.getCurrentCombatant().getHealthBar());

        JButton attackButton = new JButton();
        attackButton.addActionListener(e -> new DamagePromptPopup().setVisible(true));
        attackButton.setToolTipText("Attack");
        setIcon(attackButton, icons.ATTACK);

        healButton = new JButton();
        healButton.addActionListener(e -> new HealPromptPopup().setVisible(true));
        healButton.setToolTipText("Heal");
        setIcon(healButton, icons.HEAL);

        add(turnInformation);
        add(currentCombatantHealthBar);
        add(attackButton);
        add(healButton);
        add(getInspirationUsedButton());
        add(getEndTurnButton());
    }

    private JButton getEndTurnButton() {
        JButton endTurnButton = new JButton();
        setIcon(endTurnButton, icons.END_TURN);
        endTurnButton.setToolTipText("End Turn");

        endTurnButton.addActionListener(e -> {
            Combatant newCurrentCombatant = queue.endTurnAndGetNext();
            updateTurnInformation();

            if (!newCurrentCombatant.lifeStatus().isConscious()) {
                int deathSaveThrow = promptValueFromRoll("Death Save",20);
                newCurrentCombatant.lifeStatus().rollDeathSave(deathSaveThrow);
            }

            CombatMain.COMBAT_MENU.update();
        });

        return endTurnButton;
    }

    private JButton getInspirationUsedButton() {
        JButton inspirationButton = new JButton();
        setIcon(inspirationButton, icons.INSPIRATION);
        inspirationButton.setToolTipText("Use Inspiration");

        inspirationButton.addActionListener(e -> {
            boolean isExcessInspiration = queue.getCurrentCombatant().useInspirationAndCheckExcess();
            updateTurnInformation();

            if (isExcessInspiration) {
                int excessInspirationRoll = promptValueFromRoll("Inspiration",4);
                CombatMain.COMBAT_MENU.logInspiration(excessInspirationRoll);
            }
        });

        return inspirationButton;
    }

    public void updateTurnInformation() {
        turnInformation.setText(queue.getCurrentCombatant().actionList());
        if (queue.getCurrentCombatant().isEnemy()) {
            turnInformation.setForeground(new Color(122, 160, 245));
        } else {
            turnInformation.setForeground(Color.WHITE);
        }
        turnInformation.setEditable(false);
    }

    public void copyHealthBar(JProgressBar mimic) {
        currentCombatantHealthBar.setString(mimic.getString());
        currentCombatantHealthBar.setMaximum(mimic.getMaximum());
        currentCombatantHealthBar.setForeground(mimic.getForeground());
        if (queue.getCurrentCombatant().isEnemy()) {
            currentCombatantHealthBar.setValue(currentCombatantHealthBar.getMaximum());
        } else {
            currentCombatantHealthBar.setValue(mimic.getValue());
        }
    }

    public JButton getHealButton() {
        return healButton;
    }

    private void setIcon(JButton button, icons name) {
        Image image = new ImageIcon(Objects.requireNonNull(getClass().getResource(buttonPics.get(name)))).getImage();
        Image resized = image.getScaledInstance(80, 80, Image.SCALE_SMOOTH);

        button.setIcon(new ImageIcon(resized));
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
            } catch (Exception ignored) {}
        }
        return collectedValue;
    }
}