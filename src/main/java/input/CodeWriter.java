package input;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * CodeWriter is a fluent class to streamline the creation of .txt object code
 */
public class CodeWriter extends ArrayList<String> {

    /**
     * Instantiates a CodeWriter object with an optional header
     * @param header if present, the header line to be added to the
     *               start of the object's code. If header is null,
     *               no header line will be added
     */
    public CodeWriter(@Nullable String header) {
        Optional.ofNullable(header).ifPresent(this::add);
    }

    /**
     * Returns this CodeWriter object after adding a basic key value
     * code line derived from the provided key and value
     * @param key the Key value for the line to be added
     * @param value the value Object associated with the provided key.
     *              toString will be called to display the object
     * @return this CodeWriter object, with the new line being added
     */
    public CodeWriter withLine(String key, Object value) {
        return withLine(key + TextReader.KEY_DELIMITER + value);
    }

    /**
     * Returns this CodeWriter object after adding the given line verbatim
     * @param line the line of code to be added
     * @return this CodeWriter object, with the new line being added
     */
    public CodeWriter withLine(String line) {
        add(line);
        return this;
    }

    /**
     * Returns this CodeWriter object after potentially adding a basic
     * key value code line derived from the provided key and list. If the
     * list is not empty (size > 0), the line will be added to the
     * CodeWriter. If the list is empty, nothing is added
     * @param key the Key value for the line that might be added
     * @param list the value List associated with the provided key. If
     *             {@link List#isEmpty()}, no line will be added
     * @return this CodeWriter object, with the new line potentially added
     */
    public CodeWriter withLineFromNonEmptyList(String key, List<?> list) {
        if (list.isEmpty()) return this;
        return withLine(key, list);
    }

    /**
     * Returns this CodeWriter object after adding all lines in the given
     * list verbatim
     * @param lines the lines of code to be added
     * @return this CodeWriter object after all lines have been added
     */
    public CodeWriter withLines(ArrayList<String> lines) {
        addAll(lines);
        return this;
    }

}
