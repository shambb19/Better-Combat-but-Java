package exception;

import org.intellij.lang.annotations.MagicConstant;
import util.Message;

import java.io.IOException;

public class FileProcessErrorException extends IOException {
    public FileProcessErrorException(String message, @MagicConstant(valuesFromClass = Message.class) String type) {
        super(message);
        Message.showFileErrorMessage(this, type);
    }
}