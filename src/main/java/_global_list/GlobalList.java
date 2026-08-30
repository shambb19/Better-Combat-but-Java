package _global_list;

import combat_object.CombatObject;
import input.CampaignReader;
import util.Message;

import java.util.ArrayList;

public class GlobalList<T extends CombatObject> {

    @lombok.experimental.Delegate protected final ArrayList<T> list = new ArrayList<>();

    protected <S extends T> void init(java.net.URL url, Class<S> type) {
        try {
            var inputs = CampaignReader.getInstancesFromCode(url, type);
            list.addAll(inputs);
        } catch (java.io.IOException e) {
            Message.showFileErrorMessage(e, Message.READ_ERROR);
        }
    }

}
