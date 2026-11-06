package config.ruleset;

import lombok.*;

@AllArgsConstructor
public enum AttackResult {

    SUCCEEDED("..attacker..'s attack succeeded"),
    TARGET_SAVE_SUCCESSFUL("..target..'s saving throw beat ..attacker..'s DC"),
    AC_NOT_MET("..attacker..'s roll did not meet ..target..'s AC"),
    MISFIRE("..attacker..'s gun jammed"),
    COVER_SAVE_SUCCESSFUL("..target..'s cover DC defeated ..attacker..'s shot"),
    SHOT_MISSED("..attacker..'s shot missed");

    @Getter private final String reason;

}
