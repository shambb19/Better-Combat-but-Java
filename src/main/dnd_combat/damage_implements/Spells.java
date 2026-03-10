package damage_implements;

import org.apache.commons.io.FileUtils;
import util.TxtReader;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.util.ArrayList;

public class Spells {

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

    private static void decodeFile(ArrayList<String> lines) {
        while (!lines.isEmpty()) {
            String line = lines.removeFirst();
            spells.add(TxtReader.decodeSpell(line));
        }
    }

}
