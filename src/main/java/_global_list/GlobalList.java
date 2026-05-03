package _global_list;

import input.CampaignReader;
import util.Message;

import java.util.ArrayList;

public class GlobalList<T extends combat_object.CombatObject> {

    @lombok.experimental.Delegate protected final ArrayList<T> list = new ArrayList<>();

    protected <S extends T> void init(java.net.URL url, Class<S> type) {
        try {
            var inputs = CampaignReader.getInstancesFromCode(url, type);
            list.addAll(inputs);
        } catch (java.io.IOException e) {
            Message.showFileErrorMessage(e);
        }
    }

}
