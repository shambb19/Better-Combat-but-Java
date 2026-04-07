package txt_input;

import _global_list.Combatants;
import _global_list.Scenarios;
import character_info.combatant.Combatant;
import character_info.combatant.PC;
import encounter_info.Scenario;
import util.Filter;
import util.Message;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class CampaignWriter {

    private final List<Combatant> friendlySource;
    private final List<Combatant> enemySource;
    private final List<Scenario> scenarioSource;

    private ArrayList<String> code;

    public CampaignWriter() {
        friendlySource = Filter.matchingCondition(Combatants.toList(), c -> !c.isEnemy());
        enemySource = Filter.matchingCondition(Combatants.toList(), Combatant::isEnemy);

        scenarioSource = Scenarios.toList();
    }

    public CampaignWriter(List<Combatant> friendlies, List<Combatant> enemies, List<Scenario> scenarios) {
        friendlySource = friendlies;
        enemySource = enemies;
        scenarioSource = scenarios;
    }


    public ArrayList<String> getCode() {
        code = new ArrayList<>();
        writeCombatants();
        writeScenarios();
        return code;
    }

    public URL getUrl(String fileName, boolean isSentToDownloads) {
        try {
            return getWrittenFile(fileName, isSentToDownloads);
        } catch (IOException e) {
            Logger.getAnonymousLogger().severe("getUrl in CampaignWriter: Could not create or save file");
            Message.throwFileDownloadError(null, e);
            return null;
        }
    }

    private URL getWrittenFile(String fileName, boolean isSentToDownloads) throws IOException {
        File file;
        if (isSentToDownloads) {
            int numRand = (int) (Math.random() * 1000);
            File directory = new File(System.getProperty("user.home"), "Downloads");

            file = new File(directory, fileName + LocalDate.now() + " " + numRand + ".txt");
        } else {
            file = File.createTempFile("campaignParam", ".txt");
            file.deleteOnExit();
        }

        writeTo(file);
        return file.toURI().toURL();
    }

    private void writeTo(File file) throws IOException {
        try (FileWriter writer = new FileWriter(file)) {
            for (String line : getCode())
                writer.write(line + System.lineSeparator());
        }
    }

    private void writeCombatants() {
        friendlySource.forEach(c -> {
            if (c instanceof PC || c.lifeStatus().isAlive())
                code.addAll(c.toTxt());
        });

        Filter.matchingCondition(enemySource, enemy -> enemy.lifeStatus().isConscious())
                .forEach(enemy -> code.addAll(enemy.toTxt()));
    }

    private void writeScenarios() {
        scenarioSource.forEach(scenario -> code.addAll(scenario.toTxt()));
    }

}
