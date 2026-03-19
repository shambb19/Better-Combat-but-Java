package txt_input;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static util.TxtReader.withoutComments;

public class Reader5e {

    public static <T> List<T> getInstancesFromCode(URL url, Class<T> instanceType) {
        try {
            return getInstancesFromCode(FileUtils.toFile(url), instanceType);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static <T> List<T> getInstancesFromCode(File file, Class<T> instanceType) {
        try {
            List<String> lines = Files.readAllLines(file.toPath());
            List<Object> code = getCode(lines);

            return code.stream()
                    .filter(obj -> instanceType.isAssignableFrom(obj.getClass()))
                    .map(instanceType::cast)
                    .toList();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static List<Object> getCode(List<String> lines) {
        List<String>[] paramSets = getItems(lines);
        List<Object> items = new ArrayList<>();

        for (List<String> params : paramSets) {
            String header = withoutComments(params.getFirst());
            Item item = Item.getFromHeader(header);

            assert item != null;
            item.log(params, items);
        }

        items.removeIf(Objects::isNull);
        return items;
    }

    private static List<String>[] getItems(List<String> lines) {
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

    public static boolean fileCompiles(URL file) {
        if (file == null) {
            return false;
        }

        try {
            getInstancesFromCode(file, Object.class);
            return true;
        } catch (Exception ignored) {
            return false;
        }
    }

}