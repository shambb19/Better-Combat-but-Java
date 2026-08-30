package combat_object.implement;

import combat_object.combatant.info.AbilityModifier;
import input.TextReader;
import input.syntax.Key;
import lombok.*;
import lombok.experimental.*;
import util.Roll;

import java.util.EnumMap;

import static input.syntax.Key.*;

@Getter @SuperBuilder
@FieldDefaults(makeFinal = true, level = AccessLevel.PROTECTED)
@ExtensionMethod(TextReader.class)
public class Gun extends Implement {

    int shortHitDc, longHitDc, coverDc, misfireDc;
    // TODO implement numShots somehow
    int numShots;
    boolean isHeavy, isExplosive;

    public static Gun from(EnumMap<Key, Object> params) {
        validateAll(params, "Gun");
        return Gun.builder()
                .name((String) params.get(NAME))
                .roll(Roll.d(8))
                .stat(AbilityModifier.OPTION)
                .isManual(false)
                .shortHitDc(params.get(HIT).beforeSlash())
                .longHitDc(params.get(HIT).afterSlash())
                .coverDc((int) params.get(COVER))
                .misfireDc((int) params.get(MISFIRE))
                .numShots((int) params.get(SHOTS))
                .isHeavy(params.get(HEAVY).booleanFromOptionalPresence())
                .isExplosive(params.get(EXPLOSIVE).booleanFromOptionalPresence())
                .build();
    }

    public static Gun createManual(String name) {
        return Gun.builder()
                .name(name)
                .roll(Roll.implementDefault())
                .stat(AbilityModifier.OPTION)
                .isManual(true)
                .shortHitDc(10).longHitDc(10)
                .coverDc(10).misfireDc(2)
                .numShots(1)
                .isHeavy(false).isExplosive(false)
                .build();
    }

}
