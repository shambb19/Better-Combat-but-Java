package exception;

import util.StringUtil;

public class InvalidSyntaxError extends RuntimeException {
    /**
     * Indicates that invalid syntax has been read in a Better Combat source
     * file that prevents the file from being fully decoded. Message displays
     * the class and method that called this exception (or the method that called
     * that if referenceCaller=true) and the reason
     * @param referenceCaller if true, stack trace message shows the source String
     *                        of the method that threw this exception. Otherwise,
     *                        shows the String of the method that called that method
     * @param reason the cause of the exception
     */
    public InvalidSyntaxError(boolean referenceCaller, String reason) {
        super(StringUtil.getSource(referenceCaller) + ": " + reason);
    }
}