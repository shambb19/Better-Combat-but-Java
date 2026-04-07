package _global_list;

import txt_input.Reader5e;
import util.Filter;
import util.Message;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class GlobalList<T> {

    protected final ArrayList<T> list = new ArrayList<>();

    protected <S extends T> void init(URL url, Class<S> type) {
        if (url == null) return;

        try {
            List<S> inputs = Reader5e.getInstancesFromCode(url, type);
            list.addAll(inputs);
        } catch (IOException e) {
            Message.fileError(e);
            Logger.getAnonymousLogger().severe("init in GlobalList: could not add elements");
        }
    }

    protected <S extends T> void addItem(S item) {
        if (item != null) list.add(item);
    }

    protected <S extends T> S getItem(String name, Class<S> type) {
        var withCorrectType = Filter.matchingClass(list, type);

        return Filter.firstWithToStringEquals(withCorrectType, name);
    }

    protected <S extends T> List<S> castToList(Class<S> type) {
        return Filter.matchingClass(list, type);
    }

}
