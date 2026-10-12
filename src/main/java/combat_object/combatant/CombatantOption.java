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

    public boolean isEnemies() {
        return this == ENEMIES;
    }
    @Override public String toString() {
        return name().charAt(0) + name().substring(1).toLowerCase();
    }

    public static CombatantOption getNpcType(boolean isEnemies) {
        return isEnemies ? ENEMIES : ALLIES;
    }

}
