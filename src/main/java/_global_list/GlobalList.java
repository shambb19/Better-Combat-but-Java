package _global_list;

import combat_object.CombatObject;
import lombok.*;
import lombok.experimental.Delegate;
import txt_input.Reader5e;
import util.Message;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class GlobalList<T extends CombatObject> {

    @Delegate protected final ArrayList<T> list = new ArrayList<>();

    protected <S extends T> void init(@NonNull URL url, Class<S> type) {
        try {
            List<S> inputs = Reader5e.getInstancesFromCode(url, type);
            list.addAll(inputs);
        } catch (IOException e) {
            Message.fileError(null, e);
            Logger.getAnonymousLogger().severe("init in GlobalList: could not add elements");
        }
    }

}
