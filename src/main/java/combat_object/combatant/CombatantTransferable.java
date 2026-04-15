package combat_object.combatant;

import lombok.*;
import org.jetbrains.annotations.NotNull;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;

@AllArgsConstructor
public class CombatantTransferable implements Transferable {

    public static final DataFlavor COMBATANT_FLAVOR = new DataFlavor(Combatant.class, "Combatant");

    private final Combatant combatant;

    @Override
    public DataFlavor[] getTransferDataFlavors() {
        return new DataFlavor[]{COMBATANT_FLAVOR};
    }

    @Override
    public boolean isDataFlavorSupported(DataFlavor flavor) {
        return flavor.equals(COMBATANT_FLAVOR);
    }

    @NotNull
    @Override
    public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException {
        if (!isDataFlavorSupported(flavor)) throw new UnsupportedFlavorException(flavor);
        return combatant;
    }
}