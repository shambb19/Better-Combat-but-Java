package combat_object;

import lombok.*;
import lombok.experimental.*;

@Getter @SuperBuilder @AllArgsConstructor
public abstract class CombatObject {

    protected final String name;

}