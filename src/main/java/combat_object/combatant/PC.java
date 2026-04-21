package combat_object.combatant;

import __main.exception.InvalidParameterException;
import combat_object.combatant.info.Stats;
import combat_object.damage_implements.Spell;
import combat_object.damage_implements.Weapon;
import format.ColorStyles;
import input.Key;
import util.TxtReader;

import java.awt.*;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Objects;

import static input.Key.*;

@lombok.experimental.SuperBuilder
public class PC extends Combatant {

    public static PC create(String name, int hpMax, int armorClass, Stats stats, List<Weapon> weapons, List<Spell> spells) {
        return PC.builder()
                .name(name)
                .maxHp(hpMax)
                .hp(hpMax)
                .armorClass(armorClass)
                .isEnemy(false)
                .stats(stats)
                .weapons(new ArrayList<>(Objects.requireNonNullElse(weapons, List.of())))
                .spells(new ArrayList<>(Objects.requireNonNullElse(spells, List.of())))
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
        if (!weapons.isEmpty())
            txt.add("weapons: " + weapons);
        if (!spells.isEmpty())
            txt.add("spells: " + spells);
        txt.add("");
        return txt;
    }

    @SuppressWarnings("unchecked")
    public static PC from(EnumMap<Key, Object> params) {
        params.forEach((key, value) -> {
            if (!key.isValid(value)) throw new InvalidParameterException("CombatObject$PC", key, value);
        });

        int maxHp = TxtReader.getHp((String) params.get(HP));
        int hp = TxtReader.getHpCur((String) params.get(HP));

        if (hp > maxHp) throw new InvalidParameterException("PC", "hp", hp, "hp <= hpMax");

        return PC.builder()
                .name((String) params.get(NAME))
                .maxHp(maxHp)
                .hp(hp)
                .isEnemy(false)
                .armorClass((int) params.get(AC))
                .stats(Stats.from(params.get(STATS), params.get(CLASS), params.get(LEVEL)))
                .weapons(new ArrayList<>(Objects.requireNonNullElse((List<Weapon>) params.get(WEAPONS), List.of())))
                .spells(new ArrayList<>(Objects.requireNonNullElse((List<Spell>) params.get(SPELLS), List.of())))
                .build();
    }

}
