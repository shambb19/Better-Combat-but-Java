package config;

import config.ruleset.Ruleset;
import config.ruleset.StandardRuleset;
import config.ruleset.SteampunkRuleset;
import lombok.*;

import java.util.List;
import java.util.Optional;

public class Config {

    @Getter @Setter private static Ruleset ruleset = new StandardRuleset();
    @Getter @Setter private static boolean damageHidden = false;

    public static void configure(List<String> config) {
        config.removeIf(l -> l.startsWith("config") || l.isBlank());

        // ----- RULESET ----- //
        Optional<String> rulesetLine = config.stream().filter(l -> l.startsWith("ruleset")).findAny();
        rulesetLine.ifPresent(line -> ruleset = switch (line.split("\\.")[1]) {
            case "steampunk" -> new SteampunkRuleset();
            case null, default -> new StandardRuleset();
        });

        // ----- DAMAGE INPUT ----- //
        Optional<String> damageLine = config.stream().filter(l -> l.startsWith("damage")).findAny();
        damageLine.ifPresent(l -> damageHidden = l.split("\\.")[1].equals("show"));
    }

}
