package damage_implements;

import txt_input_2.Txt5eReader;

import java.net.URL;
import java.util.ArrayList;
import java.util.Objects;

public class Spells {

    private static ArrayList<Spell> spells = new ArrayList<>();

    public static final Spell MANUAL_HIT = new Spell("Manual with Hit Roll", -1, -1, null, null);
    public static final Spell MANUAL_SAVE = new Spell("Manual with Save Throw", -1, -1, null, null);

    public static void init(URL url) {
        spells = Objects.requireNonNull(Txt5eReader.getCode(url)).toList(Spell.class);
        spells.add(MANUAL_HIT);
        spells.add(MANUAL_SAVE);
    }

    public static ArrayList<Spell> get() {
        return spells;
    }

    public static void add(Spell spell) {
        spells.add(spell);
    }

}
