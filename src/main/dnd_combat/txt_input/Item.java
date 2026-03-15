package txt_input;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public enum Item {

    PARTY(".party", Decoder::pc),
    NPC(".npc", Decoder::npc),
    ENEMY(".enemy", Decoder::enemy),
    SCENARIO(".scenario", Decoder::scenario),
    WEAPON(".weapon", Decoder::weapon),
    SPELL(".spell", Decoder::spell);

    private static final Map<String, Item> LOOKUP = Arrays.stream(values())
            .collect(Collectors.toMap(item -> item.header, item -> item));

    private final String header;
    private final ItemFactory itemFactory;

    Item(String header, ItemFactory itemFactory) {
        this.header = header;
        this.itemFactory = itemFactory;
    }

    public static Item getFromHeader(String header) {
        return LOOKUP.get(header);
    }

    public void log(List<String> item, List<Object> list) {
        list.add(itemFactory.log(item));
    }

}

@FunctionalInterface
interface ItemFactory {
    Object log(List<String> params);
}