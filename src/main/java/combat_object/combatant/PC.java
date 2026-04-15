package combat_object.combatant;

import combat_object.CombatObject;
import combat_object.damage_implements.Spell;
import combat_object.damage_implements.Weapon;
import lombok.experimental.*;
import txt_input.Key;
import util.TxtReader;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Objects;

import static txt_input.Key.*;

@SuperBuilder
public class PC extends Combatant implements CombatObject {

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
        int hpIncrement = stats.levelUp();
        maxHp += hpIncrement;
    }

    /**
     * @return the lines of text for this combatant to be logged in a .txt file for its party.
     */
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
        return PC.builder()
                .name((String) params.get(NAME))
                .maxHp(TxtReader.getHp((String) params.get(HP)))
                .hp(TxtReader.getHpCur((String) params.get(HP)))
                .isEnemy(false)
                .armorClass((int) params.get(AC))
                .stats(Stats.from(params.get(STATS), params.get(CLASS), params.get(LEVEL)))
                .weapons(new ArrayList<>(Objects.requireNonNullElse((List<Weapon>) params.get(WEAPONS), List.of())))
                .spells(new ArrayList<>(Objects.requireNonNullElse((List<Spell>) params.get(SPELLS), List.of())))
                .build();
    }

}
