package util;

import _manager.EncounterManager;
import lombok.experimental.*;
import org.intellij.lang.annotations.MagicConstant;
import swing.custom.PopupPrompt;

import java.util.Optional;

import static swing.ColorStyles.*;

@ExtensionMethod(StringUtil.class)
public class Message {

    public static final String READ_ERROR = "reading", WRITE_ERROR = "writing";

    public static void showAsInfoMessage(String text) {
        PopupPrompt.showAlert("Notice", text, SUCCESS);
    }

    public static void showActionPrompt(String text, PopupPrompt.ActionButton... buttons) {
        PopupPrompt.showActions("Selection Required", text, buttons);
    }

    public static String promptString(String message, String placeholder) {
        return PopupPrompt.showAndGetInput("Input Required", message, placeholder, true);
    }

    public static int promptRoll(String reason, Roll roll, int target, Runnable onSuccess, String failMessage) {
        String reasonFormatted = reason;
        if (reason.contains("..name..")) {
            if (EncounterManager.getCurrentCombatant() == null)
                throw new RuntimeException("Message.promptRoll: cannot specify combatant before Encounter initialization");
            reasonFormatted = reason.replace("..name..", EncounterManager.getCurrentCombatant().toString());
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

    public static void showFileErrorMessage(
            Exception error, @MagicConstant(valuesFromClass = Message.class) String errorType
    ) {
        PopupPrompt.showAlert("File Error", "Error " + errorType + " file : " + error.getMessage(), CRITICAL);
    }

}