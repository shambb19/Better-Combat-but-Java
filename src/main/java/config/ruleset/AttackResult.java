package config.ruleset;

import combat_object.combatant.Combatant;
import lombok.*;
import util.StringUtil;

@AllArgsConstructor
public enum AttackResult {

    SUCCEEDED("..attacker..'s attack succeeded"),
    TARGET_SAVE_SUCCESSFUL("..target..'s saving throw beat ..attacker..'s DC"),
    AC_NOT_MET("..attacker..'s roll did not meet ..target..'s AC"),
    MISFIRE("..attacker..'s gun jammed"),
    COVER_SAVE_SUCCESSFUL("..target..'s cover DC defeated ..attacker..'s shot"),
    SHOT_MISSED("..attacker..'s shot missed");

    private final String reason;

    public String getReason(Combatant attacker, Combatant target) {
        return StringUtil.infoString(reason, attacker, target);
    }

}
