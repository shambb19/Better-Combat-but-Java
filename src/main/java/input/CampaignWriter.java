package input;

import _global_list.Combatants;
import _global_list.Scenarios;
import combat_object.combatant.Combatant;
import combat_object.combatant.PC;
import combat_object.scenario.Scenario;
import exception.FileProcessErrorException;
import lombok.*;
import lombok.experimental.*;
import util.Filterable;
import util.Message;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static config.Config.*;

@ExtensionMethod(util.Filterable.class)
@RequiredArgsConstructor
public class CampaignWriter {

    private final Set<Combatant> friendlySource, enemySource;
    private final Set<Scenario> scenarioSource;

    private ArrayList<String> code = null;

    public static CampaignWriter ofFullCampaign() {
        return new CampaignWriter(Combatants.getFriendlies(), Combatants.getEnemies(), Scenarios.toSet());
    }

    public ArrayList<String> getCode() {
        code = new ArrayList<>();
        writeConfig();
        writeCombatants();
        writeScenarios();
        return code;
    }

    @SneakyThrows
    public void download(String fileName, boolean isSentToDownloads) {
        try {
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
            for (String line : getCode()) writer.write(line + System.lineSeparator());
        } catch (IOException | NullPointerException e) {
            throw new FileProcessErrorException("CampaignWriter.getUrl: Could not create/save file", Message.WRITE_ERROR);
        }
    }

    private void writeConfig() {
        code.addAll(List.of(CONFIG_OPEN_TOKEN, getRuleset().getConfigLine(), CONFIG_CLOSE_TOKEN));
    }

    private void writeCombatants() {
        for (Combatant c : friendlySource) if (c instanceof PC || c.isAlive()) code.addAll(c.toTxt());
        Filterable.of(enemySource).filteredBy(Combatant::isConscious).toList().forEach(e -> code.addAll(e.toTxt()));
    }

    private void writeScenarios() {
        scenarioSource.forEach(scenario -> code.addAll(scenario.toTxt()));
    }

}