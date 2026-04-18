package combat_object.combatant.info;

import lombok.*;
import lombok.experimental.*;
import util.Locators;
import util.StringUtils;
import util.TxtReader;

import java.util.ArrayList;
import java.util.HashMap;

import static combat_object.combatant.info.AbilityModifier.*;
import static util.TxtReader.listTextAsArray;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
@ExtensionMethod(StringUtils.class)
public class Stats {

    final Class5e class5e;
    final HashMap<AbilityModifier, Integer> stats = new HashMap<>();
    int level;
    int proficiencyBonus;

    public Stats(Class5e class5e, int level) {
        this.class5e = class5e;
        this.level = level;

        for (AbilityModifier stat : AbilityModifier.values())
            stats.put(stat, 10);

        stats.put(OPTION, Math.max(stats.get(STR), stats.get(DEX)));

        updateProficiency();
    }

    private void updateProficiency() {
        proficiencyBonus = (level - 1) / 4 + 2;
    }

    public static Stats defaultStats() {
        Stats stats = new Stats(null, 1);
        stats.proficiencyBonus = 0;

        return stats;
    }

    /**
     * Sets the stat param to the value param.
     */
    public void put(AbilityModifier stat, int value) {
        stats.put(stat, value);
    }

    /**
     * @return The value of the attacker's attack bonus for spells.
     */
    public int spellAttackBonus() {
        return mod(class5e.getSpellMod()) + proficiencyBonus;
    }

    /**
     * @return the modifier for the stat param using the (stat - 10)/2 rounded down
     * calculations, and adding proficiency bonus if present.
     */
    public int mod(AbilityModifier stat) {
        return (stats.get(stat) - 10) / 2;
    }

    public int saveDc() {
        return 8 + mod(class5e.getSpellMod()) + proficiencyBonus;
    }

    public int levelUp() {
        level++;
        updateProficiency();
        return getClass5e().getHpIncrement();
    }

    public ArrayList<String> toTxt() {
        ArrayList<String> txt = new ArrayList<>();
        txt.add("level: " + level);
        txt.add("class: " + class5e);
        txt.add(statLine());
        return txt;
    }

    private String statLine() {
        StringBuilder string = new StringBuilder("stats: [");
        for (AbilityModifier stat : AbilityModifier.values())
            if (!stat.equals(OPTION))
                string.append(stat.name().toUpperCase()).append(": ").append(get(stat)).append(", ");

        return string.delete(string.length() - 2, string.length()).append("]").toString();
    }

    /**
     * @return the raw stat for the given param
     */
    public int get(@NonNull AbilityModifier stat) {
        return stats.get(stat);
    }

    public void increment(@NonNull AbilityModifier stat) {
        int newVal = get(stat) + 1;
        stats.put(stat, newVal);
    }

    public static Stats from(Object statsObj, Object class5eObj, Object levelObj) {
        Class5e class5e = (Class5e) class5eObj;
        int level = (int) levelObj;

        Stats stats = new Stats(class5e, level);

        for (String s : listTextAsArray((String) statsObj)) {
            String key = TxtReader.key(s);
            AbilityModifier stat = Locators.enumNameSearch(key, AbilityModifier.class);

            int value = TxtReader.value(s).toInt();

            stats.put(stat, value);
        }

        return stats;
    }

}