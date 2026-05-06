package popup;

import manager.MisfireManager;
import swing.custom.Popup;
import util.Message;

import javax.swing.*;
import java.util.List;

import static swing.ColorStyles.CRITICAL;
import static swing.ColorStyles.SUCCESS;
import static swing.fluent.SwingPane.*;

public class MisfireManagerPopup extends Popup {

    public MisfireManagerPopup(List<MisfireManager.Misfire> misfires) {
        setTitle("Misfires");
        setModalityType(DEFAULT_MODALITY_TYPE);

        JPanel misfiresPanel = newArrangedAs(ONE_COLUMN, 0, 10).component();
        misfires.forEach(m -> misfiresPanel.add(new GunPanel(m)));

        pack();
        setVisible(true);
    }

    private class GunPanel extends JPanel {
        private final MisfireManager.Misfire misfire;

        GunPanel(MisfireManager.Misfire misfire) {
            this.misfire = misfire;

            fluent(this).arrangedAs(FLOW_RIGHT).collect(misfire.gun().getName());

            button("Discard", CRITICAL, this::discardGun).in(this);
            button("Attempt Repair", SUCCESS, this::attemptRepair).in(this);
        }

        void discardGun() {
            MisfireManager.logDiscard(misfire);
            MisfireManagerPopup.this.remove(this);

            if (MisfireManagerPopup.this.getComponents().length == 0) MisfireManagerPopup.this.dispose();
        }

        void attemptRepair() {
            setEnabled(false);
            int repairRoll = Message.promptIntWithLoop(
                    "Roll 1d6 to repair " + misfire.combatant() + "'s " + misfire.gun() + ".",
                    "Repair Attempt");

            if (repairRoll >= 3) MisfireManager.logRepair(misfire);
            else Message.showAsInfoMessage("The repair has failed. You can try again next turn.");

            MisfireManagerPopup.this.dispose();
        }
    }

}
