package txt_input;

import util.Filter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static util.TxtReader.withoutComments;

public class Reader5e {

    public static <T> List<T> getInstancesFromCode(URL url, Class<T> instanceType) throws IOException {
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(url.openStream(), StandardCharsets.UTF_8))) {

            List<String> lines = reader.lines().toList();
            List<Object> allItems = getAllItems(lines);

            return Filter.matchingClass(allItems, instanceType);
        }
    }

    public static List<Object> getAllItems(List<String> lines) {
        List<List<String>> items = new ArrayList<>();
        List<String> currentItem = null;

        for (String line : lines) {
            if (!line.startsWith(".") && !Key.lineStartsWithKey(line)) continue;

            if (line.startsWith(".")) {
                currentItem = new ArrayList<>();
                currentItem.add(line);
                items.add(currentItem);
            } else if (currentItem != null)
                currentItem.add(line);
        }

        return items.stream()
                .map(params -> {
                    String header = withoutComments(params.getFirst());
                    Item item = Item.getFromHeader(header);
                    return item.decode(params);
                })
                .filter(Objects::nonNull)
                .toList();
    }

    public static boolean fileCompiles(URL file) {
        try {
            getInstancesFromCode(file, Object.class);
            return true;
        } catch (Exception ignored) {
            return false;
        }
    }

}