package character_info;

public enum AbilityModifier {

    STR, DEX, CON, INT, WIS, CHA;

    @Override
    public String toString() {
        return name().toLowerCase();
    }

}
