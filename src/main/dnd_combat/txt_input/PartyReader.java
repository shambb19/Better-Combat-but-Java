package txt_input;

import character_info.*;
import character_info.combatant.Combatant;
import character_info.combatant.PC;
import damage_implements.Spell;
import damage_implements.Weapon;

import java.util.ArrayList;
import java.util.Objects;

import static util.TxtReader.*;

public class PartyReader {

    private final Combatant combatant;

    public PartyReader(ArrayList<String> partyMember) {
        partyMember.replaceAll(String::trim);
        combatant = decodeAndGetCombatant(partyMember);
    }

    public Combatant get() {
        return combatant;
    }

    private Combatant decodeAndGetCombatant(ArrayList<String> lines) {
        if (lines.getFirst().equals(".party")) {
            lines.removeFirst();
        }

        String name = "name";
        int hp = 20, hpCur = -1, ac = 10;
        Stats stats = null;
        int level = 1;
        Class5e class5e = null;
        ArrayList<Weapon> weapons = new ArrayList<>();
        ArrayList<Spell> spells = new ArrayList<>();

        while (!lines.isEmpty()) {
            String identifier = identifier(lines.getFirst());
            String value = withoutIdentifier(lines.removeFirst());

            switch (identifier) {
                case "name" -> name = value;
                case "hp" -> {
                    hp = getHp(value);
                    hpCur = getHpCur(value);
                }
                case "ac" -> ac = num(value);
                case "level" -> level = num(value);
                case "class" -> class5e = Class5e.withName(value);
                case "stats" -> stats = getStats(value, level, class5e);
                case "weapons" -> addWeapons(weapons, value);
                case "spells" -> addSpells(spells, value);
            }
        }
        Combatant combatant = new PC(name, hp, ac, stats, weapons, spells);
        if (hpCur >= 0) {
            combatant.setHealth(hpCur);
        }
        return combatant;
    }

    private Stats getStats(String statLine, int level, Class5e characterClass) {
        Stats stats = new Stats(characterClass, level);

        String[] stat = statLine.split("/");
        for (String string : stat) {
            String statName = string.substring(0, 3);
            String value = string.substring(3);

            stats.put(Objects.requireNonNull(Stat.get(statName)), Integer.parseInt(value));
        }
        return stats;
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
            }
        }
        host.removeIf(Objects::isNull);
    }

    private int num(String value) {
        return Integer.parseInt(value);
    }

}
