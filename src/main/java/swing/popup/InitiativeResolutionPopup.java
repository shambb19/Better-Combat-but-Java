package swing.popup;

import combat_object.combatant.Combatant;
import exception.ProgramRunError;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import swing.custom.Popup;
import swing.builder.ButtonBuilder;
import swing.builder.TextItemBuilder;
import util.function.Writer;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import static swing.ColorStyles.*;
import static swing.builder.PanelBuilder.*;

@FieldDefaults(level = AccessLevel.PRIVATE)
public class InitiativeResolutionPopup extends Popup {

    static Writer<Integer> INSTRUCTIONS_GETTER = i -> {
        String errorMessage = "InitiativeResolutionPopup.INSTRUCTIONS_GETTER; i out of bounds for [1, 4]";
        if (i < 1 || i > 4) throw new IndexOutOfBoundsException(errorMessage);
        String[] adjectives = {"", "first", "second", "third", "fourth"};

        return "There is an initiative conflict between these combatants. " +
                "Select the one that will go " + adjectives[i + 1] + " among them in the queue.";
    };

    public ArrayList<Combatant> result;

    int numRuns;
    final JTextArea instructionArea;

    public InitiativeResolutionPopup(List<Combatant> toCompare) {
        if (toCompare.size() > 4) throw new ProgramRunError("num combatants to compare cannot be > 4");

        result = new ArrayList<>();
        numRuns = 1;

        setTitle("Initiative Resolution");
        setModalityType(DEFAULT_MODALITY_TYPE);

        modifyingPanel(this).layoutType(BORDER).vgap(12)
                        .border(SPACER_BORDER).build().validated();

        instructionArea = TextItemBuilder.createTextArea(INSTRUCTIONS_GETTER.write(numRuns))
                .build().putIn(this, BorderLayout.NORTH);

        Function<Combatant, JButton> buttonMaker = host ->
                ButtonBuilder.createButton(host.getName(), BG_DEEP, b -> {
                    result.add(host);
                    toCompare.remove(host);
                    b.setEnabled(false);
                    numRuns++;
                    if (toCompare.size() > 1) {
                        instructionArea.setText(INSTRUCTIONS_GETTER.write(numRuns));
                    }
                    else {
                        result.add(toCompare.removeFirst());
                        dispose();
                    }
                }).fixedSize(new Dimension(270, 35)).build().get();

        JButton[] buttons = toCompare.stream().map(buttonMaker).toList().toArray(new JButton[0]);
        JPanel buttonPanel = createPanel(VERTICAL_BOX).build().putIn(this, BorderLayout.CENTER);
        for (JButton b : buttons) buttonPanel.add(b);

        pack();

        int height = 150 + (35 * toCompare.size());
        setMinimumSize(new Dimension(300, height));
        setVisible(true);

    }

}
