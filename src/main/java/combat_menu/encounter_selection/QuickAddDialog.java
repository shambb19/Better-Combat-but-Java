package combat_menu.encounter_selection;

import combat_object.combatant.NPC;
import lombok.AccessLevel;
import lombok.experimental.ExtensionMethod;
import lombok.experimental.FieldDefaults;
import org.intellij.lang.annotations.MagicConstant;
import swing.fluent.SwingComp;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.border.MatteBorder;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.function.Consumer;

import static swing.ColorStyles.*;
import static swing.fluent.SwingComp.*;
import static swing.fluent.SwingPane.*;
import static combat_menu.encounter_selection.CombatantScroller.CombatantOption.*;

@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@ExtensionMethod(SwingComp.class)
public class QuickAddDialog extends JDialog {

    static final String SELECT = "select";
    static final String CREATE = "create";

    List<NPC> available;
    Consumer<HashMap<NPC, Integer>> onConfirmSelection;

    CardLayout cardLayout = new CardLayout();
    JPanel cardHolder;
    CombatantScroller selectionScroller;
    JButton selectConfirmButton;

    public QuickAddDialog(boolean forEnemies, List<NPC> available, Consumer<HashMap<NPC, Integer>> onConfirmSelection) {
        this.available = available;
        this.onConfirmSelection = onConfirmSelection;

        setTitle(forEnemies ? "Add Enemies" : "Add Allies");
        setLocationRelativeTo(null);
        setModal(true);

        fluent(this).arrangedAs(BORDER).withBorder(new LineBorder(TRACK, 1));

        cardHolder = new JPanel(cardLayout);
        cardHolder.setOpaque(false);

        //
        // begin select card
        CombatantScroller.CombatantOption toExclude = forEnemies ? ALLIES : ENEMIES;
        selectionScroller = CombatantScroller
                .npcPanel(this, "Quantity", true, false, this::validateSelection, toExclude)
                .fluent().withPreferredSize(360, 260).component();

        available.forEach(selectionScroller::addCard);
        convertCheckboxesToIncludeToggles();

        JPanel selectPanelFooter = newArrangedAs(FLOW_RIGHT, 12, 8)
                .withBackground(BG_DARK)
                .withBorder(new MatteBorder(1, 0, 0, 0, TRACK))
                .component();

        JPanel selectCard = newArrangedAs(BORDER).borderCollect(
                center(selectionScroller), south(selectPanelFooter)
        ).transparent().component();

        selectConfirmButton = button("Confirm", SUCCESS, this::confirmSelection)
                .enabled(false)
                .in(selectPanelFooter);

        cardHolder.add(selectCard, SELECT);

        //
        // --create card
        JPanel createCard = newArrangedAs(BORDER).borderCollect(
                center(new QuickCombatantPanel(this::confirmCreate, forEnemies))
        ).transparent().component();

        cardHolder.add(createCard, CREATE);

        add(cardHolder, BorderLayout.CENTER);

        showCard(SELECT);

        pack();
    }

    public void showCard(@MagicConstant(valuesFromClass = QuickAddDialog.class) String card) {
        if (available.isEmpty()) cardLayout.show(cardHolder, CREATE);
        else cardLayout.show(cardHolder, card);
    }

    /** Relabels the existing "absent" checkbox as an inclusion toggle and ties it to the quantity field. */
    private void convertCheckboxesToIncludeToggles() {
        for (CombatantCard card : selectionScroller.getCombatantCards()) {
            if (card.isEmpty || card.checkBox == null) continue;

            card.checkBox.setText("Include");
            card.checkBox.setSelected(false);
            card.input.setVisible(false);

            card.checkBox.addActionListener(e -> {
                card.input.setVisible(card.checkBox.isSelected());
                card.revalidate();
                card.repaint();
            });
        }
    }

    private void confirmSelection() {
        HashMap<NPC, Integer> selected = new LinkedHashMap<>();
        for (CombatantCard card : selectionScroller.getCombatantCards()) {
            if (card.isEmpty || card.checkBox == null || !card.checkBox.isSelected()) continue;
            selected.put((NPC) card.getCombatant(), card.getInputValue());
        }
        onConfirmSelection.accept(selected);
        dispose();
    }

    private void confirmCreate(NPC created) {
        available.add(created);
        CombatantCard card = selectionScroller.addCard(created);
        card.getCheckBox().setSelected(true);
        showCard(SELECT);
    }

    private void validateSelection() {
        boolean anyIncluded = Arrays.stream(selectionScroller.getCombatantCards())
                .anyMatch(c -> !c.isEmpty && c.checkBox != null && c.checkBox.isSelected());
        selectConfirmButton.setEnabled(anyIncluded && selectionScroller.areAllCardsValid());
    }

}