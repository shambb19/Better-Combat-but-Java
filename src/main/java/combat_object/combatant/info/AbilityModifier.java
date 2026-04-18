package combat_object.combatant.info;

public enum AbilityModifier {
    STR, DEX, CON, INT, WIS, CHA, OPTION;

    @Override public String toString() {
        return name();
    }
}
