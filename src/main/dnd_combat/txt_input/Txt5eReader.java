package txt_input;

import damage_implements.DamageImplements;
import damage_implements.Spell;
import damage_implements.Weapon;
import org.apache.commons.io.FileUtils;
import util.Message;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static txt_input.Decoder.*;
import static util.TxtReader.withoutComments;

public class Txt5eReader {

    public static Txt5e getCode(URL url) {
        try {
            return getCode(FileUtils.toFile(url));
        } catch (Exception e) {
            Message.fileError(e);
            e.printStackTrace();
            return null;
        }
    }

    public static Txt5e getCode(File file) {
        try {
            List<String> lines = Files.readAllLines(file.toPath());
            return getCode(new ArrayList<>(lines));
        } catch (IOException e) {
            Message.fileError(e);
            e.printStackTrace();
            return null;
        }
    }

    public static Txt5e getCode(ArrayList<String> lines) {
        ArrayList<String>[] items = getItems(lines);
        ArrayList<Object> objects = new ArrayList<>();

        ArrayList<ArrayList<String>> party = new ArrayList<>();
        ArrayList<ArrayList<String>> scenarios = new ArrayList<>();

        for (ArrayList<String> item : items) {
            switch (withoutComments(item.getFirst())) {
                case ".weapon" -> DamageImplements.add(Objects.requireNonNull(implement(item, Weapon.class)));
                case ".spell" -> DamageImplements.add(Objects.requireNonNull(implement(item, Spell.class)));
                case ".party" -> party.add(item);
                case ".npc" -> objects.add(npc(item, false));
                case ".enemy" -> objects.add(npc(item, true));
                case ".scenario" -> scenarios.add(item);
            }
        }

        party.forEach(item -> objects.add(pc(item)));
        scenarios.forEach(item -> objects.add(scenario(item, objects)));

        objects.removeIf(Objects::isNull);
        return new Txt5e(objects);
    }

    private static ArrayList<String>[] getItems(ArrayList<String> lines) {
        lines.removeIf(line -> !line.startsWith(".") && !Key.lineStartsWithKey(line));

        ArrayList<ArrayList<String>> linesRaw = new ArrayList<>();
        ArrayList<String> currentItem = null;

        for (String line : lines) {
            if (line.startsWith(".")) {
                currentItem = new ArrayList<>();
                currentItem.add(line);
                linesRaw.add(currentItem);
            } else if (currentItem != null) {
                currentItem.add(line);
            }
        }

        @SuppressWarnings("unchecked")
        ArrayList<String>[] arr = new ArrayList[linesRaw.size()];
        return linesRaw.toArray(arr);
    }

}
