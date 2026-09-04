package combat_object.implement;

import combat_object.CombatObject;
import combat_object.combatant.info.AbilityModifier;
import lombok.*;
import lombok.experimental.*;
import util.Roll;
import util.StringUtil;

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

}