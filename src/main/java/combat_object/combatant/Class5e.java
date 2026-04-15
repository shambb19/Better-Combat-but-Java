package combat_object.combatant;

import lombok.*;
import lombok.experimental.*;
import org.apache.commons.lang3.text.WordUtils;

import static combat_object.combatant.AbilityModifier.*;

@Getter
@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
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
    @SuppressWarnings("deprecation")
    public String toString() {
        return WordUtils.capitalizeFully(name());
    }

}
