package combat_object.combatant;

import combat_object.combatant.info.Stats;
import combat_object.implement.Implement;
import combat_object.implement.Spell;
import combat_object.implement.Weapon;
import config.Config;
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

    public static PC create(String name, int hpMax, int armorClass, Stats stats, List<Weapon> weapons, List<Spell> spells) {
        return PC.builder()
                .name(name)
                .maxHp(hpMax)
                .hp(hpMax)
                .armorClass(armorClass)
                .isEnemy(false)
                .stats(stats)
                .implementList(Stream.of(weapons, spells)
                        .flatMap(Collection::stream)
                        .collect(Collectors.toCollection(ArrayList::new)))
                .build();
    }

    public void levelUp() {
        maxHp += stats.levelUp();
    }

    public Color getCombatantColor() {
        return ColorStyles.PARTY;
    }

    @Override
    public ArrayList<String> toTxt() {
        ArrayList<String> txt = super.toTxt();

        txt.addAll(stats.toTxt());

        List<Weapon> weapons = getImplements(Weapon.class);
        if (!weapons.isEmpty())
            txt.add("weapons: " + weapons);
        List<Spell> spells = getImplements(Spell.class);
        if (!spells.isEmpty())
            txt.add("spells: " + spells);
        txt.add("");
        return txt;
    }

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
                        .flatMap(Collection::stream)
                        .collect(Collectors.toCollection(ArrayList::new)))
                .build();
    }

}
