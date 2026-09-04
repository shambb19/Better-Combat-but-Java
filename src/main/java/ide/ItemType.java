package ide;

import config.Config;
import config.ruleset.Ruleset;
import input.syntax.Key;
import lombok.*;
import lombok.experimental.*;
import org.fife.ui.rsyntaxtextarea.templates.StaticCodeTemplate;
import util.StringUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static input.syntax.Key.AC;
import static input.syntax.Key.HP;

@AllArgsConstructor @FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public enum ItemType {

    PARTY, NPC, ENEMY,
    SCENARIO,
    WEAPON, SPELL, GUN;

    static final Map<Ruleset, List<ItemType>> inaccessibleTypesMap = Map.of(
            Ruleset.STANDARD_RULESET, List.of(GUN),
            Ruleset.STEAMPUNK_RULESET, List.of(SPELL)
    );

    static final Map<Ruleset, List<Key>> nonstandardKeysMap = Map.of(
            Ruleset.STANDARD_RULESET, List.of(),
            Ruleset.STEAMPUNK_RULESET, List.of(HP, AC)
    );

    public StaticCodeTemplate getCodeTemplate() {
        String header = StringUtil.headerString(name());
        String id = header.replaceAll("[^a-zA-Z0-9_]", "");

        List<Key> autofillKeys = new ArrayList<>(Key.getAllParametersFor(header));

        Ruleset ruleset = Config.getRuleset();
        nonstandardKeysMap.get(ruleset).forEach(autofillKeys::remove);

        List<String> autofillLines = autofillKeys.stream().map(Key::getAutofillLine).toList();

        return new StaticCodeTemplate(id, id, StringUtil.fromList(autofillLines, true));
    }

    public boolean isUnavailableInActiveRuleset() {
        return inaccessibleTypesMap.get(Config.getRuleset()).contains(this);
    }

    public static boolean isInvalidHeader(String query) {
        String queryRoot = query.substring(1);
        return Stream.of(values()).noneMatch(i -> i.name().equalsIgnoreCase(queryRoot));
    }

}