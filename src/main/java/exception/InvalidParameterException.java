package exception;

import lombok.*;
import lombok.experimental.*;

@Getter @FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class InvalidParameterException extends InvalidSyntaxError {

    String simpleReason;

    public InvalidParameterException(String sourceName, String key, Object actualValue, String requirement) {
        super(
                false,
                String.format("%s.%s=%s is invalid; %s expected", sourceName, key, actualValue, requirement)
        );

        simpleReason = String.format("%s=%s is invalid; %s expected", key, actualValue, requirement);
    }
}