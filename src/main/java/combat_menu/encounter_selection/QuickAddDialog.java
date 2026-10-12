package combat_menu.encounter_selection;

import combat_object.combatant.CombatantOption;
import combat_object.combatant.NPC;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.intellij.lang.annotations.MagicConstant;
import swing.builder.ButtonBuilder;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.function.Consumer;

import static swing.ColorStyles.*;
import static swing.builder.PanelBuilder.*;

@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class QuickAddDialog extends JDialog {

    static final String SELECT = "select", CREATE = "create";

    boolean forEnemies;
    List<NPC> available;
    Consumer<HashMap<NPC, Integer>> onConfirmSelection;

    CardLayout cardLayout = new CardLayout();
    JPanel cardHolder;
    JPanel createCard;
    CombatantScroller selectionScroller;
    JButton selectConfirmButton;

    public QuickAddDialog(
            boolean forEnemies, List<NPC> available, Consumer<HashMap<NPC, Integer>> onConfirmSelection
    ) {
        this.forEnemies = forEnemies;
        this.available = available;
        this.onConfirmSelection = onConfirmSelection;

        setTitle(forEnemies ? "Add Enemies" : "Add Allies");
        setLocationRelativeTo(null);
        setModal(true);

        modifyingPanel(this).layoutType(BORDER).border(BorderFactory.createLineBorder(TRACK, 1)).build().validated();

        cardHolder = new JPanel(cardLayout);
        cardHolder.setOpaque(false);

        //
        // begin select card
        CombatantOption toExclude = CombatantOption.getNpcType(!forEnemies);
        selectionScroller = CombatantScroller.getNpcQuickAddPanel(this, this::validateSelection, toExclude);
        selectionScroller.setPreferredSize(new Dimension(360, 260));

        available.forEach(selectionScroller::addCard);

        JPanel selectPanelFooter = createPanel(FLOW_RIGHT).hgap(12).vgap(8)
                .background(BG_DARK)
                .border(BorderFactory.createMatteBorder(1, 0, 0, 0, TRACK)).build().get();

        JPanel selectCard = createPanel(BORDER)
                .hostedComponent(selectionScroller, BorderLayout.CENTER)
                .hostedComponent(selectPanelFooter, BorderLayout.SOUTH).build().get();

        selectConfirmButton = ButtonBuilder.createButton("Confirm", SUCCESS, this::confirmSelection)
                .enabled(false)
                .build().putIn(selectPanelFooter);

        cardHolder.add(selectCard, SELECT);

        createCard = createPanel(BORDER)
                .hostedComponent(new QuickCombatantPanel(this::confirmCreate, forEnemies), BorderLayout.CENTER)
                .build().get();

        setCreateCard();

        cardHolder.add(createCard, CREATE);

        add(cardHolder, BorderLayout.CENTER);

        showCard(SELECT);

        pack();
    }

    public void showCard(@MagicConstant(valuesFromClass = QuickAddDialog.class) String card) {
        if (available.isEmpty()) cardLayout.show(cardHolder, CREATE);
        else cardLayout.show(cardHolder, card);
    }

    private void setCreateCard() {
        createCard.removeAll();
        createCard.add(new QuickCombatantPanel(this::confirmCreate, forEnemies), BorderLayout.CENTER);
    }

    private void confirmSelection() {
        HashMap<NPC, Integer> selected = new LinkedHashMap<>();
        for (CombatantCard card : selectionScroller.getCombatantCards()) {
            if (card.isEmpty || card.checkBox == null || !card.checkBox.isSelected()) continue;
            selected.put((NPC) card.combatant, card.getInputValue());
        }
        onConfirmSelection.accept(selected);
        dispose();
    }

    private void confirmCreate(NPC created) {
        available.add(created);
        selectionScroller.addCard(created);
        showCard(SELECT);
        setCreateCard();
    }

    private void validateSelection() {
        boolean anyIncluded = Arrays.stream(selectionScroller.getCombatantCards())
                .anyMatch(c -> !c.isEmpty && c.checkBox != null && c.checkBox.isSelected());
        selectConfirmButton.setEnabled(anyIncluded && selectionScroller.areAllCardsValid());
    }

}