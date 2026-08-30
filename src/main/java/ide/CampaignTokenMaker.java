package ide;

import config.Config;
import org.fife.ui.rsyntaxtextarea.AbstractTokenMaker;
import org.fife.ui.rsyntaxtextarea.Token;
import org.fife.ui.rsyntaxtextarea.TokenMap;
import org.intellij.lang.annotations.MagicConstant;

import javax.swing.text.Segment;

import static ide.CampaignToken.*;

public class CampaignTokenMaker extends AbstractTokenMaker {

    @Override public TokenMap getWordsToHighlight() {
        return new TokenMap();
    }

    @Override public void addToken(Segment segment, int start, int end, int tokenType, int startOffset) {
        super.addToken(segment, start, end, tokenType, startOffset);
    }

    @Override public Token getTokenList(Segment text, int initialTokenType, int startOffset) {
        resetTokenList();

        char[] array = text.array;
        int offset = text.offset;
        int end = offset + text.count;
        int lineEnd = end - 1;

        if (text.count == 0) {
            addNullToken();
            return firstToken;
        }

        int docOffset = startOffset - offset; // startOffset (array relative) to document relative
        int tokenStart = offset;

        int firstNonSpace = offset;
        while (firstNonSpace < end && array[firstNonSpace] == ' ') firstNonSpace++;

        if (array[firstNonSpace] == '~') {
            addToken(array, offset, lineEnd, COMMENT, docOffset + offset);
            addNullToken();
            return firstToken;
        }

        if (array[offset] == '~') { // entire segment set to comment
            addToken(array, offset, lineEnd, COMMENT, docOffset + offset);
            addNullToken();
            return firstToken;
        }

        boolean isConfigAction = configLineStartsWith(array, offset, Config.CONFIG_GENERIC_TOKEN);
        if (initialTokenType == CONFIG || isConfigAction) {
            int token = isConfigAction ? CONFIG : VALUE; // interior lines use VALUE color

            addLineWithTrailingComment(array, offset, end, token, docOffset);

            if (configLineStartsWith(array, offset, Config.CONFIG_CLOSE_TOKEN)) {
                addNullToken();
            } else {
                addToken(array, end, end, CONFIG, docOffset + end);
            }

            return firstToken;
        }

        if (array[offset] == '.') { // header lines

            int i = offset;

            while (i < end && array[i] != '<' && array[i] != ' ') i++; // if header, stop at tag or name
            addToken(array, tokenStart, i - 1, HEADER, docOffset + tokenStart);

            if (i < end && array[i] == '<') { // if tags, stop at name
                tokenStart = i;
                while (i < end && array[i] != ' ') i++;
                addToken(array, tokenStart, i - 1, TAG, docOffset + tokenStart);
            }

            if (i < end && array[i] == ' ') { // space indicates name start and is final header line item
                addToken(array, i, i, Token.WHITESPACE, docOffset + i);

                int nameStart = i + 1;
                if (nameStart < end) {
                    addLineWithTrailingComment(array, nameStart, end, NAME, docOffset);
                }
            }

            addNullToken();
            return firstToken;
        }

        if (containsColon(array, offset, end) || array[offset] == '+') { // colon used as key: value delimiter
            int i = offset;

            int token = array[offset] == '+' ? KEY_OPTIONAL : KEY;

            while (i < end && array[i] != ':') i++;
            addToken(array, tokenStart, i, token, docOffset + tokenStart);

            int spaceStart = i + 1;
            int spaceEnd = spaceStart;

            while (spaceEnd < end && array[spaceEnd] == ' ') spaceEnd++;

            if (spaceEnd > spaceStart) {
                addToken(array, spaceStart, spaceEnd - 1, Token.WHITESPACE, docOffset + spaceStart);
            }

            i = spaceEnd;

            if (i < end) { // check for inline comments
                addLineWithTrailingComment(array, i, end, VALUE, docOffset);
            }

            addNullToken();
            return firstToken;
        }

        addLineWithTrailingComment(array, offset, end, UNEXPECTED, docOffset); // unrecognized line

        addNullToken();
        return firstToken;
    }

    private void addLineWithTrailingComment(char[] array, int offset, int end, int tokenType, int docOffset) {
        int i = offset;
        while (i < end && array[i] != '~') i++;

        if (i > offset) {
            addToken(array, offset, i - 1, tokenType, docOffset + offset);
        }
        if (i < end && array[i] == '~') {
            addToken(array, i, end - 1, COMMENT, docOffset + i);
        }
    }

    private boolean configLineStartsWith(
            char[] array, int offset,
            @MagicConstant(valuesFromClass = Config.class) String actionKey
    ) {
        if (offset + actionKey.length() > array.length) return false;

        for (int i = 0; i < actionKey.length(); i++) {
            if (array[offset + i] != actionKey.charAt(i)) return false;
        }

        return true;
    }

    private boolean containsColon(char[] array, int offset, int end) {
        for (int i = offset; i < end; i++) {
            if (array[i] == ':') return true;
        }
        return false;
    }
}
