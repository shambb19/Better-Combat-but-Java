package exception;

import input.Key;
import util.Message;

import java.util.logging.Logger;

public class InvalidParameterException extends InvalidSyntaxError {
    public InvalidParameterException(String sourceName, Key key, Object actualValue) {
        this(sourceName, key.name().toLowerCase(), actualValue, key.getRequirement());
    }

    public InvalidParameterException(String sourceName, String key, Object actualValue, String requirement) {
        super(
                false,
                String.format("%s=%s is invalid; %s expected", key, actualValue, requirement)
        );

        Logger.getLogger(sourceName).severe(getMessage());
        Message.showAsErrorMessage(getMessage());
    }
}