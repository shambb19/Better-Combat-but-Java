package txt_input_2;

import _main.CombatMain;
import character_info.combatant.Combatant;
import character_info.combatant.PC;
import scenario_info.Scenario;
import util.Message;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;

public class CampaignWriter {

    private static final String COMBATANT_HEADER = "<Combatants>";
    private static final String SCENARIO_HEADER = "<Scenarios>";
    private static final String LINE = "";

    private final ArrayList<Combatant> friendlySource;
    private final ArrayList<Combatant> enemySource;
    private final ArrayList<Scenario> scenarioSource;

    private final File file;
    private ArrayList<String> code;

    public CampaignWriter() {
        int numRand = (int) (Math.random()*1000);
        file = new File(
                new File(System.getProperty("user.home"), "Downloads"),
                "campaign post encounter " + LocalDate.now() + " " + numRand + ".txt"
        );

        friendlySource = CombatMain.BATTLE.friendliesOriginal();
        enemySource = CombatMain.BATTLE.enemiesOriginal();
        scenarioSource = CombatMain.BATTLE.scenarios();
    }

    public CampaignWriter(String fileTitle,
                          ArrayList<Combatant> friendlies, ArrayList<Combatant> enemies, ArrayList<Scenario> scenarios) {
        int numRand = (int) (Math.random()*1000);
        file = new File(
                new File(System.getProperty("user.home"), "Downloads"),
                fileTitle + " " + LocalDate.now() + " " + numRand + ".txt"
        );

        friendlySource = friendlies;
        enemySource = enemies;
        scenarioSource = scenarios;
    }

    public CampaignWriter(ArrayList<Combatant> friendlies, ArrayList<Combatant> enemies, ArrayList<Scenario> scenarios) {
        file = null;

        friendlySource = friendlies;
        enemySource = enemies;
        scenarioSource = scenarios;
    }

    private void writeCombatants() {
        code.add(COMBATANT_HEADER);
        code.add(LINE);
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
        code.add(SCENARIO_HEADER);
        code.add(LINE);
        scenarioSource.forEach(scenario -> code.addAll(scenario.toTxt()));
    }

    public ArrayList<String> getCode() {
        code = new ArrayList<>();
        writeCombatants();
        writeScenarios();
        return code;
    }

    public File getFile() {
        code = new ArrayList<>();
        writeCombatants();
        writeScenarios();

        try (FileWriter writer = new FileWriter(file)) {
            code.forEach(line -> {
                try {
                    writer.write(line + "\n");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            return file;
        } catch (IOException e) {
            Message.throwFileDownloadError(null, e);
            return null;
        }
    }

}
