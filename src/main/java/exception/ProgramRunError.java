package exception;

import util.StringUtil;

public class ProgramRunError extends RuntimeException {
    /**
     * Indicates that something has occurred that prevents combat from initializing or running properly
     * @param reason the reason the exception was thrown
     */
    public ProgramRunError(String reason) {
        super(StringUtil.getSource(false) + ": " + reason);
    }
}
