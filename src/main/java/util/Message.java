package util;

import combat.Main;
import combatants.Combatant;

import javax.swing.*;

public class Message {

    public static int getDeathSaveRoll() {
        int roll = -1;
        boolean isFirstTry = true;
        while (roll < 0) {
            String text = "Roll Death Save for " + Main.queue.getCurrentCombatant().name() + ".";
            if (!isFirstTry) {
                text = "Please enter an integer. " + text;
            }
            String result = JOptionPane.showInputDialog(
                    Main.menu,
                    text,
                    Main.TITLE,
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
                Main.menu,
                "The attack does not hit.",
                Main.TITLE,
                JOptionPane.INFORMATION_MESSAGE
        );
        Main.queue.getCurrentCombatant().logMiss();
    }

    public static void informHexSuccess(Combatant target) {
        JOptionPane.showMessageDialog(
                Main.menu,
                target.name() + " has been successfully hexed. " +
                        "They will now receive 1d6 additional damage from " +
                        Main.queue.getCurrentCombatant().name() + ".",
                Main.TITLE,
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    public static void informIllusion(Combatant target) {
        JOptionPane.showMessageDialog(
                Main.menu,
                target.name() + " is now under the illusion of your choice. " +
                        "Remember that or something, idk.",
                Main.TITLE,
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    public static int confirmIf(String reason) {
        return JOptionPane.showConfirmDialog(
                Main.menu,
                "Are you sure you would like to " + reason + "?",
                Main.TITLE,
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );
    }

    public static void bye() {
        JOptionPane.showMessageDialog(
                Main.menu,
                "Goodbye! Thanks for playing!",
                Main.TITLE,
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    public static void fileError() {
        JOptionPane.showMessageDialog(
                Main.menu,
                "Could not retrieve file.",
                Main.TITLE,
                JOptionPane.ERROR_MESSAGE
        );
    }

    public static void template(String text) {
        JOptionPane.showMessageDialog(
                Main.menu,
                text,
                Main.TITLE,
                JOptionPane.INFORMATION_MESSAGE
        );
    }

}
