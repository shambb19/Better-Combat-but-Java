package swing.popup;

import __main.Main;
import __main.UploadMain;
import _manager.EncounterManager;
import combat_object.combatant.PC;
import input.CampaignWriter;
import org.intellij.lang.annotations.MagicConstant;
import org.jetbrains.annotations.NotNull;
import swing.custom.Popup;
import swing.builder.ButtonBuilder;
import swing.builder.ComponentBuilder;
import util.Message;

import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;

import static swing.ColorStyles.*;
import static swing.builder.Components.*;
import static swing.builder.PanelBuilder.*;

@lombok.experimental.ExtensionMethod({util.StringUtil.class, util.Message.class})
public class CombatEndPopup extends Popup {

    public static final String
            VICTORY_END = "Victory",
            DEFEAT_END = "Defeat",
            QUIT_END = "Quit";

    private CombatEndPopup(@MagicConstant(valuesFromClass = CombatEndPopup.class) String endType) {
        getContentPane().setBackground(BACKGROUND);
        setLayout(new BorderLayout());
        getRootPane().setBorder(BorderFactory.createLineBorder(TRACK, 1));
        setAlwaysOnTop(false);

        Color titleForeground = switch (endType) {
            case VICTORY_END -> HEALTHY;
            case DEFEAT_END -> CRITICAL;
            default -> FOREGROUND;
        };

        setTitle(endType);

        // top bar with the title text (victory or defeat or whatever)
        createPanel(FLOW_LEFT,
                ComponentBuilder.createLabel(endType, Font.BOLD, 16f, titleForeground)
        ).background(BG_DARK)
                .border(BorderFactory.createMatteBorder(0, 0, 1, 0, TRACK))
                .build().putIn(this, BorderLayout.NORTH);

        // longer end description and the three action buttons
        JLabel optionsLabel = ComponentBuilder.createLabel("OPTIONS", Font.BOLD, 10f, FG_MUTED)
                .alignmentX(LEFT_ALIGNMENT).build().get();

        createPanel(VERTICAL_BOX,
                getEndMessage(endType), spacer(0, 24),
                optionsLabel,
                createActionButton("Level Up the Party", SUCCESS, this::levelUp), spacer(0, 10),
                createActionButton("Download Updated .txt File", TRACK, b -> download()), spacer(0, 10),
                createActionButton("Quit Program", CRITICAL, b -> quit())
        ).border(SPACER_BORDER)
                .build().putIn(this, BorderLayout.CENTER);

        pack();

        Main.closeCombat();
        setVisible(true);
    }

    @NotNull
    private static JLabel getEndMessage(String endType) {
        String percentToVictory = EncounterManager.getEncounter().percentToVictory();
        String msg = switch (endType) {
            case VICTORY_END -> "Victory! You have won this combat.";
            case DEFEAT_END -> "You have been defeated. You were " + percentToVictory + " of the way to victory.";
            case QUIT_END -> "You have quit early. Lame.";
            default -> null;
        };

        return ComponentBuilder.createLabel("<html><p style='width: 250px;'>" + msg + "</p></html>",
                        Font.PLAIN, 14f, FOREGROUND)
                .alignmentX(LEFT_ALIGNMENT).build().get();
    }

    private JButton createActionButton(String text, Color bg, Consumer<JButton> onClick) {
        return ButtonBuilder.createButton(text, bg, onClick)
                .fontBuilder(FontBuilder.of(Font.PLAIN, 13f))
                .alignmentX(LEFT_ALIGNMENT)
                .fixedHeight(40).build().get();
    }

    private void levelUp(JButton button) {
        EncounterManager.party().forEach(PC::levelUp);

        final String message = "Level up successful! As of " + Main.VERSION + ", only proficiency bonuses " +
                "and hp are handled internally. All other changes (stats, etc.) need to be manually entered " +
                "in the Campaign Creator for now. If you buy Braden a Red Bull he might fix that :P";
        message.showAsInfoMessage();

        ButtonBuilder.modifyingButton(button)
                .enabled(false)
                .background(TRACK).foreground(FG_MUTED)
                .text("Party Level Increased").build().validated();
    }

    private void download() {
        CampaignWriter.ofFullCampaign().download("Campaign Post Encounter", true);
    }

    public static void quit() {
        promptAction("quit", () -> {
            "Goodbye! Thanks for playing :)".showAsInfoMessage();
            System.exit(0);
        });
    }

    public static void fireCombatEndedNaturally() {
        boolean isVictory = EncounterManager.getEncounter().isVictory();
        String endType = isVictory ? VICTORY_END : DEFEAT_END;

        new CombatEndPopup(endType);
    }

    public static void fireQuit() {
        promptAction("quit", () -> new CombatEndPopup(QUIT_END));
    }

    public static void fireRestart() {
        promptAction("restart", () -> Main.closeAndSwitch(null, new UploadMain()));
    }

    private static void promptAction(String mode, Runnable runnable) {
        Message.showActionPrompt("Are you sure you would like to " + mode + "? You will lose all progress.",
                new PopupPrompt.ActionButton(mode.capitalized(), CRITICAL, runnable),
                new PopupPrompt.ActionButton("Continue", SUCCESS, null));
    }
}