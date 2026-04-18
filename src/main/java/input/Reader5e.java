package input;

import combat_object.CombatObject;
import combat_object.combatant.NPC;
import combat_object.combatant.PC;
import combat_object.damage_implements.Spell;
import combat_object.damage_implements.Weapon;
import combat_object.scenario.Scenario;
import lombok.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static util.TxtReader.withoutComments;

public class Reader5e {

    public static <T extends CombatObject> List<T> getInstancesFromCode(URL url, Class<T> instanceType) throws IOException {
        @Cleanup BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream(), StandardCharsets.UTF_8));
        List<String> lines = reader.lines().toList();

        List<ItemBlock> blocks = getAllItemBlocks(lines);

        return blocks.stream()
                .map(block -> createObject(block.header, block.params, instanceType))
                .filter(instanceType::isInstance)
                .map(instanceType::cast)
                .toList();
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

    private static <T extends CombatObject> Object createObject(String header, EnumMap<Key, Object> map, Class<T> instanceType) {
        var headerClasses = Map.of(
                ".party", PC.class, ".npc", NPC.class, ".enemy", NPC.class,
                ".weapon", Weapon.class, ".spell", Spell.class,
                ".scenario", Scenario.class
        );
        var targetClass = headerClasses.get(header);

        if (targetClass == null || !instanceType.isAssignableFrom(targetClass)) {
            return null;
        }

        return switch (header) {
            case ".party" -> PC.from(map);
            case ".npc" -> NPC.from(map, false);
            case ".enemy" -> NPC.from(map, true);
            case ".weapon" -> Weapon.from(map);
            case ".spell" -> Spell.from(map);
            case ".scenario" -> Scenario.from(map);
            default -> null;
        };
    }

    public static boolean fileCompiles(URL file) {
        if (file == null) return false;
        try {
            getInstancesFromCode(file, CombatObject.class);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private static EnumMap<Key, Object> toMap(List<String> params) {
        EnumMap<Key, Object> map = new EnumMap<>(Key.class);

        params.stream()
                .skip(1)
                .forEach(param -> {
                    Key key = Key.get(param);
                    Optional.ofNullable(key).ifPresent(k -> map.put(k, Key.value(param)));
                });

        return map;
    }

    @Value
    private static class ItemBlock {
        String header;
        EnumMap<Key, Object> params;

        ItemBlock(List<String> lines) {
            this.header = withoutComments(lines.getFirst());
            this.params = toMap(lines);
        }
    }

}