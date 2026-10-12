package combat_object.implement;

import combat_object.CombatObject;
import combat_object.combatant.info.AbilityModifier;
import lombok.*;
import lombok.experimental.*;
import util.Roll;
import util.StringUtil;

import java.util.List;
import java.util.Map;

@EqualsAndHashCode(callSuper = true) @Getter @SuperBuilder
@FieldDefaults(makeFinal = true, level = AccessLevel.PROTECTED)
public abstract class Implement extends CombatObject {

    Roll roll;
    AbilityModifier stat;
    boolean isManual;

    public String damageString() {
        return StringUtil.stringIfElseBlank(roll.toString(), !isManual);
    }

    public String damageString(boolean isHalfDamage) {
        if (isHalfDamage) {
            return new Roll(roll.numDice() / 2, roll.dieSize()).toString();
        }
        return damageString();
    }

    public boolean effectEquals(Effect o) {
        return false;
    }

    @Override
    public String toString() {
        return name;
    }

    public static List<? extends Implement> createHeader(Class<? extends Implement> type) {
        return List.of(Map.of(
                Weapon.class, Weapon.HEADER,
                Spell.class, Spell.HEADER,
                Gun.class, Gun.HEADER
        ).get(type));
    }

    public static List<? extends Implement> getManualEntries(Class<? extends Implement> type) {
        return Map.of(
                Weapon.class, List.of(Weapon.MANUAL),
                Spell.class, List.of(Spell.HIT_MANUAL, Spell.SAVE_MANUAL),
                Gun.class, List.of(Gun.MANUAL)
        ).get(type);
    }

}