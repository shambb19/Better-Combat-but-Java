package damage_implements;

import txt_input_2.Txt5eReader;

import java.net.URL;
import java.util.ArrayList;
import java.util.Objects;

public class Weapons {

    private static ArrayList<Weapon> weapons = new ArrayList<>();
    public static final Weapon MANUAL = new Weapon("Manual", -1, -1, null);

    public static void init(URL url) {
        weapons = Objects.requireNonNull(Txt5eReader.getCode(url)).toList(Weapon.class);
        weapons.add(MANUAL);
    }

    public static ArrayList<Weapon> get() {
        return weapons;
    }

    public static void add(Weapon weapon) {
        weapons.add(weapon);
    }

}
