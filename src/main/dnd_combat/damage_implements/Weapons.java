package damage_implements;

import character_info.Stats;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import static util.Reader.*;

public class Weapons {

    private static final ArrayList<Weapon> weapons = new ArrayList<>();
    public static final Weapon MANUAL = new Weapon("Manual", -1, -1, null);

    public static ArrayList<Weapon> get() {
        return weapons;
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

    public static void decodeFile(ArrayList<String> lines) {
        while (!lines.isEmpty()) {
            ArrayList<String> currentRead = new ArrayList<>();

            lines.removeFirst();
            while (!lines.getFirst().equals("}")) {
                currentRead.add(lines.removeFirst());
            }
            lines.removeFirst();

            String name = "name";
            int numDice = 0, dieSize = 0;
            Stats.stat stat = null;

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
                    case "stat" -> stat = mod(value);
                }
            }

            weapons.add(new Weapon(name, numDice, dieSize, stat));
        }
    }

}
