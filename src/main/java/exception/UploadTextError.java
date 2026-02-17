package exception;

import combat.Main;

import javax.swing.*;

public class UploadTextError extends RuntimeException {

    public enum cause {STATS};

    private static final String statHelpMessage = "Remember that stats should be in the following format: " +
            "str(x)/dex(x+)/con(x)/int(x)/wis(x)/cha(x+) where x represents the stat value and + " +
            "represents proficiency in that stat field.";

    public UploadTextError(cause cause) {
        String helpMessage = switch (cause) {
            case STATS -> statHelpMessage;
        };
        String errorLocation = switch (cause) {
            case STATS -> "stat block";
        };

        JOptionPane.showMessageDialog(
                Main.menu,
                "One of your " + errorLocation + " entries was entered incorrectly. " +
                        "Please edit and try again." + helpMessage,
                Main.TITLE,
                JOptionPane.ERROR_MESSAGE
        );
        System.exit(0);
    }
}
