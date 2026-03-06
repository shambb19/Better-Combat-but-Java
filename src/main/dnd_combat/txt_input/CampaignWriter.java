package txt_input;

import main.CombatMain;
import character_info.Combatant;
import util.Message;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;

public class CampaignWriter {

    private static final String FRIENDLY_HEADER = "<Allies>";
    private static final String ENEMY_HEADER = "<Enemies>";
    private static final String SCENARIO_HEADER = "<Scenarios>";
    private static final String LINE = "";

    private final File file;

    public CampaignWriter() throws IOException {
        file = new File("campaign post encounter " + LocalDate.now() + ".txt");
        try (FileWriter writer = new FileWriter(file)) {
            ArrayList<String> txt = new ArrayList<>();
            writeTeam(txt, CombatMain.BATTLE.friendliesOriginal(), FRIENDLY_HEADER);
            writeTeam(txt, CombatMain.BATTLE.enemiesOriginal(), ENEMY_HEADER);
            writeScenarios(txt);

            txt.forEach(line -> {
                try {
                    writer.write(line);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        } catch (IOException e) {
            Message.throwFileDownloadError(null, e);
        }
    }

    private void writeTeam(ArrayList<String> dest, ArrayList<Combatant> source, String header) {
        dest.add(header);
        dest.add(LINE);
        source.forEach(combatant -> dest.addAll(combatant.toTxt()));
        dest.add(header);
        dest.add(LINE);
    }

    private void writeScenarios(ArrayList<String> dest) {
        dest.add(SCENARIO_HEADER);
        dest.add(LINE);
        CombatMain.BATTLE.scenarios().forEach(scenario -> dest.addAll(scenario.toTxt()));
        dest.add(SCENARIO_HEADER);
        dest.add(LINE);
    }

    public File getFile() {
        return file;
    }

}
