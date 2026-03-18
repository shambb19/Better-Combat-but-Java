package _global_list;

import txt_input.Reader5e;
import util.Message;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class GlobalList<T> {

    protected final ArrayList<T> list = new ArrayList<>();

    protected void init(URL url, Class<T> type) {
        List<T> inputs = Reader5e.getInstancesFromCode(url, type);
        list.addAll(inputs);
    }

    protected void init(File file, Class<T> type) {
        try {
            URL url = file.toURI().toURL();
            init(url, type);
        } catch (MalformedURLException e) {
            Message.fileError(e);
        }
    }

    protected <S extends T> void addItem(S item) {
        if (item != null) {
            list.add(item);
        }
    }

    protected <S extends T> S getItem(String name, Class<S> type) {
        return list.stream()
                .filter(type::isInstance)
                .map(type::cast)
                .filter(obj -> obj.toString().trim().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }

    protected <S extends T> ArrayList<S> castToList(Class<S> type) {
        return new ArrayList<>(list
                .stream()
                .filter(type::isInstance)
                .map(type::cast)
                .toList()
        );
    }

}
