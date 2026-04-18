package combat_menu.popup;

import __main.Main;
import __main.manager.EncounterManager;
import combat_object.combatant.PC;
import input.CampaignWriter;
import org.intellij.lang.annotations.MagicConstant;
import org.jetbrains.annotations.NotNull;
import util.Message;

import javax.swing.*;
import javax.swing.border.MatteBorder;
import java.awt.*;
import java.net.URL;
import java.util.function.Consumer;

import static format.ColorStyles.*;
import static swing.swing_comp.SwingComp.fluent;
import static swing.swing_comp.SwingComp.*;
import static swing.swing_comp.SwingPane.*;
import static util.Message.confirmIf;
import static util.Message.template;

public class CombatEndPopup extends JDialog {

    public static final String VICTORY = "VICTORY", DEFEAT = "DEFEAT", QUIT = "ENDED EARLY";

    public static void run(@MagicConstant(valuesFromClass = CombatEndPopup.class) String endType) {
        Main.getCombatMenu().dispose();
        new CombatEndPopup(endType).setVisible(true);
    }

    private CombatEndPopup(@MagicConstant(valuesFromClass = CombatEndPopup.class) String endType) {
        getContentPane().setBackground(BACKGROUND);
        setLayout(new BorderLayout());
        getRootPane().setBorder(BorderFactory.createLineBorder(TRACK, 1));

        String title = "Quit";
        Color titleForeground = TEXT_PRIMARY;
        if (endType.equals(VICTORY)) {
            title = "Victory";
            titleForeground = HEALTHY;
        } else if (endType.equals(DEFEAT)) {
            title = "Defeat";
            titleForeground = CRITICAL;
        }
        setTitle(title);

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
                        label("OPTIONS", Font.BOLD, 10f, TEXT_MUTED).onLeft(),
                        createActionButton("Level Up the Party", SUCCESS, this::levelUp), spacer(0, 10),
                        createActionButton("Download Updated .txt File", TRACK, b -> download()), spacer(0, 10),
                        createActionButton("Quit Program", CRITICAL, b -> quit("quit"))
                )
                .withEmptyBorder(20, 20, 20, 20);

        setIconImage(__main.Main.getAppIcon().getImage());
        setModal(false);
        setAlwaysOnTop(true);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        pack();
        setVisible(true);
    }

    @NotNull
    private static JLabel getEndMessage(@MagicConstant(valuesFromClass = CombatEndPopup.class) String endType) {
        String percentToVictory = EncounterManager.getEncounter().percentToVictory();
        String msg = switch (endType) {
            case VICTORY -> "Victory! You have won this combat.";
            case DEFEAT -> "You have been defeated. You were " + percentToVictory + " of the way to victory.";
            case QUIT -> "You have quit early. Lame.";
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
        template(message);

        fluent(button).enabled(false)
                .withBackgroundAndForeground(TRACK, TEXT_MUTED)
                .applied(b -> b.setText("Party Level Increased"));
    }

    private void download() {
        URL savedFile = CampaignWriter.ofFullCampaign().getUrl("Campaign Post Encounter", true);

        if (savedFile != null)
            template("Successfully saved to Downloads");
        else
            Message.error("Could not download file");
    }

    public static void quit(@MagicConstant(stringValues = {"quit", "restart"}) String mode) {
        if (confirmIf(mode + " and lose all progress") == JOptionPane.OK_OPTION) {
            Message.template("Goodbye! Thanks for playing :)");

            if (mode.equals("quit"))
                System.exit(0);
            else
                Main.clearAllAndShowUploadMenu();
        }
    }
}