package util;

import __main.EncounterInfo;
import __main.Main;
import campaign_creator_menu.CampaignCreatorMenu;
import combat_menu.CombatMenu;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class Message {

    public static int confirmIf(String reason) {
        return JOptionPane.showConfirmDialog(
                Main.getMenu(),
                "Are you sure you would like to " + reason + "?",
                CombatMenu.TITLE,
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );
    }

    public static int getWithLoopUntilInt(String message, String title) {
        int value = -1;
        while (value < 0) {
            String result = JOptionPane.showInputDialog(
                null,
                    message,
                    title,
                    JOptionPane.QUESTION_MESSAGE
            );
            try {
                value = Integer.parseInt(result);
            } catch (Exception ignored) {}

            message += " Try an integer this time.";
        }
        return value;
    }

    public static int getDeathSaveRoll() {
        return getWithLoopUntilInt(
                "Roll Death Save for " + EncounterInfo.getCurrentCombatant().name() + ".",
                CombatMenu.TITLE
        );
    }

    public static int editOrRemoveOption(String name) {
        final int REMOVE_OPTION = 1;

        int result = JOptionPane.showOptionDialog(
                null,
                "What would you like to do with " + name + "?",
                CampaignCreatorMenu.TITLE,
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                new String[] {"Edit", "Remove", "Cancel"},
                null
        );
        if (result == REMOVE_OPTION) {
            int confirm = question("Are you sure you would like to remove " + name + "?");
            if (confirm != JOptionPane.YES_OPTION)
                return 2;
        }
        return result;
    }

    public static void fileError(Component parent, Exception error) {
        JOptionPane.showMessageDialog(
                parent,
                "There was an error reading this file: " + error.getMessage(),
                CombatMenu.TITLE,
                JOptionPane.ERROR_MESSAGE
        );
    }

    public static void throwFileDownloadError(JPanel root, IOException error) {
        JOptionPane.showMessageDialog(
                root,
                "There was an error downloading the file: " + error.getMessage(),
                "File Download Error",
                JOptionPane.ERROR_MESSAGE
        );
    }

    public static void template(String text) {
        JOptionPane.showMessageDialog(
                null,
                text,
                CombatMenu.TITLE,
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    public static int question(String text) {
        return JOptionPane.showConfirmDialog(
                null,
                text,
                CampaignCreatorMenu.TITLE,
                JOptionPane.YES_NO_OPTION
        );
    }

}
