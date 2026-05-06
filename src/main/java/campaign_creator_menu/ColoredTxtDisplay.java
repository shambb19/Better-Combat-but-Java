package campaign_creator_menu;

import input.TextReader;
import lombok.experimental.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.text.AttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static input.TextReader.value;
import static swing.ColorStyles.*;

@ExtensionMethod(TextReader.class)
public class ColoredTxtDisplay extends JTextPane {

    private ArrayList<String> lines;

    public ColoredTxtDisplay(ArrayList<String> lines) {
        this.lines = lines;

        setFocusable(false);

        setBorder(new EmptyBorder(new Insets(10, 10, 10, 10)));
        setMargin(new Insets(5, 5, 5, 5));

        Optional.ofNullable(lines).ifPresent(l -> addLines());
    }

    public void setLines(List<String> lines) {
        this.lines = new ArrayList<>(lines);
        this.lines.replaceAll(TextReader::withoutComments);
        addLines();
    }

    public void addLines() {
        setText("");

        List<String> remaining = TextReader.extractConfigBlock(lines, block ->
                block.forEach(l -> appendToPane(l + "\n", l.startsWith("config") ? CONFIG : VALUE))
        );

        for (String line : remaining) {
            Color lineType = getLineType(line);

            if (lineType.equals(PARAMETER))
                appendParameter(line, "\n");
            else if (lineType.equals(STAT_PARAMETER))
                appendStatsParameter(line);
            else
                appendToPane(line + "\n", lineType);
        }
    }

    private void appendStatsParameter(String line) {
        appendToPane("stats", KEY);
        appendToPane(": ", EQUATOR);
        appendToPane("[", VALUE);

        String[] vals = line.listTextAsArray();
        for (int i = 0; i < vals.length; i++) {
            appendParameter(vals[i], "");

            if (i != vals.length - 1)
                appendToPane(", ", VALUE);
        }

        appendToPane("]", VALUE);
        appendToPane("\n", EMPTY);

    }

    private void appendToPane(String line, Color lineType) {
        StyleContext sc = StyleContext.getDefaultStyleContext();
        AttributeSet aSet = sc.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, lineType);

        aSet = sc.addAttribute(aSet, StyleConstants.Alignment, StyleConstants.ALIGN_JUSTIFIED);

        int len = getDocument().getLength();
        setCaretPosition(len);
        setCharacterAttributes(aSet, false);
        replaceSelection(line);
    }

    private void appendParameter(String line, String end) {
        String key = line.key();
        String value = value(line);

        appendToPane(key, KEY);
        appendToPane(": ", EQUATOR);
        appendToPane(value + end, VALUE);
    }

    private Color getLineType(String line) {
        if (line.isBlank()) return EMPTY;
        if (line.startsWith("stats")) return STAT_PARAMETER;
        if (line.startsWith("//") || line.startsWith("~") || line.startsWith("#")) return COMMENT;

        return switch (line.getHeader()) {
            case ".party" -> PARTY;
            case ".npc" -> FRIENDLY;
            case ".enemy" -> ENEMY;
            case ".scenario" -> SCENARIO;
            default -> PARAMETER;
        };
    }

}
