package combat_menu.popup;

import __main.EncounterInfo;
import __main.Main;
import character_info.combatant.PC;
import combat_menu.CombatMenu;
import format.SwingStyles;
import txt_input.CampaignWriter;

import javax.swing.*;
import java.awt.*;
import java.net.URL;

import static util.Message.confirmIf;
import static util.Message.template;

public class CombatEndPopup extends JDialog {

    private static final String victoryMessage = "Victory! You have won this combat.";
    private static final String victoryTitle = "Victory";

    private static final String lossMessage = "You have been defeated. You were " + EncounterInfo.getBattle().percentToVictory() + " of the way to victory.";
    private static final String lossTitle = "Defeat";

    public static void run(boolean isVictory) {
        new CombatEndPopup(isVictory).setVisible(true);
    }

    private CombatEndPopup(boolean isVictory) {
        setTitle(isVictory ? victoryTitle : lossTitle);
        setModal(false);
        setAlwaysOnTop(true);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new GridLayout(0, 1));
        setIconImage(Main.getImage());

        SwingStyles.addComponents(this,
                new JLabel(isVictory ? victoryMessage : lossMessage), new JSeparator(),
                new JLabel("Options:"),
                partyLevelUpButton(), downloadUpdatedPartyTxtButton(), quitButton()
        );

        pack();
        setLocationRelativeTo(Main.getMenu());
    }

    public JButton partyLevelUpButton() {
        JButton button = new JButton("Level Up the Party");
        button.putClientProperty("JButton.buttonType", "roundRect");
        button.addActionListener(e -> levelUp(button));

        return button;
    }

    public JButton downloadUpdatedPartyTxtButton() {
        JButton button = new JButton("Download Updated .txt File");
        button.putClientProperty("JButton.buttonType", "roundRect");
        button.addActionListener(e -> download());
        return button;
    }

    public JButton quitButton() {
        JButton button = new JButton("Quit Program");
        button.putClientProperty("JButton.buttonType", "roundRect");
        button.addActionListener(e -> quit());
        return button;
    }

    private void levelUp(JButton button) {
        EncounterInfo.getFriendlies().forEach(friendly -> {
            if (friendly instanceof PC pc) {
                pc.levelUp();
            }
        });
        String message = "As of v3.4.0, only proficiency bonuses are handled internally. " +
                "All other changes (hp, stats, etc.) need to be manually entered in the Campaign Creator " +
                "for now. If you buy Braden a Red Bull he might fix that :P";
        template(message);

        button.setEnabled(false);
        button.setText("Party Level Increased");
    }

    private void download() {
        URL savedFile = new CampaignWriter().getUrl("Campaign Post Encounter", true);

        if (savedFile != null) {
            template("Successfully saved to Downloads");
        } else {
            System.err.println("Failed to save the campaign file.");
        }
    }

    public void quit() {
        if (confirmIf("quit") == JOptionPane.OK_OPTION) {
            JOptionPane.showMessageDialog(
                    Main.getMenu(),
                    "Goodbye! Thanks for playing :)",
                    CombatMenu.TITLE,
                    JOptionPane.INFORMATION_MESSAGE
            );
            dispose();
            System.exit(0);
        }
    }

}