package exception;

public class IllegalArgumentAtRootException extends RuntimeException {
    public IllegalArgumentAtRootException(String message) {
        super(
                Thread.currentThread().getStackTrace()[2].getClassName() + "." +
                        Thread.currentThread().getStackTrace()[2].getMethodName() + ": " +
                        message
        );
    }
}
