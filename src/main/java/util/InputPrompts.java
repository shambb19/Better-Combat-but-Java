package util;

import combat.Main;

import javax.swing.*;

public class InputPrompts {

    public static int promptHealth(String message) {
        int value = -1;
        while (value < 0) {
            String input = JOptionPane.showInputDialog(
                    Main.menu,
                    "Enter " + message + ".",
                    Main.TITLE,
                    JOptionPane.QUESTION_MESSAGE
            );
            try {
                value = Integer.parseInt(input);
            } catch (Exception ignored) {}
        }
        return value;
    }

    public static void informAttackFail() {
        JOptionPane.showMessageDialog(
                Main.menu,
                "The attack does not hit.",
                Main.TITLE,
                JOptionPane.INFORMATION_MESSAGE
        );
    }

}
