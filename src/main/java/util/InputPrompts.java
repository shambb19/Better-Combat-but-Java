package util;

import combat.Main;

import javax.swing.*;

public class InputPrompts {

    public static int promptHealth(boolean isDamage) {
        String message = (isDamage) ? "damage" : "health";
        int value = -1;
        while (value < 0) {
            String input = JOptionPane.showInputDialog(
                    Main.menu,
                    "Enter " + message + " amount.",
                    "Better Combat but Java",
                    JOptionPane.QUESTION_MESSAGE
            );
            try {
                value = Integer.parseInt(input);
            } catch (Exception ignored) {}
        }
        return value;
    }

}
