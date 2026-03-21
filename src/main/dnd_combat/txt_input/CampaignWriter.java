package txt_input;

import _global_list.Combatants;
import _global_list.Scenarios;
import character_info.combatant.Combatant;
import character_info.combatant.PC;
import encounter_info.Scenario;
import util.Message;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CampaignWriter {

    private final List<Combatant> friendlySource;
    private final List<Combatant> enemySource;
    private final List<Scenario> scenarioSource;

    private ArrayList<String> code;

    public CampaignWriter() {

        friendlySource = Combatants.toList().stream().filter(combatant -> !combatant.isEnemy()).toList();
        enemySource = Combatants.toList().stream().filter(Combatant::isEnemy).toList();
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

    public URL getUrl(String fileName, boolean sendToDownloads) {
        try {
            File file;
            if (sendToDownloads) {
                int numRand = (int) (Math.random() * 1000);
                file = new File(
                        new File(System.getProperty("user.home"), "Downloads"),
                        fileName + LocalDate.now() + " " + numRand + ".txt"
                );
            } else {
                file = File.createTempFile("campaignParam", ".txt");
                file.deleteOnExit();
            }

            try (FileWriter writer = new FileWriter(file)) {
                getCode().forEach(line -> {
                    try {
                        writer.write(line + "\n");
                    } catch (IOException e) {
                        Logger.getAnonymousLogger().log(
                                Level.SEVERE, "getURL in CampaignWriter: would not write to File", e
                        );
                    }
                });
                return file.toURI().toURL();
            } catch (IOException e) {
                Message.throwFileDownloadError(null, e);
                return null;
            }

        } catch (IOException e) {
            Message.throwFileDownloadError(null, e);
            Logger.getAnonymousLogger().log(
                    Level.SEVERE, "getUrl in CampaignWriter: could not create or save file"
            );
            return null;
        }
    }

    private void writeCombatants() {
        friendlySource.forEach(c -> {
            if (c instanceof PC || c.lifeStatus().isAlive()) {
                code.addAll(c.toTxt());
            }
        });
        enemySource.forEach(c -> {
            if (c.lifeStatus().isConscious()) {
                code.addAll(c.toTxt());
            }
        });
    }

    private void writeScenarios() {
        scenarioSource.forEach(scenario -> code.addAll(scenario.toTxt()));
    }

}
