package combat_object.combatant.info;

import lombok.*;
import util.StringUtils;

import static combat_object.combatant.info.AbilityModifier.*;

@Getter
@AllArgsConstructor
@lombok.experimental.FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public enum Class5e {

    BARBARIAN(null, 7),
    BARD(CHA, 5),
    CLERIC(WIS, 5),
    DRUID(WIS, 5),
    FIGHTER(null, 6),
    MONK(null, 5),
    PALADIN(CHA, 6),
    RANGER(WIS, 6),
    ROGUE(null, 5),
    SORCERER(CHA, 4),
    WARLOCK(CHA, 5),
    WIZARD(INT, 4);

    AbilityModifier spellMod;
    int hpIncrement;

    @Override
    public String toString() {
        return StringUtils.capitalized(name());
    }

}
