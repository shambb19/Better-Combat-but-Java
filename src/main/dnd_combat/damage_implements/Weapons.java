package damage_implements;

import org.apache.commons.io.FileUtils;
import util.TxtReader;

import java.io.File;
import java.net.URL;
import java.nio.file.Files;
import java.util.ArrayList;

public class Weapons {

    private static final ArrayList<Weapon> weapons = new ArrayList<>();
    public static final Weapon MANUAL = new Weapon("Manual", -1, -1, null);

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

    public static ArrayList<Weapon> get() {
        return weapons;
    }

    public static void add(Weapon weapon) {
        weapons.add(weapon);
    }

    public static void decodeFile(ArrayList<String> lines) {
        while (!lines.isEmpty()) {
            String line = lines.removeFirst();
            weapons.add(TxtReader.decodeWeapon(line));
        }
    }

}
