package _global_list;

import combat_object.CombatObject;
import input.CampaignReader;
import lombok.experimental.Delegate;
import util.Message;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

public class GlobalList<T extends CombatObject> {

    @Delegate protected final ArrayList<T> list = new ArrayList<>();

    protected <S extends T> void init(URL url, Class<S> type) {
        try {
            var inputs = CampaignReader.getInstancesFromCode(url, type);
            list.addAll(inputs);
        } catch (IOException e) {
            Message.showFileErrorMessage(e, Message.READ_ERROR);
        }
    }

}
