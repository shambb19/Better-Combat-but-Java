package util;

import _manager.EncounterManager;
import exception.ProgramRunError;
import lombok.experimental.*;
import org.intellij.lang.annotations.MagicConstant;
import swing.popup.PopupPrompt;

import java.util.Optional;

import static swing.ColorStyles.*;

@ExtensionMethod(StringUtil.class)
public class Message {

    public static final String READ_ERROR = "reading", WRITE_ERROR = "writing";

    /**
     * Displays a message popup displaying the given text in a standard information format
     * @param text the text to be displayed
     */
    public static void showAsInfoMessage(String text) {
        PopupPrompt.showAlert("Notice", text, SUCCESS);
    }

    /**
     * Displays a popup prompting the selection of one of the provided buttons
     * @param text the prompt text to be displayed
     * @param buttons the buttons between which to choose, each of which is linked to a
     *                subsequent action
     */
    public static void showActionPrompt(String text, PopupPrompt.ActionButton... buttons) {
        PopupPrompt.showActions("Selection Required", text, buttons);
    }

    /**
     * Displays a popup prompting the entering of a String which is then returned
     * @param message the prompt text to be displayed
     * @param placeholder the placeholder text of the input field
     * @return the entered String
     */
    public static String promptString(String message, String placeholder) {
        return PopupPrompt.showAndGetInput("Input Required", message, placeholder, true);
    }

    /**
     * Displays a popup prompting the entering of a die roll which is then
     * returned as an int
     * @param reason the reason the given roll is required
     * @param roll the type of roll (i.e. 1d20, 3d6)
     * @param target the value needed for the roll to succeed
     * @param onSuccess the action taken if the target is met
     * @param failMessage the text to be displayed in a
     *                    Message.showAsInfoMessage popup if the target
     *                    is not met
     * @return the value of the roll
     */
    public static int promptRoll(String reason, Roll roll, int target, Runnable onSuccess, String failMessage) {
        String reasonFormatted = reason;
        if (reason.contains("..name..")) {
            if (EncounterManager.currentCombatant() == null)
                throw new ProgramRunError("current combatant cannot exist before encounter init");
            reasonFormatted = reason.replace("..name..", EncounterManager.currentCombatant().toString());
        }

        String message = "Roll " + roll.toString() + " " + reasonFormatted + ".";
        int result;
        while (true) {
            result = PopupPrompt.showAndGetInput("Roll Required", message, roll.toString(), false).toInt();
            if (result > Integer.MIN_VALUE) break;
            message = "Invalid input. Please enter a whole number. " + message;
        }

        if (result >= target) Optional.of(onSuccess).ifPresent(Runnable::run);
        else Optional.of(failMessage).ifPresent(Message::showAsInfoMessage);

        return result;
    }

    /**
     * Displays an error popup describing the given file error
     * @param error the thrown error (caused by the reading or writing of source files)
     * @param errorType the String constant indicating the type of error
     *                  (Message.READ_ERROR or Message.WRITE_ERROR)
     */
    public static void showFileErrorMessage(
            Exception error, @MagicConstant(valuesFromClass = Message.class) String errorType
    ) {
        PopupPrompt.showAlert("File Error", "Error " + errorType + " file : " + error.getMessage(), CRITICAL);
    }

}