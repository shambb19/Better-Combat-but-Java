package combat_object;

import input.CodeWriter;
import lombok.*;
import lombok.experimental.*;

@Getter @SuperBuilder @AllArgsConstructor
public abstract class CombatObject {
    protected final String name;

    public abstract CodeWriter toTxt();
}