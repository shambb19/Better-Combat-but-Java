package ide;

import __main.Main;
import config.ruleset.Ruleset;
import exception.InvalidSyntaxError;
import input.syntax.Key;
import input.syntax.Tag;
import lombok.experimental.*;
import org.fife.ui.rsyntaxtextarea.RSyntaxDocument;
import org.fife.ui.rsyntaxtextarea.Token;
import org.fife.ui.rsyntaxtextarea.parser.AbstractParser;
import org.fife.ui.rsyntaxtextarea.parser.DefaultParseResult;
import org.fife.ui.rsyntaxtextarea.parser.DefaultParserNotice;
import org.fife.ui.rsyntaxtextarea.parser.ParseResult;
import util.Locators;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static config.Config.*;
import static ide.CampaignToken.*;

@ExtensionMethod({input.TextReader.class, util.Locators.class})
public class CampaignParser extends AbstractParser {

    private final DefaultParseResult parseResult;

    {
        parseResult = new DefaultParseResult(this);
    }

    @Override public ParseResult parse(RSyntaxDocument document, String s) {
        parseResult.clearNotices();
        int numLines = document.getDefaultRootElement().getElementCount();
        parseResult.setParsedLines(0, numLines - 1);

        ArrayList<String> currentItem = new ArrayList<>();
        boolean currentItemExists = false;
        int currentItemStartIndex = 0;

        for (int i = 0; i < numLines; i++) {
            Token token = document.getTokenListForLine(i);
            if (token == null || !token.isPaintable()) continue;

            String line = getFullLine(token);
            int type = token.getType();

            switch (type) {
                case HEADER:
                    validateLastItem(currentItem, currentItemStartIndex);
                    validateHeader(line, i);

                    currentItemStartIndex = i;
                    currentItemExists = true;
                    currentItem = new ArrayList<>();
                    currentItem.add(line);
                    break;
                case CONFIG:
                    validateConfigActionLine(line, i);
                    switch (line) {
                        case CONFIG_OPEN_TOKEN:
                            currentItemStartIndex = i;
                            currentItemExists = true;
                            currentItem = new ArrayList<>();
                            currentItem.add(line);
                            break;
                        case CONFIG_CLOSE_TOKEN:
                            validateConfig(currentItem, currentItemStartIndex);
                            currentItemExists = false;
                            currentItem = new ArrayList<>();
                            break;
                        default:
                            currentItem.add(line);
                    }
                    break;
                case KEY, KEY_OPTIONAL, VALUE:
                    if (currentItemExists) currentItem.add(line);
                    break;
                case UNEXPECTED:
                    addNoticeAt(i, "cannot resolve line");
            }
        }

        validateLastItem(currentItem, currentItemStartIndex);

        return parseResult;
    }

    private void validateHeader(String line, int index) {
        try { // header validation
            String header = line.getHeader();
            if (ItemType.isInvalidHeader(header)) {
                throw new InvalidSyntaxError(false, null);
            }

            ItemType type = Locators.enumNameSearch(header.substring(1), ItemType.class);
            if (type.isUnavailableInActiveRuleset()) addNoticeAt(index, "type unavailable in current ruleset");
        } catch (InvalidSyntaxError ignored) {
            addNoticeAt(index, "invalid item header");
        }

        try { // tag validation
            Set<Tag> tags = line.getTags();

            if (Main.getRuleset().equals(Ruleset.STANDARD_RULESET)) {
                boolean invalidTags = tags.stream().anyMatch(t -> !t.equals(Tag.INCOMPLETE));
                if (invalidTags) addNoticeAt(index, "tag unavailable in standard ruleset");
            }
        } catch (InvalidSyntaxError ignored) {
            addNoticeAt(index, "invalid tag(s)");
        }
        try { // just need the name to be there
            String name = line.getName();
            if (name.isBlank()) {
                addNoticeAt(index, "missing name");
            }
        } catch (InvalidSyntaxError ignored) {
            addNoticeAt(index, "invalid name");
        }
    }

    private void validateConfigActionLine(String line, int index) {
        String action = line.withoutComments();

        if (!action.equals(CONFIG_OPEN_TOKEN) && !action.equals(CONFIG_CLOSE_TOKEN)) {
            addNoticeAt(index, "invalid config action; " + CONFIG_OPEN_TOKEN + " or " + CONFIG_CLOSE_TOKEN + " expected");
        }
    }

