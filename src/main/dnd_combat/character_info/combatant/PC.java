package character_info.combatant;

import character_info.AbilityModifier;
import character_info.Stats;
import damage_implements.Spell;
import damage_implements.Weapon;
import txt_input.Key;
import util.TxtReader;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Objects;

import static txt_input.Key.*;

public class PC extends Combatant {

    private final Stats stats;
    private final List<Weapon> weapons;
    private final List<Spell> spells;

    public PC(String name, int hpMax, int armorClass,
              Stats stats, List<Weapon> weapons, List<Spell> spells
    ) {
        super(name, hpMax, armorClass);
        this.stats = stats;
        this.weapons = weapons;
        this.spells = spells;
    }

    @SuppressWarnings("unchecked")
    public PC(EnumMap<Key, Object> values) {
        super(
                (String) values.get(NAME),
                TxtReader.getHp((String) values.get(HP)),
                (int) values.get(AC)
        );
        this.hpCurrent = TxtReader.getHpCur((String) values.get(HP));
        this.stats = (Stats) values.get(STATS);

        List<Weapon> weaponTemp = (List<Weapon>) values.get(WEAPONS);
        this.weapons = Objects.requireNonNullElseGet(weaponTemp, ArrayList::new);

        List<Spell> spellTemp = (List<Spell>) values.get(SPELLS);
        this.spells = Objects.requireNonNullElseGet(spellTemp, ArrayList::new);
    }

    public int mod(AbilityModifier stat) {
        return stats.mod(stat);
    }

    public int attackBonus(Weapon weapon) {
        return stats.prof() + stats.mod(weapon.stat());
    }

    public int spellAttackBonus() {
        return stats.spellAttackBonus();
    }

    public int saveDc() {
        return stats.saveDc();
    }

    public List<Weapon> weapons() {
        return weapons;
    }

    public List<Spell> spells() {
        return spells;
    }

    public Stats stats() {
        return stats;
    }

    public void levelUp() {
        int hpIncrement = stats.levelUp();
        hpMax += hpIncrement;
    }

    /**
     * @return the lines of text for this combatant to be logged in a .txt file for its party.
     */
    public ArrayList<String> toTxt() {
        ArrayList<String> txt = new ArrayList<>();
        txt.add(".party");
        txt.add("name: " + name);
        txt.add("hp: " + hpCurrent + "/" + hpMax);
        txt.add("ac: " + armorClass);
        txt.add("level: " + stats.level());
        txt.add("class: " + stats.class5e());
        txt.add(stats.toString());

        if (!weapons.isEmpty()) {
            txt.add("weapons: " + weapons);
        }
        if (!spells.isEmpty()) {
            txt.add("spells: " + spells);
        }

        txt.add("");
        return txt;
    }
}
