package combat_object.implement;

import combat_object.combatant.info.AbilityModifier;
import input.Key;
import input.TextReader;
import lombok.*;
import lombok.experimental.*;

import java.util.EnumMap;

import static input.Key.*;

@Getter @SuperBuilder
@FieldDefaults(makeFinal = true, level = AccessLevel.PROTECTED)
@ExtensionMethod(TextReader.class)
public class Gun extends Implement {

    int shortHitDc, longHitDc, coverDc, misfireDc;
    // TODO implement numShots somehow
    int numShots;
    boolean isHeavy, isExplosive;

    public static Gun from(EnumMap<Key, Object> params) {
        return Gun.builder()
                .name((String) params.get(NAME))
                .numDice(1).dieSize(8)
                .stat(AbilityModifier.OPTION)
                .isManual(false)
                .shortHitDc(params.get(RANGE_DC).beforeSlash())
                .longHitDc(params.get(RANGE_DC).afterSlash())
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
                .numDice(1).dieSize(100)
                .stat(AbilityModifier.OPTION)
                .isManual(true)
                .shortHitDc(10).longHitDc(10)
                .coverDc(10).misfireDc(2)
                .numShots(1)
                .isHeavy(false).isExplosive(false)
                .build();
    }

}
