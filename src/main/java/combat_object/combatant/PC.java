package combat_object.combatant;

import combat_object.combatant.info.Stats;
import combat_object.implement.Implement;
import combat_object.implement.Spell;
import combat_object.implement.Weapon;
import config.Config;
import input.CodeWriter;
import input.TextReader;
import input.syntax.Key;
import input.syntax.Tag;
import lombok.experimental.*;
import swing.ColorStyles;

import java.awt.*;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static input.syntax.Key.*;

@SuperBuilder
@ExtensionMethod(TextReader.class)
public class PC extends Combatant {

    /**
     * Creates a PC object from the given parameters
     * @param name the pc's name
     * @param hpMax the pc's maximum hp
     * @param armorClass the pc's armor class
     * @param stats the pc's ability stats
     * @param weapons the weapons this pc has in their possession
     * @param spells the spells this pc knows
     * @return the new PC object
     */
    public static PC create(String name, int hpMax, int armorClass, Stats stats, List<Weapon> weapons, List<Spell> spells) {
        return PC.builder()
                .name(name)
                .maxHp(hpMax)
                .hp(hpMax)
                .armorClass(armorClass)
                .isEnemy(false)
                .stats(stats)
                .implementList(Stream.of(weapons, spells)
                        .flatMap(Collection::stream).collect(Collectors.toSet()))
                .build();
    }

    /**
     * Levels up this pc and increases its maximum hp by the
     * amount determined by its character class
     */
    public void levelUp() {
        maxHp += stats.levelUp();
    }

    public Color getCombatantColor() {
        return ColorStyles.PARTY;
    }

    /**
     * Returns the lines of code associated with this PC object
     * @return the .txt code lines
     */
    @Override
    public CodeWriter toTxt() {
        List<Weapon> weapons = getImplements(Weapon.class);
        List<Spell> spells = getImplements(Spell.class);

        return super.toTxt()
                .withLines(stats.toTxt())
                .withLineFromNonEmptyList("weapons", weapons)
                .withLineFromNonEmptyList("spells", spells);
    }

    /**
     * Returns a PC object from the given parameters
     * @param params the PC instance variables, encoded as Objects
     *               and decoded in the Key class
     * @param tags the tags the new PC has
     * @return the new PC object
     */
    @SuppressWarnings("unchecked")
    public static PC from(EnumMap<Key, Object> params, Set<Tag> tags) {
        validateAll(params, "PC");
        Config.getRuleset().validateCombatant(params, tags);

        return PC.builder()
                .name((String) params.get(NAME))
                .maxHp(params.get(HP).getHp(true))
                .hp(params.get(HP).getHp(false))
                .isEnemy(false)
                .isArmored(tags.contains(Tag.ARMORED))
                .armorClass((int) params.get(AC))
                .stats(Stats.from(params.get(STATS), params.get(CLASS), params.get(LEVEL)))
                .implementList(Stream.of(WEAPONS, SPELLS, GUNS)
                        .map(key -> (List<? extends Implement>) params.getOrDefault(key, List.of()))
                        .flatMap(Collection::stream).collect(Collectors.toSet()))
                .build();
    }

}
