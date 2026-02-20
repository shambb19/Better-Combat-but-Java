package gui.popup;

import combat.Main;
import combatants.Combatant;
import combatants.Highlights;
import txt_input.PartyWriter;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

import static util.Message.*;

public class CombatEndPopup extends JFrame {

    private static final String victoryMessage = "Victory! You have won this combat.";
    private static final String victoryTitle = "Victory";

    private static final String lossMessage = "You have been defeated. You were " + Main.battle.percentToVictory() + " of the way to victory.";
    private static final String lossTitle = "Defeat";

    public CombatEndPopup(boolean isVictory) {
        setTitle(isVictory ? victoryTitle : lossTitle);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new GridLayout(0, 1));

        add(new JLabel(isVictory ? victoryMessage : lossMessage));
        add(new JSeparator());
        add(new JLabel("Options:"));
        add(seeCombatHighlightsButton());
        add(partyLevelUpButton());
        add(downloadUpdatedPartyTxtButton());
        add(exitButton());

        pack();
        setLocationRelativeTo(Main.menu);
    }

    public JButton seeCombatHighlightsButton() {
        JButton button = new JButton("See Highlights");
        button.putClientProperty("JButton.buttonType", "roundRect");
        button.addActionListener(e -> template(Highlights.get()));
        return button;
    }

    public JButton partyLevelUpButton() {
        JButton button = new JButton("Level Up the Party");
        button.putClientProperty("JButton.buttonType", "roundRect");
        button.addActionListener(e -> {
            for (Combatant combatant : Main.battle.friendlies()) {
                if (!combatant.isNPC()) {
                    combatant.levelUp();
                }
            }
            button.setEnabled(false);
            button.setText("Party Level Increased");
        });
        return button;
    }

    public JButton downloadUpdatedPartyTxtButton() {
        JButton button = new JButton("Download Updated Party .txt File");
        button.putClientProperty("JButton.buttonType", "roundRect");
        button.addActionListener(e -> {
            try {
                setVisible(false);
                JFileChooser fileChooser = new JFileChooser(new PartyWriter().getFile());
                int result = fileChooser.showSaveDialog(Main.menu);
                if (result == JFileChooser.APPROVE_OPTION) {
                    button.setText("Re-download .txt File");
                    setVisible(true);
                }
            } catch (IOException ignored) {
                fileError();
                button.setEnabled(false);
            }
        });
        return button;
    }

    public JButton exitButton() {
        JButton button = new JButton("Quit Program");
        button.putClientProperty("JButton.buttonType", "roundRect");
        button.addActionListener(e -> {
            if (confirmIf("quit") == JOptionPane.OK_OPTION) {
                bye();
                dispose();
                System.exit(0);
            }
        });
        return button;
    }

}