    private void validateItem(ArrayList<String> lines, int headerLine) {
        String header = lines.getFirst().getHeader();

        if (ItemType.isInvalidHeader(header)) {
            addNoticeAt(headerLine, "unexpected item type");
            return;
        }

        List<KeyEntry> loggedKeys = new ArrayList<>();

        for (int i = 1; i < lines.size(); i++) {
            String line = lines.get(i);
            int index = headerLine + i;

            Key key;
            try {
                String keyString = line.key();
                key = keyString.enumNameSearch(Key.class);
                loggedKeys.add(new KeyEntry(key, index));
            } catch (IllegalArgumentException ignored) {
                addNoticeAt(index, "invalid key");
                continue;
            }

            try {
                Object value = Key.getAppropriateValueFromLine(line);
                if (key.isValueInvalid(value)) {
                    addNoticeAt(index, "invalid value; " + key.getRequirement() + " expected");
                }
            } catch (IllegalArgumentException ignored) {
                addNoticeAt(index, "invalid value");
            }
        }

        validateLoggedParameters(header, loggedKeys, headerLine);
    }

    private void validateLoggedParameters(String header, List<KeyEntry> loggedKeys, int headerLine) {
        interface DisplayableKeyGetter {
            String get(Key key);
        }
        DisplayableKeyGetter kg = k -> k.name().toLowerCase();

        interface KeyListGetter {
            List<Key> getList(List<KeyEntry> entries);
        }
        KeyListGetter lg = e -> e.stream().map(KeyEntry::key).toList();

        // unexpected keys
        loggedKeys.stream()
                .filter(e -> !Key.getAllParametersFor(header).contains(e.key))
                .forEach(e -> addNoticeAt(e.index, "cannot resolve key \"" + kg.get(e.key) + "\" for item type " + header));

        // missing required params
        Ruleset activeRuleset = Main.getRuleset();
        List<String> missingKeys = Key.getRequiredParametersFor(header, activeRuleset).stream()
                .filter(k -> !lg.getList(loggedKeys).contains(k))
                .map(kg::get).toList();

        if (!missingKeys.isEmpty()) {
            String missingKeyString = String.join(", ", missingKeys);
            addNoticeAt(headerLine, "missing parameters " + missingKeyString + " for item type " + header);
        }

        // duplicate params
        List<Key> allowedDuplicates = List.of(Key.WEAPONS, Key.SPELLS, Key.GUNS);

        List<KeyEntry> duplicates = new ArrayList<>();
        List<KeyEntry> entryDump = new ArrayList<>();
        for (KeyEntry entry : loggedKeys) {
            if (allowedDuplicates.contains(entry.key)) continue;

            boolean duplicate = lg.getList(entryDump).contains(entry.key);
            if (duplicate) duplicates.add(entry);
            else entryDump.add(entry);
        }
        duplicates.forEach(e -> addNoticeAt(e.index, "duplicate key"));
    }

    private void validateConfig(ArrayList<String> lines, int configStartLine) {
        int endLine = configStartLine + lines.size() - 1;
        if (!lines.getFirst().equals(CONFIG_OPEN_TOKEN)) {
            addNoticeAt(configStartLine, "invalid config opener; " + CONFIG_OPEN_TOKEN + " expected");
            return;
        }
        if (!lines.getFirst().equals(CONFIG_CLOSE_TOKEN)) {
            addNoticeAt(endLine, "invalid config closer; " + CONFIG_CLOSE_TOKEN + " expected");
        }
        // TODO needs more robust validation for error prevention
        try {
            ConfigSet configSet = test(lines);
            Main.applyRuleset(configSet.ruleset());
        } catch (ConfigException ce) {
            addNoticeAt(configStartLine, ce.getSimpleReason());
        }
    }

    private void validateLastItem(ArrayList<String> lines, int startLine) {
        if (lines.isEmpty()) return;

        String opener = lines.getFirst();
        if (opener.startsWith(".")) {
            validateItem(lines, startLine);
        } else if (opener.startsWith(CONFIG_GENERIC_TOKEN)) {
            validateConfig(lines, startLine);
        }
    }

    private String getFullLine(Token token) {
        StringBuilder builder = new StringBuilder();
        while (token != null && token.isPaintable()) {
            builder.append(token.getLexeme());
            token = token.getNextToken();
        }
        return builder.toString();
    }

    private void addNoticeAt(int i, String msg) {
        parseResult.addNotice(new DefaultParserNotice(this, msg, i));
    }

    record KeyEntry(Key key, Integer index) {}

}