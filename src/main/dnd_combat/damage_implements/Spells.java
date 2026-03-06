package damage_implements;

import character_info.Stats;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import static util.Reader.*;

public class Spells {

    private static final ArrayList<Spell> spells = new ArrayList<>();

    public static final Spell MANUAL_HIT = new Spell("Manual with Hit Roll", -1, -1, null, null);
    public static final Spell MANUAL_SAVE = new Spell("Manual with Save Throw", -1, -1, null, null);

    public static ArrayList<Spell> get() {
        return spells;
    }

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

    private static void decodeFile(ArrayList<String> lines) {
        while (!lines.isEmpty()) {
            ArrayList<String> currentRead = new ArrayList<>();

            lines.removeFirst();
            while (!lines.getFirst().equals("}")) {
                currentRead.add(lines.removeFirst());
            }
            lines.removeFirst();

            String name = "name";
            int numDice = 0, dieSize = 0;
            Stats.stat saveThrow = null;
            Effect effect = null;

            while (!currentRead.isEmpty()) {
                String key = identifier(currentRead.getFirst());
                String value = withoutIdentifier(currentRead.removeFirst());

                switch (key) {
                    case "name" -> name = value;
                    case "dmg" -> {
                        numDice = getNumDice(value);
                        dieSize = getDieSize(value);
                    }
                    case "numDice" -> numDice = Integer.parseInt(value);
                    case "dieSize" -> dieSize = Integer.parseInt(value);
                    case "save" -> saveThrow = mod(value);
                    case "effect" -> effect = Effect.withRawName(value);
                }
            }

            spells.add(new Spell(name, numDice, dieSize, saveThrow, effect));
        }
    }

}
