package combat_object.damage_implements;

import combat_object.combatant.info.AbilityModifier;
import lombok.*;
import lombok.experimental.*;

@Getter
@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PROTECTED)
public class Implement implements combat_object.CombatObject {

    String name;
    int numDice, dieSize;
    AbilityModifier stat;
    boolean isManual;

    public String damageString() {
        if (isManual()) return "";
        return numDice + "d" + dieSize;
    }

    public boolean effectEquals(Effect o) {
        return false;
    }

    @Override
    public String toString() {
        return name;
    }

}