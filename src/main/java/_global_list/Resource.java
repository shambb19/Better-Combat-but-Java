package _global_list;

@lombok.Getter public enum Resource {

    APP_ICON("/logo.png"),
    ATTACK_BUTTON("/attack-button.png"),
    END_TURN_BUTTON("/end-turn-button.png"),
    HEAL_BUTTON("/heal-button.png"),
    INSPIRATION_BUTTON("/inspiration-button.png"),

    STARTER_CODE("/starter.txt"),
    SPELL_CODE("/spells.txt"),
    WEAPON_CODE("/weapons.txt");

    private final java.net.URL url;

    Resource(String root) {
        url = Resource.class.getResource(root);
    }

}
