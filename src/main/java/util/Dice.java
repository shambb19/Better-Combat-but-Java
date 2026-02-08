package util;

import combat.Main;

import javax.swing.*;

public class Dice {

    public static int promptValueFromRoll(String rollMeaning, int numRolls, int dieSize) {
        int collectedValue = -1;
        while (collectedValue < 0 || collectedValue > dieSize) {
            try {
                String input = JOptionPane.showInputDialog(
                        Main.menu,
                        "Enter " + numRolls + "d" + dieSize + " roll for " + rollMeaning + ".",
                        "Better Combat but Java",
                        JOptionPane.QUESTION_MESSAGE
                );
                collectedValue = Integer.parseInt(input);
            } catch (Exception ignored) {}
        }
        return collectedValue;
    }

}
