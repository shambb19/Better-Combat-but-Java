package popup;

import combat_object.combatant.Combatant;
import swing.custom.Popup;

import javax.swing.*;
import java.awt.*;

import static swing.fluent.SwingComp.*;
import static swing.fluent.SwingPane.*;
import static swing.ColorStyles.*;

public class InitiativeResolutionPopup extends Popup {

    static String INSTRUCTIONS = "There is an initiative conflict between these two combatants. " +
            "Select the one that will go first between them in the queue.";

    public Combatant result;

    public InitiativeResolutionPopup(Combatant c1, Combatant c2) {
        setTitle("Initiative Resolution");
        setModalityType(DEFAULT_MODALITY_TYPE);
        fluent(this).arrangedAs(BORDER, 0, 12).spaced();

        textArea(INSTRUCTIONS).in(this, BorderLayout.NORTH);

        interface ButtonMaker {
            JButton create(Combatant host);
        }
        ButtonMaker bm = host -> button(host.getName(), BG_DEEP, () -> {
            result = host;
            dispose();
        }).withMinimumSize(270, 35).withMaximumSize(270, 35).component();

        panelIn(this, BorderLayout.CENTER).arrangedAs(VERTICAL_BOX, 0, 8)
                .collect(bm.create(c1), bm.create(c2));

        pack();
        setMinimumSize(new Dimension(300, 210));
        setVisible(true);
    }

}
