package _global_list;

import combat_object.CombatObject;
import input.CampaignReader;
import lombok.experimental.Delegate;

import java.net.URL;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

public class GlobalList<T extends CombatObject> {

    @Delegate protected final Set<T> set = new HashSet<>();

    private <S extends T> List<S> initList(URL url, Class<S> type) {
        return CampaignReader.getInstancesFromCode(url, type);
    }

    protected <S extends T> void init(URL url, Class<S> type) {
        List<S> inputs = initList(url, type);
        set.addAll(inputs);
    }

    @SuppressWarnings("all")
    protected <S extends T> void init(URL url, Class<S> type, Function<List<S>, List<S>> converter) {
        List<S> inputs = initList(url, type);
        List<S> converted = converter.apply(inputs);
        set.addAll(converted);
    }

}
