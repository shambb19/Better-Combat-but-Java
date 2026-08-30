package input;

import combat_object.CombatObject;
import combat_object.combatant.NPC;
import combat_object.combatant.PC;
import combat_object.implement.Gun;
import combat_object.implement.Spell;
import combat_object.implement.Weapon;
import combat_object.scenario.Scenario;
import config.Config;
import exception.InvalidParameterException;
import exception.InvalidSyntaxError;
import input.syntax.Key;
import input.syntax.Tag;
import lombok.*;
import lombok.experimental.*;
import util.Message;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;

@ExtensionMethod({TextReader.class, util.Filterable.class})
public class CampaignReader {

    public static List<String> getLines(URL url) throws IOException {
        if (url == null) return new ArrayList<>();

        @Cleanup BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream(), StandardCharsets.UTF_8));
        return new ArrayList<>(reader.lines().toList());
    }

    public static <T extends CombatObject> List<T> getInstancesFromCode(URL url, Class<T> instanceType) throws IOException {
        List<String> lines = getLines(url);

        lines = TextReader.extractConfigBlock(lines, Config::applyGlobal);

        return getAllItemBlocks(lines).stream().map(CampaignReader::createObject)
                .filter(Objects::nonNull).of().castToAsList(instanceType);
    }

    private static List<ItemBlock> getAllItemBlocks(List<String> lines) {
        List<ItemBlock> blocks = new ArrayList<>();
        List<String> currentLines = null;

        for (String line : lines) {
            if (line.startsWith(".")) {
                Optional.ofNullable(currentLines).ifPresent(l -> blocks.add(new ItemBlock(l)));
                currentLines = new ArrayList<>();
                currentLines.add(line);
            } else if (currentLines != null && Key.lineStartsWithKey(line)) {
                currentLines.add(line);
            }
        }
        Optional.ofNullable(currentLines).ifPresent(l -> blocks.add(new ItemBlock(l)));
        return blocks;
    }

    private static CombatObject createObject(ItemBlock block) {
        String header = block.header;
        EnumMap<Key, Object> map = block.params;

        if (block.tags.contains(Tag.INCOMPLETE)) return null;

        return switch (header) {
            case ".party" -> PC.from(map, block.tags);
            case ".npc" -> NPC.from(map, block.tags, false);
            case ".enemy" -> NPC.from(map, block.tags, true);
            case ".weapon" -> Weapon.from(map);
            case ".spell" -> Spell.from(map);
            case ".gun" -> Gun.from(map);
            case ".scenario" -> Scenario.from(map);
            default ->
                    throw new InvalidParameterException("CampaignReader.createObject", "header", header, "valid item header");
        };
    }

    public static boolean fileCompiles(URL file) {
        if (file == null) return false;
        try {
            getInstancesFromCode(file, CombatObject.class);
            return true;
        } catch (Exception e) {
            Message.showAsErrorMessage(e.getMessage());
            return false;
        }
    }

    private static EnumMap<Key, Object> toMap(List<String> params) {
        EnumMap<Key, Object> map = new EnumMap<>(Key.class);

        Arrays.stream(Key.values())
                .filter(k -> k.getDefaultValue() != null)
                .forEach(k -> map.put(k, k.getDefaultValue()));

        params.stream().skip(1)
                .forEach(param -> {
                    Key key = Key.getKeyFromString(param);
                    Object value = Key.getAppropriateValueFromLine(param);

                    Optional.ofNullable(key).ifPresent(k ->
                            map.put(k, Objects.requireNonNullElse(value, k.getDefaultValue()))
                    );
                });

        return map;
    }

    @Value private static class ItemBlock {
        String header;
        Set<Tag> tags;
        EnumMap<Key, Object> params;

        ItemBlock(List<String> lines) {
            String opener = lines.getFirst();
            header = opener.getHeader();
            tags = opener.getTags();
            params = toMap(lines);

            String name = opener.getName();
            if (name.isBlank()) {
                throw new InvalidSyntaxError(false, "Empty name");
            }

            params.put(Key.NAME, opener.getName());
        }
    }

}