package exception;

import java.util.Arrays;
import java.util.List;

public class UnexpectedClassException extends ProgramRunError {
    /**
     * Indicated that a class outside the scope of the given source method has
     * been passed. {@code UnexpectedClassException} passes a message String to its
     * super stating the class and method where it was thrown, the class of the object
     * received, and the classes that are excepted in that context
     * @param object the object passed
     * @param expectedClasses the varargs of classes acceptable at the location of
     *                        the throw
     */
    public UnexpectedClassException(Object object, Class<?>... expectedClasses) {
        super(getMessage(object, expectedClasses));
    }

    private static String getMessage(Object object, Class<?>... expectedClasses) {
        List<String> expectedClassNames = Arrays.stream(expectedClasses).map(Class::getSimpleName).toList();
        String expectedClassesString = "valid in " + expectedClassNames + " expected";
        return ": invalid class " + object.getClass().getSimpleName() + "; " + expectedClassesString;
    }
}
