package combat_object.combatant.info;

import input.CodeWriter;
import input.TextReader;
import lombok.*;
import lombok.experimental.*;
import util.Locators;
import util.StringUtil;

import java.util.Arrays;
import java.util.HashMap;

import static combat_object.combatant.info.AbilityModifier.*;
import static input.TextReader.listTextAsArray;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
@ExtensionMethod(StringUtil.class)
public class Stats {

    final Class5e class5e;
    final HashMap<AbilityModifier, Integer> stats = new HashMap<>();
    int level;
    int proficiencyBonus;

    private Stats(Class5e class5e, int level) {
        this.class5e = class5e;
        this.level = level;

        for (AbilityModifier stat : stats()) stats.put(stat, 10);

        updateProficiency();
    }

    /**
     * Manually adjusts the stats proficiency bonus based on the stats level.
     */
    private void updateProficiency() {
        if (class5e.equals(Class5e.NPC)) proficiencyBonus = 0;
        else proficiencyBonus = (level - 1) / 4 + 2;
    }

    /**
     * Returns a default Stats object for NPCs with class NPC and level 1
     * @return the new Stats object
     */
    public static Stats defaultStats() {
        return new Stats(Class5e.NPC, 1);
    }

    /**
     * @return the raw stat for the given param
     */
    public int get(@NonNull AbilityModifier stat) {
        return stats.get(stat);
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
        if (class5e.getSpellMod() == null) return 0;

        return mod(class5e.getSpellMod()) + proficiencyBonus;
    }

    /**
     * @return the modifier for the stat param using the (stat - 10)/2 rounded down
     * calculations, and adding proficiency bonus if present.
     */
    public int mod(AbilityModifier stat) {
        return Math.floorDiv(get(stat) - 10, 2);
    }

    /**
     * Returns the associated combatant's spell save DC
     * @return the value of the save DC
     */
    public int saveDc() {
        if (class5e.getSpellMod() == null) return 0;

        return 8 + mod(class5e.getSpellMod()) + proficiencyBonus;
    }

    /**
     * Increments the level by one and adjusts the proficiency accordingly
     * @return the new level
     */
    public int levelUp() {
        level++;
        updateProficiency();
        return getClass5e().getHpIncrement();
    }

    /**
     * Returns the lines of code associated with this Stats object
     * @return the .txt code lines
     */
    public CodeWriter toTxt() {
        return new CodeWriter(null)
                .withLine("level", level)
                .withLine("class", class5e)
                .withLine(statLine());
    }

    /**
     * Returns the code line for the stats ability stats
     * @return the stats code line
     */
    private String statLine() {
        String prefix = "stats" + TextReader.KEY_DELIMITER + "[";
        StringBuilder string = new StringBuilder(prefix);
        Arrays.stream(stats()).map(s -> s.name() + TextReader.KEY_DELIMITER + get(s) + ", ").forEach(string::append);
        return string.delete(string.length() - 2, string.length()).append("]").toString();
    }

    /**
     * Returns a Stats object constructed from the given parameters
     * @param statsObj the stat code line (precondition: instanceof String)
     * @param class5eObj the provided 5e class (precondition: instanceof Class5e)
     * @param levelObj the provided player level (precondition: instanceof int)
     * @return the new Stats object
     */
    public static Stats from(Object statsObj, Object class5eObj, Object levelObj) {
        Class5e class5e = (Class5e) class5eObj;
        int level = (int) levelObj;

        Stats stats = new Stats(class5e, level);

        for (String s : listTextAsArray((String) statsObj)) {
            String key = TextReader.key(s);
            AbilityModifier stat = Locators.enumNameSearch(key, AbilityModifier.class);

            int value = TextReader.value(s).toInt();

            stats.put(stat, value);
        }

        stats.put(OPTION, Math.max(stats.get(STR), stats.get(DEX)));

        return stats;
    }

}