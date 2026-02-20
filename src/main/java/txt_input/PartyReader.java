package txt_input;

import combatants.Combatant;
import combatants.Stats;
import damage.Spell;
import damage.Weapon;
import exception.UploadTextError;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;

import static util.Reader.identifier;
import static util.Reader.withoutIdentifier;

public class PartyReader {

    private final ArrayList<String> allLines;
    private final ArrayList<String> currentRead;
    private final ArrayList<Combatant> readCombatants;

    private boolean isHpCurRecoded;

    public PartyReader(File party) throws IOException {
        allLines = new ArrayList<>(Files.readAllLines(party.toPath()));
        allLines.replaceAll(String::trim);

        currentRead = new ArrayList<>();
        readCombatants = new ArrayList<>();

        while (!allLines.isEmpty()) {
            readCombatants.add(stripAndGetCombatant());
        }
    }

    public ArrayList<Combatant> getCombatants() {
        return readCombatants;
    }

    private Combatant stripAndGetCombatant() {
        if (allLines.getFirst().equals("{")) {
            allLines.removeFirst();
        } else {
            return null;
        }

        while (!allLines.getFirst().equals("}")) {
            currentRead.add(allLines.removeFirst());
        }
        allLines.removeFirst();

        return getCurrentRead();
    }

    // Note that stat reading requires that proficiency and spell modifiers are entered before stats
    // in the document. Ideally, fix this later.
    private Combatant getCurrentRead() {
        String name = "name";
        int hp = 20, hpCur = -1, ac = 10;
        Stats stats = null;
        int level = 1;
        Stats.stat spellMod = null;
        ArrayList<Weapon> weapons = new ArrayList<>();
        ArrayList<Spell> spells = new ArrayList<>();

        while (!currentRead.isEmpty()) {
            String identifier = identifier(currentRead.getFirst());
            String value = withoutIdentifier(currentRead.removeFirst());

            switch (identifier) {
                case "name" -> name = value;
                case "hp" -> hp = num(value);
                case "hpCur" -> {
                    hpCur = num(value);
                    isHpCurRecoded = true;
                }
                case "ac" -> ac = num(value);
                case "level" -> level = num(value);
                case "spellMod" -> spellMod = mod(value);
                case "stats" -> stats = getStats(value, level, spellMod);
                case "weapons" -> addWeapons(weapons, value);
                case "spells" -> addSpells(spells, value);
            }
        }
        if (weapons.isEmpty()) {
            weapons = null;
        }
        if (spells.isEmpty()) {
            spells = null;
        }
        Combatant combatant = new Combatant(name, hp, ac, false, stats, weapons, spells);
        if (hpCur >= 0) {
            combatant.setHealth(hpCur);
        }
        return combatant;
    }

    private Stats getStats(String statLine, int prof, Stats.stat spellMod) {
        Stats stats = new Stats(prof, spellMod);

        String[] stat = statLine.split("/");
        for (String string : stat) {
            String statName = string.substring(0, 3);
            String value = string.substring(string.indexOf("(") + 1, string.indexOf(")"));
            boolean statProf = false;
            if (value.charAt(value.length() - 1) == '+') {
                statProf = true;
                value = value.substring(0, value.length() - 1);
            }
            stats.put(mod(statName), Integer.parseInt(value), statProf);
        }
        try {
            return stats;
        } catch (Exception ignored) {
            throw new UploadTextError(UploadTextError.cause.STATS);
        }
    }

    private void addWeapons(ArrayList<Weapon> host, String line) {
        String[] weapons = line.split("/");
        for (String weapon : weapons) {
            host.add(Weapon.get(weapon));
        }
    }

    private void addSpells(ArrayList<Spell> host, String line) {
        String[] spells = line.split("/");
        for (String spell : spells) {
            Spell newSpell = Spell.get(spell);
            if (newSpell != null) {
                host.add(Spell.get(spell));
            } else {
                System.out.println(spell + " null");
            }
        }
    }

    private Stats.stat mod(String key) {
        return switch (key) {
            case "str" -> Stats.stat.STR;
            case "dex" -> Stats.stat.DEX;
            case "con" -> Stats.stat.CON;
            case "int" -> Stats.stat.INT;
            case "wis" -> Stats.stat.WIS;
            case "cha" -> Stats.stat.CHA;
            default -> throw new UploadTextError(UploadTextError.cause.STATS);
        };
    }

    private int num(String value) {
        return Integer.parseInt(value);
    }

    public boolean isHpRecorded() {
        return isHpCurRecoded;
    }

}
