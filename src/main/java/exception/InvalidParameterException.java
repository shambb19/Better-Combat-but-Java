package exception;

import lombok.*;
import lombok.experimental.*;
import util.StringUtil;

@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class InvalidParameterException extends InvalidSyntaxError {

    /**
     * Indicates that a parameter in a Better Combat .txt source file is of an
     * invalid syntax for the parameter's requirements.
     * {@code InvalidParameterException} stores the message in the thrown exception
     * as String simpleReason which can be referenced after the exception is thrown
     * @param sourceName the class containing the key that received an invalid
     *                   parameter
     * @param key the name of the key that received an invalid parameter
     * @param actualValue the invalid parameter value that was passed
     * @param requirement the type of input required for the given key
     */
    public InvalidParameterException(String sourceName, String key, Object actualValue, String requirement) {
        super(false, getMessage(sourceName, key, actualValue, requirement));
    }

    private static String getMessage(String sourceName, String key, Object actualValue, String requirement) {
        String sourceString = StringUtil.getSource(true) + ": ";
        return sourceString + String.format("%s.%s=%s is invalid; %s expected", sourceName, key, actualValue, requirement);
    }
}