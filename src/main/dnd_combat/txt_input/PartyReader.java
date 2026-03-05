package txt_input;

import character_info.Combatant;
import character_info.Stats;
import damage_implements.Spell;
import damage_implements.Weapon;
import exception.UploadTextError;

import java.util.ArrayList;
import java.util.Objects;

import static util.Reader.identifier;
import static util.Reader.withoutIdentifier;

public class PartyReader {

    private final Combatant combatant;

    public PartyReader(ArrayList<String> partyMember) {
        partyMember.replaceAll(String::trim);
        combatant = decodeAndGetCombatant(partyMember);
    }

    public Combatant get() {
        return combatant;
    }

    // Note that stat reading requires that proficiency and spell modifiers are entered before stats
    // in the document. Ideally, fix this later.
    private Combatant decodeAndGetCombatant(ArrayList<String> lines) {
        if (lines.getFirst().equals("{party")) {
            lines.removeFirst();
        }

        String name = "name";
        int hp = 20, hpCur = -1, ac = 10;
        Stats stats = null;
        int level = 1;
        Stats.stat spellMod = null;
        ArrayList<Weapon> weapons = new ArrayList<>();
        ArrayList<Spell> spells = new ArrayList<>();

        while (!lines.isEmpty()) {
            String identifier = identifier(lines.getFirst());
            String value = withoutIdentifier(lines.removeFirst());

            switch (identifier) {
                case "name" -> name = value;
                case "hp" -> hp = num(value);
                case "hpCur" -> hpCur = num(value);
                case "ac" -> ac = num(value);
                case "level" -> level = num(value);
                case "spellMod" -> spellMod = mod(value);
                case "stats" -> stats = getStats(value, level, spellMod);
                case "weapons" -> addWeapons(weapons, value);
                case "spells" -> addSpells(spells, value);
            }
        }
        Combatant combatant = new Combatant(name, hp, ac, stats, weapons, spells);
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
        host.removeIf(Objects::isNull);
    }

    private void addSpells(ArrayList<Spell> host, String line) {
        String[] spells = line.split("/");
        for (String spell : spells) {
            Spell newSpell = Spell.get(spell);
            if (newSpell != null) {
                host.add(newSpell);
            } else {
                System.out.println(spell + " null");
            }
        }
        host.removeIf(Objects::isNull);
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

}
