package damage_implements;

import character_info.Stats;
import admin.Admin;
import util.TxtReader;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class Spells {

    // TODO add effect advantage for guiding bolt

    private static final ArrayList<Spell> spells = new ArrayList<>();

    public static final Spell MANUAL_HIT = new Spell("Manual with Hit Roll", -1, -1, null, null);
    public static final Spell MANUAL_SAVE = new Spell("Manual with Save Throw", -1, -1, null, null);

    public static void init(URL url) {
        ArrayList<String> lines = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(url.openStream(), StandardCharsets.UTF_8))) {

            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        lines.replaceAll(String::trim);
        lines.removeIf(String::isBlank);
        decodeFile(lines);
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
            ArrayList<String> currentRead = new ArrayList<>();

            lines.removeFirst();
            while (!lines.getFirst().equals("}")) {
                currentRead.add(lines.removeFirst());
            }
            lines.removeFirst();

            spells.add(TxtReader.decodeSpell(currentRead));
        }
    }

}
