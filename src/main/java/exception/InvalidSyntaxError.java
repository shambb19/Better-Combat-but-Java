package exception;

import util.Message;

import java.util.logging.Logger;

public class InvalidSyntaxError extends IllegalArgumentException {
    public InvalidSyntaxError(boolean referenceCaller, String reason) {
        super(
                Thread.currentThread().getStackTrace()[referenceCaller ? 3 : 2].getClassName() + "."
                        + Thread.currentThread().getStackTrace()[referenceCaller ? 3 : 2].getMethodName() + ": "
                        + reason
        );
        Logger.getAnonymousLogger().severe(getMessage());
        Message.showAsErrorMessage(getMessage());
    }
}