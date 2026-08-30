package _global_list;

@lombok.Getter public enum Resource {

    // ----- ICONS ----- //
    APP_ICON("/logo.png"),
    ATTACK_BUTTON("/attack-button.png"),
    END_TURN_BUTTON("/end-turn-button.png"),
    HEAL_BUTTON("/heal-button.png"),
    INSPIRATION_BUTTON("/inspiration-button.png"),

    // ----- DATABASES ----- //
    SPELL_CODE("/spells.txt"),
    WEAPON_CODE("/weapons.txt"),
    GUN_CODE("/guns.txt"),

    // ----- STARTER CODE ----- //
    STARTER_STANDARD("/starter.txt"),
    STARTER_STEAMPUNK("/starter_steampunk.txt"),

    // ----- TUTORIAL CODE ----- //
    TUTORIAL_STANDARD("/tutorial_standard.txt"),
    TUTORIAL_STEAMPUNK("/tutorial_steampunk.txt");

    private final java.net.URL url;

    Resource(String root) {
        url = Resource.class.getResource(root);
    }

}