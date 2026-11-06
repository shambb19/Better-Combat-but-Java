package combat_object.implement;

import combat_object.CombatObject;
import combat_object.combatant.info.AbilityModifier;
import input.CodeWriter;
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

    /**
     * Returns the DnD-format damage String for this Implement (i.e. 1d4, 2d8),
     * or halved if the given boolean is true. Manual Implements return a blank
     * String
     * @param isHalfDamage whether the spell is dealing half damage in the context
     *                     to which the String is being sent
     * @return the determined String
     */
    public String damageString(boolean isHalfDamage) {
        if (isHalfDamage) {
            return new Roll(roll.numDice() / 2, roll.dieSize()).toString();
        }
        return StringUtil.stringIfElseBlank(roll.toString(), !isManual);
    }

    /**
     * Returns whether the given Effect equals the effect dealt by this Implement
     * @param o the Effect to be compared to the Implement's effect
     * @return true if the effects match, else false
     */
    public boolean effectEquals(Effect o) {
        return false;
    }

    // todo implement
    public CodeWriter toTxt() {
        return null;
    }

    @Override
    public String toString() {
        return name;
    }

    /**
     * Returns a singleton list of the header item for the given Implement subclass.
     * @param type the subclass of Implement for which the header is requested
     * @return the singleton list of the Implement subclass' header
     */
    public static List<? extends Implement> createHeader(Class<? extends Implement> type) {
        return List.of(Map.of(
                Weapon.class, Weapon.HEADER,
                Spell.class, Spell.HEADER,
                Gun.class, Gun.HEADER
        ).get(type));
    }

    /**
     * Returns the list of static manual versions of the given Implement subclass.
     * The list is normally a singleton list of a manual version of the Implement,
     * except Spell, which has individual manual items for Spells that require a
     * hit roll vs a save roll
     * @param type the subclass of Implement for which manual entries are requested
     * @return the Implement subclass' manual entry list
     */
    public static List<? extends Implement> getManualEntries(Class<? extends Implement> type) {
        return Map.of(
                Weapon.class, List.of(Weapon.MANUAL),
                Spell.class, List.of(Spell.HIT_MANUAL, Spell.SAVE_MANUAL),
                Gun.class, List.of(Gun.MANUAL)
        ).get(type);
    }

}