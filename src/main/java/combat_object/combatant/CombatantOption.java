package combat_object.combatant;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import swing.ColorStyles;

import java.awt.*;

@AllArgsConstructor @FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE) @Getter
public enum CombatantOption {

    PARTY(ColorStyles.PARTY, ".party", "Party Member"),
    ALLIES(ColorStyles.FRIENDLY, ".npc", "Friendly NPC"),
    ENEMIES(ColorStyles.ENEMY, ".enemy", "Enemy Combatant");

    Color color;
    String codeHeader;
    String expandedName;

    /**
     * Returns whether this CombatantOption equals CombatantOption.ENEMIES
     * @return true if this instance is ENEMIES, else false
     */
    public boolean isEnemies() {
        return this == ENEMIES;
    }

    @Override public String toString() {
        return name().charAt(0) + name().substring(1).toLowerCase();
    }

    /**
     * Returns the CombatantObject constant associated with the Combatant
     * subclass of which the provided instance is
     * @param c the Combatant whose subclass is to be checked
     * @return the CombatantObject constant associated with the provided
     * Combatant
     */
    public static CombatantOption fromCombatant(Combatant c) {
        if (!(c instanceof NPC npc)) return PARTY;
        return getNpcType(npc.isEnemy);
    }

    /**
     * Returns the CombatantOption associated with the given boolean,
     * with true returning ENEMIES and false returning ALLIES
     * @param isEnemies whether the enemies constant is requested
     * @return ENEMIES if isEnemies=true, else ALLIES
     */
    public static CombatantOption getNpcType(boolean isEnemies) {
        return isEnemies ? ENEMIES : ALLIES;
    }

}
