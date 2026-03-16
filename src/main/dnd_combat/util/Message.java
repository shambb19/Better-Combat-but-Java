package util;

import __main.CombatMain;
import campaign_creator_menu.TxtMenu;
import character_info.combatant.Combatant;
import combat_menu.CombatMenu;

import javax.swing.*;
import java.io.IOException;

public class Message {

    public static int getDeathSaveRoll() {
        int roll = -1;
        boolean isFirstTry = true;
        while (roll < 0) {
            String text = "Roll Death Save for " + CombatMain.QUEUE.getCurrentCombatant().name() + ".";
            if (!isFirstTry) {
                text = "Please enter an integer. " + text;
            }
            String result = JOptionPane.showInputDialog(
                    CombatMain.COMBAT_MENU,
                    text,
                    CombatMenu.TITLE,
                    JOptionPane.QUESTION_MESSAGE
            );
            try {
                roll = Integer.parseInt(result);
            } catch (Exception ignored) {}
        }
        return roll;
    }

    public static void informAttackFail() {
        JOptionPane.showMessageDialog(
                CombatMain.COMBAT_MENU,
                "The attack does not hit.",
                CombatMenu.TITLE,
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    public static void informHexSuccess(Combatant target) {
        JOptionPane.showMessageDialog(
                CombatMain.COMBAT_MENU,
                target.name() + " has been successfully hexed. " +
                        "They will now receive 1d6 additional damage from " +
                        CombatMain.QUEUE.getCurrentCombatant().name() + ".",
                CombatMenu.TITLE,
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    public static void informIllusion(Combatant target) {
        JOptionPane.showMessageDialog(
                CombatMain.COMBAT_MENU,
                target.name() + " is now under the illusion of your choice. " +
                        "Remember that or something, idk.",
                CombatMenu.TITLE,
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    public static int confirmIf(String reason) {
        return JOptionPane.showConfirmDialog(
                CombatMain.COMBAT_MENU,
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
        }
        return value;
    }
    public static int editOrRemoveOption(String name) {
        int result = JOptionPane.showOptionDialog(
                null,
                "What would you like to do with " + name + "?",
                TxtMenu.TITLE,
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                new String[] {"Edit", "Remove", "Cancel"},
                null
        );
        if (result == 1) {
            int confirm = question("Are you sure you would like to remove " + name + "?");
            if (confirm != JOptionPane.YES_OPTION) {
                return 2;
            }
        }
        return result;
    }

    public static void fileError(Exception error) {
        JOptionPane.showMessageDialog(
                null,
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
                TxtMenu.TITLE,
                JOptionPane.YES_NO_OPTION
        );
    }

}
