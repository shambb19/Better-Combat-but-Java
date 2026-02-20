package txt_input;

import combat.Main;
import combatants.Combatant;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class PartyWriter {

    private final File file;

    public PartyWriter() throws IOException {
        file = new File("post-battle-campaign-" + getTimeString() + ".txt");
        try (FileWriter writer = new FileWriter(file)) {
            ArrayList<Combatant> friendlies = Main.battle.friendlies();
            for (Combatant combatant : friendlies) {
                if (combatant.isNPC()) {
                    continue;
                }
                ArrayList<String> txt = combatant.toTxt();
                for (String line : txt) {
                    writer.write(line);
                    writer.write(System.lineSeparator());
                }
            }
        }
    }

    public File getFile() {
        return file;
    }

    private String getTimeString() {
        String time = LocalDateTime.now().toString();
        time = time.substring(0, time.lastIndexOf(":"));
        return time.replace(":", "-");
    }

}
