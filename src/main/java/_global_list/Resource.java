package _global_list;

import java.net.URL;

public enum Resource {

    PROGRAM_LOGO("/logo.png"),
    ATTACK_BUTTON("/attack-button.png"),
    END_TURN_BUTTON("/end-turn-button.png"),
    HEAL_BUTTON("/heal-button.png"),
    INSPIRATION_BUTTON("/inspiration-button.png"),

    STARTER_CODE("/starter.txt"),
    SPELL_CODE("/spells.txt"),
    WEAPON_CODE("/weapons.txt");

    private final String root;

    Resource(String root) {
        this.root = root;
    }

    public URL url() {
        return Resource.class.getResource(root);
    }

}
