package exception;

import org.intellij.lang.annotations.MagicConstant;
import util.Message;

public class FileProcessErrorException extends ProgramRunError {
    /**
     * Indicates that an error has occurred while either reading from or writing
     * to a Better Combat .txt file. {@code FileProcessErrorException} also shows
     * a headed error popup in the program itself
     * @param message the message/reason for the exception
     * @param type the Message {@code String} constant stating whether the error is a
     *             read or a writer error ({@code Message.READ_ERROR} or
     *             {@code Message.WRITE_ERROR})
     */
    public FileProcessErrorException(String message, @MagicConstant(valuesFromClass = Message.class) String type) {
        super(message);
        Message.showFileErrorMessage(this, type);
    }
}