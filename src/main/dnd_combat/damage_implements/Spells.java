package damage_implements;

import character_info.Stats;
import admin.Admin;
import org.apache.commons.io.FileUtils;
import util.TxtReader;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.util.ArrayList;

public class Spells {

    // TODO add effect advantage for guiding bolt

    private static final ArrayList<Spell> spells = new ArrayList<>();

    public static final Spell MANUAL_HIT = new Spell("Manual with Hit Roll", -1, -1, null, null);
    public static final Spell MANUAL_SAVE = new Spell("Manual with Save Throw", -1, -1, null, null);

    public static void init(URL url) {
        File file = FileUtils.toFile(url);
        try {
            ArrayList<String> lines = new ArrayList<>(Files.readAllLines(file.toPath()));

            lines.replaceAll(String::trim);
            lines.removeIf(String::isBlank);
            decodeFile(lines);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<Spell> get() {
        return spells;
    }

    public static void add(Spell spell) {
        spells.add(spell);
    }

    public static void manualAdjust(String name, String key, String value) {
        Spell target = null;
        for (Spell spell : spells) {
            if (spell.name().equals(name)) {
                target = spell;
            }
        }
        if (target == null) {
            return;
        }

        replace(target, key, value);
    }

    private static void replace(Spell spell, String key, String value) {
        int i = spells.indexOf(spell);
        Spell adjusted = null;
        switch (key) {
            case Admin.NAME_EDIT_CODE -> adjusted = new Spell(value, spell.numDice(), spell.dieSize(), spell.savingThrow(), spell.effect());
            case Admin.DAMAGE_EDIT_CODE -> {
                int numDice = TxtReader.getNumDice(value);
                int dieSize = TxtReader.getDieSize(value);
                adjusted = new Spell(spell.name(), numDice, dieSize, spell.savingThrow(), spell.effect());
            }
            case Admin.STAT_EDIT_CODE -> {
                Stats.stat stat = TxtReader.mod(value);
                adjusted = new Spell(spell.name(), spell.numDice(), spell.dieSize(), stat, spell.effect());
            }
            case Admin.EFFECT_EDIT_CODE -> {
                Effect effect = Effect.withRawName(value);
                adjusted = new Spell(spell.name(), spell.numDice(), spell.dieSize(), spell.savingThrow(), effect);
            }
        }

        if (adjusted != null) {
            spells.set(i, adjusted);
        }
    }

    private static void decodeFile(ArrayList<String> lines) {
        while (!lines.isEmpty()) {
            String line = lines.removeFirst();
            spells.add(TxtReader.decodeSpell(line));
        }
    }

}
