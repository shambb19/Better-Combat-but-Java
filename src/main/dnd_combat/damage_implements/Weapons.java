package damage_implements;

import admin.Admin;
import character_info.Stats;
import util.TxtReader;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class Weapons {

    private static final ArrayList<Weapon> weapons = new ArrayList<>();
    public static final Weapon MANUAL = new Weapon("Manual", -1, -1, null);

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

    public static ArrayList<Weapon> get() {
        return weapons;
    }

    public static void add(Weapon weapon) {
        weapons.add(weapon);
    }

    public static void manualAdjust(String name, String key, String value) {
        Weapon target = null;
        for (Weapon weapon : weapons) {
            if (weapon.name().equals(name)) {
                target = weapon;
            }
        }
        if (target == null) {
            return;
        }

        replace(target, key, value);
    }

    private static void replace(Weapon weapon, String key, String value) {
        int i = weapons.indexOf(weapon);
        Weapon adjusted = null;
        switch (key) {
            case Admin.NAME_EDIT_CODE -> adjusted = new Weapon(value, weapon.numDice(), weapon.dieSize(), weapon.stat());
            case Admin.DAMAGE_EDIT_CODE -> {
                int numDice = TxtReader.getNumDice(value);
                int dieSize = TxtReader.getDieSize(value);
                adjusted = new Weapon(weapon.name(), numDice, dieSize, weapon.stat());
            }
            case Admin.STAT_EDIT_CODE -> {
                Stats.stat stat = TxtReader.mod(value);
                adjusted = new Weapon(weapon.name(), weapon.numDice(), weapon.dieSize(), stat);
            }
        }

        if (adjusted != null) {
            weapons.set(i, adjusted);
        }
    }

    public static void decodeFile(ArrayList<String> lines) {
        while (!lines.isEmpty()) {
            ArrayList<String> currentRead = new ArrayList<>();

            lines.removeFirst();
            while (!lines.getFirst().equals("}")) {
                currentRead.add(lines.removeFirst());
            }
            lines.removeFirst();

            weapons.add(TxtReader.decodeWeapon(currentRead));
        }
    }

}
