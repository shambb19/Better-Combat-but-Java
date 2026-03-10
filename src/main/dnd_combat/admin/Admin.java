package admin;

import character_info.Combatant;
import damage_implements.Spells;
import damage_implements.Weapons;
import main.CombatMain;
import util.Locators;
import util.Message;

import java.util.ArrayList;
import java.util.Arrays;

import static util.TxtReader.*;

public class Admin {

    public static final String ADJUST_VALUE_CODE = "adjust";
    public static final String ADD_ELEMENT_CODE = "add";

    public static final String FRIENDLY_REF_CODE = "with";
    public static final String ENEMY_REF_CODE = "against";

    public static final String WEAPON_REF_CODE = "weapon";
    public static final String SPELL_REF_CODE = "spell";

    public static final String NAME_EDIT_CODE = "name";
    public static final String HP_EDIT_CODE = "hp";
    public static final String HP_CUR_EDIT_CODE = "hpCur";
    public static final String AC_EDIT_CODE = "ac";
    public static final String SPELL_MOD_EDIT_CODE = "spellMod";
    public static final String STAT_EDIT_CODE = "stat";

    public static final String DAMAGE_EDIT_CODE = "dmg";
    public static final String EFFECT_EDIT_CODE = "effect";

    /**
     * Accepts a text string from a JOptionPane and decodes it to make respective changes
     * to the current combat encounter.
     * @param request The input string. This uses several values separated by periods. This
     *                method handles item adjusting and adding. Adjusting follows the pattern
     *                "adjust.item_location.item_name.parameter_to_be_edited.new_value". For
     *                example, the code to edit Frodo's armor class to 16 would be
     *                "adjust.with.Frodo.ac.16". Alternatively, the code to change the damage
     *                dealt by Fireball to 2d10 would be "adjust.spell.Fireball.dmg.2d10".
     */
    public static void logAdminRequest(String request) {
        try {
            String[] codes = request.split("\\.");

            String actionCode = codes[0];
            switch (actionCode) {
                case ADJUST_VALUE_CODE -> logAdjustRequest(codes);
                case ADD_ELEMENT_CODE -> logAddRequest(codes);
            }
            CombatMain.COMBAT_MENU.update();
        } catch (Exception e) {
            Message.template("Log failed with throw for  " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void logAdjustRequest(String[] codes) {
        String ref = codes[1], target = codes[2], key = codes[3], value = codes[4];

        switch (ref) {
            case FRIENDLY_REF_CODE -> combatantAdjust(CombatMain.BATTLE.friendlies(), target, key, value);
            case ENEMY_REF_CODE -> combatantAdjust(CombatMain.BATTLE.enemies(), target, key, value);
            case WEAPON_REF_CODE -> Weapons.manualAdjust(target, key, value);
            case SPELL_REF_CODE -> Spells.manualAdjust(target, key, value);
        }
    }

    private static void combatantAdjust(ArrayList<Combatant> source, String name, String key, String value) {
        Combatant combatant = Locators.getCombatantWithNameFrom(source, name);
        assert combatant != null;
        combatant.manualAdjust(key, value);
    }

    private static void logAddRequest(String[] codes) {
        String ref = codes[1];
        ArrayList<String> params = new ArrayList<>(Arrays.asList(codes[2].split("/")));

        switch (ref) {
            case FRIENDLY_REF_CODE -> CombatMain.BATTLE.friendlies().add(decodeNPC(params, false));
            case ENEMY_REF_CODE -> CombatMain.BATTLE.enemies().add(decodeNPC(params, false));
            case WEAPON_REF_CODE -> Weapons.add(decodeWeapon(params));
            case SPELL_REF_CODE -> Spells.add(decodeSpell(params));
        }
    }

}
