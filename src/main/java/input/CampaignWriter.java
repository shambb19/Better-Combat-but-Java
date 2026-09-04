package input;

import _global_list.Combatants;
import _global_list.Scenarios;
import combat_object.combatant.Combatant;
import combat_object.combatant.PC;
import combat_object.scenario.Scenario;
import lombok.*;
import lombok.experimental.*;
import util.Filterable;
import util.Message;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import static config.Config.*;

@ExtensionMethod(util.Filterable.class)
@RequiredArgsConstructor
public class CampaignWriter {

    private final List<Combatant> friendlySource, enemySource;
    private final List<Scenario> scenarioSource;

    private ArrayList<String> code = null;

    public static CampaignWriter ofFullCampaign() {
        return new CampaignWriter(Combatants.getFriendlies(), Combatants.getEnemies(), Scenarios.toList());
    }

    public ArrayList<String> getCode() {
        code = new ArrayList<>();
        writeConfig();
        writeCombatants();
        writeScenarios();
        return code;
    }

    public URL getUrl(String fileName, boolean isSentToDownloads) {
        try {
            return getWrittenFile(fileName, isSentToDownloads);
        } catch (IOException e) {
            Logger.getAnonymousLogger().severe("getUrl in CampaignWriter: Could not create or save file");
            Message.showFileErrorMessage(e, Message.WRITE_ERROR);
            return null;
        }
    }

    private URL getWrittenFile(String fileName, boolean isSentToDownloads) throws IOException {
        File file;
        if (isSentToDownloads) {
            File directory = new File(System.getProperty("user.home"), "Downloads");

            int numRand = (int) (Math.random() * 1000);
            file = new File(directory, fileName + LocalDate.now() + " " + numRand + ".txt");
        } else {
            file = File.createTempFile("campaignParam", ".txt");
            file.deleteOnExit();
        }

        @Cleanup FileWriter writer = new FileWriter(file);
        for (String line : getCode())
            writer.write(line + System.lineSeparator());

        return file.toURI().toURL();
    }

    private void writeConfig() {
        code.addAll(List.of(CONFIG_OPEN_TOKEN, getRuleset().getConfigLine(), CONFIG_CLOSE_TOKEN));
    }

    private void writeCombatants() {
        for (Combatant c : friendlySource) if (c instanceof PC || c.isAlive()) code.addAll(c.toTxt());
        Filterable.of(enemySource).filteredBy(Combatant::isConscious).forEach(e -> code.addAll(e.toTxt()));
    }

    private void writeScenarios() {
        scenarioSource.forEach(scenario -> code.addAll(scenario.toTxt()));
    }

}