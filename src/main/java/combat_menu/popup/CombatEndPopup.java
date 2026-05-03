package combat_menu.popup;

import __main.Main;
import combat_object.combatant.PC;
import input.CampaignWriter;
import manager.EncounterManager;
import org.jetbrains.annotations.NotNull;
import util.Message;
import util.PopupPrompt;

import javax.swing.*;
import javax.swing.border.MatteBorder;
import java.awt.*;
import java.net.URL;
import java.util.function.Consumer;

import static format.ColorStyles.*;
import static format.swing_comp.SwingComp.fluent;
import static format.swing_comp.SwingComp.*;
import static format.swing_comp.SwingPane.*;

@lombok.experimental.ExtensionMethod({util.StringUtil.class, util.Message.class})
public class CombatEndPopup extends Popup {

    public static void fireCombatEndedNaturally() {
        boolean isVictory = EncounterManager.getEncounter().isVictory();
        String endType = isVictory ? "Victory" : "Defeat";

        new CombatEndPopup(endType);
    }

    public static void fireQuit() {
        new CombatEndPopup("Quit");
    }

    private CombatEndPopup(String endType) {
        getContentPane().setBackground(BACKGROUND);
        setLayout(new BorderLayout());
        getRootPane().setBorder(BorderFactory.createLineBorder(TRACK, 1));

        Color titleForeground = FOREGROUND;
        if (endType.equals("VICTORY")) {
            titleForeground = HEALTHY;
        } else if (endType.equals("DEFEAT")) {
            titleForeground = CRITICAL;
        }
        setTitle(endType);

        // top bar with the title text (victory or defeat or whatever)
        panelIn(this, BorderLayout.NORTH).arrangedAs(FLOW_LEFT, 15, 12)
                .collect(
                        label(endType, Font.BOLD, 16f, titleForeground)
                )
                .withBackground(BG_DARK)
                .withBorder(new MatteBorder(0, 0, 1, 0, TRACK));

        // longer end description and the three action buttons
        panelIn(this, BorderLayout.CENTER).arrangedAs(VERTICAL_BOX)
                .collect(
                        getEndMessage(endType), spacer(0, 24),
                        label("OPTIONS", Font.BOLD, 10f, FG_MUTED).onLeft(),
                        createActionButton("Level Up the Party", SUCCESS, this::levelUp), spacer(0, 10),
                        createActionButton("Download Updated .txt File", TRACK, b -> download()), spacer(0, 10),
                        createActionButton("Quit Program", CRITICAL, b -> quit())
                ).spaced();

        pack();

        Main.closeCombat();
        setVisible(true);
    }

    @NotNull
    private static JLabel getEndMessage(String endType) {
        String percentToVictory = EncounterManager.getEncounter().percentToVictory();
        String msg = switch (endType) {
            case "Victory" -> "Victory! You have won this combat.";
            case "Defeat" -> "You have been defeated. You were " + percentToVictory + " of the way to victory.";
            case "Quit" -> "You have quit early. Lame.";
            default -> throw new ClassCastException("CombatEndPopup.getEndMessage: unexpected String endType");
        };

        return label("<html><p style='width: 250px;'>" + msg + "</p></html>")
                .withDerivedFont(Font.PLAIN, 14f)
                .onLeft().component();
    }

    private JButton createActionButton(String text, Color bg, Consumer<JButton> onClick) {
        return button(text, bg, null).withDerivedFont(Font.PLAIN, 13f)
                .withAction(onClick)
                .withBackground(bg)
                .onLeft()
                .withMaximumSize(Integer.MAX_VALUE, 40).component();
    }

    private void levelUp(JButton button) {
        EncounterManager.getParty().forEach(PC::levelUp);

        final String message = "Level up successful! As of " + Main.VERSION + ", only proficiency bonuses " +
                "and hp are handled internally. All other changes (stats, etc.) need to be manually entered " +
                "in the Campaign Creator for now. If you buy Braden a Red Bull he might fix that :P";
        message.showAsInfoMessage();

        fluent(button).enabled(false)
                .withBackgroundAndForeground(TRACK, FG_MUTED)
                .applied(b -> b.setText("Party Level Increased"));
    }

    private void download() {
        URL savedFile = CampaignWriter.ofFullCampaign().getUrl("Campaign Post Encounter", true);

        if (savedFile != null)
            "Successfully saved to Downloads".showAsInfoMessage();
        else
            "Could not download file".showAsErrorMessage();
    }

    public static void quit() {
        promptAction("quit", () -> {
            "Goodbye! Thanks for playing :)".showAsInfoMessage();
            System.exit(0);
        });
    }

    public static void restart() {
        promptAction("restart", () -> Main.closeAndSwitch(null, Main.UPLOAD));
    }

    public static void promptAction(String mode, Runnable runnable) {
        Message.showActionPrompt("Are you sure you would like to " + mode + "? You will lose all progress.",
                new PopupPrompt.ActionButton[]{
                        new PopupPrompt.ActionButton(mode.capitalized(), CRITICAL, runnable),
                        new PopupPrompt.ActionButton("Continue", SUCCESS, null)
                });
    }
}