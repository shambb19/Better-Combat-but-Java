package combat_menu.popup;

import __main.EncounterInfo;
import __main.Main;
import character_info.combatant.PC;
import org.intellij.lang.annotations.MagicConstant;
import swing.swing_comp.SwingComp;
import swing.swing_comp.SwingPane;
import txt_input.CampaignWriter;
import util.Message;

import javax.swing.*;
import java.net.URL;

import static util.Message.confirmIf;
import static util.Message.template;

public class CombatEndPopup extends JDialog {

    private static final String victoryMessage = "Victory! You have won this combat.";
    private static final String victoryTitle = "Victory";

    private static final String lossMessage = "You have been defeated. You were " + EncounterInfo.getBattle().percentToVictory() + " of the way to victory.";
    private static final String lossTitle = "Defeat";

    public static void run(boolean isVictory) {
        Main.getMenu().dispose();
        new CombatEndPopup(isVictory).setVisible(true);
    }

    private CombatEndPopup(boolean isVictory) {
        setTitle(isVictory ? victoryTitle : lossTitle);
        setModal(false);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setIconImage(Main.getImage());

        SwingPane.modifiable(this).collect(
                isVictory ? victoryMessage : lossMessage,
                new JSeparator(),
                "Options:",
                partyLevelUpButton(), downloadUpdatedPartyTxtButton(), quitButton()
                )
                .withLayout(SwingPane.ONE_COLUMN)
                .withEmptyBorder(20);

        pack();
        setLocationRelativeTo(Main.getMenu());
    }

    public JButton partyLevelUpButton() {
        return SwingComp.button("Level Up the Party", null)
                .withAction(this::levelUp)
                .build();
    }

    public JButton downloadUpdatedPartyTxtButton() {
        return SwingComp.button("Download Updated .txt File", this::download).build();
    }

    public JButton quitButton() {
        return SwingComp.button("Quit Program", () -> quit("quit")).build();
    }

    private void levelUp(JButton button) {
        EncounterInfo.getParty().forEach(PC::levelUp);

        String message = "Level up successful! As of v4.2.4, only proficiency bonuses are handled internally. " +
                "All other changes (hp, stats, etc.) need to be manually entered in the Campaign Creator " +
                "for now. If you buy Braden a Red Bull he might fix that :P";
        template(message);

        button.setEnabled(false);
        button.setText("Party Level Increased");
    }

    private void download() {
        URL savedFile = new CampaignWriter().getUrl("Campaign Post Encounter", true);

        if (savedFile != null)
            template("Successfully saved to Downloads");
        else
            System.err.println("Failed to save the campaign file.");
    }

    public static void quit(@MagicConstant(stringValues = {"quit", "restart"}) String mode) {
        if (confirmIf(mode + " and lose all progress") == JOptionPane.OK_OPTION) {
            Message.template("Goodbye! Thanks for playing :)");

            if (mode.equals("quit"))
                System.exit(0);
            else
                Main.restart();
        }
    }

}