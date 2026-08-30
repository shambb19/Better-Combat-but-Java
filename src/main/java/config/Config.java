package config;

import config.queue.PlayerQueue;
import config.ruleset.Ruleset;
import exception.InvalidParameterException;
import input.TextReader;
import lombok.*;
import lombok.experimental.*;

import java.util.ArrayList;
import java.util.List;

@FieldDefaults(level = AccessLevel.PRIVATE)
@ExtensionMethod(input.TextReader.class)
public class Config {

    public static final String
            CONFIG_GENERIC_TOKEN = "config<<",
            CONFIG_OPEN_TOKEN = "config<<open",
            CONFIG_CLOSE_TOKEN = "config<<close";

    @Getter @Setter static Ruleset ruleset = Ruleset.STANDARD_RULESET;
    @Getter static boolean damageHidden = false;
    @Getter static Class<? extends PlayerQueue> overrideQueueType = null;

    public static void applyGlobal(ArrayList<String> config) {
        ConfigSet configurables = configure(config);

        ruleset = configurables.ruleset;
        damageHidden = configurables.damageHidden;
        overrideQueueType = configurables.queue;
    }

    public static ConfigSet test(ArrayList<String> config) {
        return configure(config);
    }

    private static ConfigSet configure(ArrayList<String> configRaw) {
        List<String> config = configRaw.stream()
                .filter(l -> !l.trim().equals(CONFIG_OPEN_TOKEN) && !l.trim().equals(CONFIG_CLOSE_TOKEN))
                .map(TextReader::withoutComments).toList();

        Ruleset rulesetNew = Ruleset.STANDARD_RULESET;
        boolean damageHiddenNew = false;
        Class<? extends PlayerQueue> queueTypeNew = null;

        for (String line : config) {
            String[] split;
            try {
                split = line.split("\\.");
            } catch (Exception ignored) {
                throw new ConfigException(line, "valid configurable.value line");
            }

            if (split.length == 1) throw new ConfigException(line, "valid configurable.value line");

            String key = split[0];
            String value = split[1];

            switch (key) {
                case "ruleset" -> rulesetNew = rulesetConfig(value);
                case "damage" -> damageHiddenNew = damageConfig(value);
                case "queue" -> queueTypeNew = queueConfig(value);
                default -> throw new ConfigException(line, "valid configurable key");
            }
        }

        return new ConfigSet(rulesetNew, damageHiddenNew, queueTypeNew);
    }

    private static Ruleset rulesetConfig(String value) {
        return switch (value) {
            case "steampunk" -> Ruleset.STEAMPUNK_RULESET;
            case "standard" -> Ruleset.STANDARD_RULESET;
            default -> throw new ConfigException("ruleset", value, "valid ruleset");
        };
    }

    private static boolean damageConfig(String value) {
        return switch (value) {
            case "show" -> false;
            case "hide" -> true;
            default -> throw new ConfigException("damage", value, "\"show\" or \"hide\"");
        };
    }

    private static Class<? extends PlayerQueue> queueConfig(String value) {
        return switch (value) {
            case "cath" -> PlayerQueue.CATH_QUEUE;
            case "standard" -> PlayerQueue.STANDARD_QUEUE;
            default -> throw new ConfigException("queue", value, "valid queue");
        };
    }

    public record ConfigSet(Ruleset ruleset, boolean damageHidden, Class<? extends PlayerQueue> queue) {}

    public static class ConfigException extends InvalidParameterException {
        public ConfigException(String errLine, String msg) {
            super("Config", "configurable", errLine, msg);
        }

        public ConfigException(String type, String errLine, String msg) {
            super("Config", type, errLine, msg);
        }
    }

}
