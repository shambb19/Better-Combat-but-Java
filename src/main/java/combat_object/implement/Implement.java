package combat_object.implement;

import combat_object.CombatObject;
import combat_object.combatant.info.AbilityModifier;
import lombok.*;
import lombok.experimental.*;

@EqualsAndHashCode(callSuper = true) @Getter
@Data @SuperBuilder
@FieldDefaults(makeFinal = true, level = AccessLevel.PROTECTED)
public abstract class Implement extends CombatObject {

    int numDice, dieSize;
    AbilityModifier stat;
    boolean isManual;

    public String damageString() {
        return util.StringUtil.stringIfElseBlank(numDice + "d" + dieSize, !isManual);
    }

    public boolean effectEquals(Effect o) {
        return false;
    }

    @Override
    public String toString() {
        return name;
    }

}