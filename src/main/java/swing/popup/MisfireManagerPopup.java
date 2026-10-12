package swing.popup;

import _manager.MisfireManager;
import swing.custom.Popup;
import swing.builder.ButtonBuilder;
import util.Message;
import util.Roll;

import javax.swing.*;
import java.util.List;

import static swing.ColorStyles.*;
import static swing.builder.PanelBuilder.*;

public class MisfireManagerPopup extends Popup {

    public MisfireManagerPopup(List<MisfireManager.Misfire> misfires) {
        setTitle("Misfires");
        setModalityType(DEFAULT_MODALITY_TYPE);

        JPanel misfiresPanel = createPanel(ONE_COLUMN).vgap(10).build().get();
        misfires.forEach(m -> misfiresPanel.add(new GunPanel(m)));

        pack();
        setVisible(true);
    }

    private class GunPanel extends JPanel {

        private final MisfireManager.Misfire misfire;

        GunPanel(MisfireManager.Misfire misfire) {
            this.misfire = misfire;

            modifyingPanel(this).layoutType(FLOW_RIGHT)
                    .hostedComponent(misfire.gun().getName(), null).build().validated();

            ButtonBuilder.createButton("Discard", CRITICAL, this::discardGun).build().putIn(this);
            ButtonBuilder.createButton("Attempt Repair", SUCCESS, this::attemptRepair).build().putIn(this);
        }

        void discardGun() {
            MisfireManager.logDiscard(misfire);
            MisfireManagerPopup.this.remove(this);

            if (MisfireManagerPopup.this.getComponents().length == 0) MisfireManagerPopup.this.dispose();
        }

        void attemptRepair() {
            setEnabled(false);

            Message.promptRoll("to repair " + misfire.combatant() + "'s " + misfire.gun(),
                    Roll.d(6), 3,
                    () -> MisfireManager.logRepair(misfire), "The repair has failed. You can try again next turn."
            );

            MisfireManagerPopup.this.dispose();
        }
    }

}
