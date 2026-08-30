package combat_menu.encounter_selection;

import _manager.EncounterManager;
import combat_object.combatant.Combatant;
import combat_object.combatant.NPC;
import combat_object.scenario.Scenario;
import lombok.*;
import lombok.experimental.*;
import util.Filterable;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;

import static combat_menu.encounter_selection.EncounterSelectionPanel.CombatantCard;
import static swing.ColorStyles.*;
import static swing.fluent.SwingComp.button;
import static swing.fluent.SwingPane.*;

@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@ExtensionMethod(util.StringUtil.class)
public class QuickScenario extends JDialog {

    EncounterSelectionPanel root;

    JButton confirmButton;
    JComboBox<NPC> comboBox;
    JPanel selectedNpcPanel;

    public QuickScenario(EncounterSelectionPanel root) {
        this.root = root;

        setTitle("Quick Scenario");
        setLocationRelativeTo(root.getScenarioCombo());

        fluent(this).arrangedAs(BORDER, 0, 15).spaced().withPreferredSize(350, 310);

        comboBox = new JComboBox<>(EncounterManager.getEncounter().getAllNpcs().toArray(new NPC[0]));
        root.getQuickAdds().forEach(comboBox::addItem);
        fluent(comboBox).withAction(b -> addNpcToScenario()).in(this, BorderLayout.NORTH);

        selectedNpcPanel = newArrangedAs(VERTICAL_BOX).component();
        fluent(selectedNpcPanel).toScroller().in(this, BorderLayout.CENTER);

        confirmButton = button("Quick Add", SUCCESS, this::addQuickScenario).in(this, BorderLayout.SOUTH);

        pack();
        setVisible(true);
    }

    private void addNpcToScenario() {
        NPC selected = (NPC) comboBox.getSelectedItem();
        assert selected != null;

        comboBox.removeItem(selected);

        Color accent = selected.isEnemy() ? ENEMY : FRIENDLY;
        CombatantCard card = new CombatantCard(
                selected, "Quantity", accent, true, this::validateCards
        );
        card.getAbsentCheck().addActionListener(e -> {
            comboBox.addItem(selected);
            selectedNpcPanel.remove(card);
            selectedNpcPanel.revalidate();
            selectedNpcPanel.repaint();
        });
        selectedNpcPanel.add(card);

        selectedNpcPanel.revalidate();
        selectedNpcPanel.repaint();
    }

    private void addQuickScenario() {
        HashMap<String, Integer> friendlies = new HashMap<>();
        HashMap<String, Integer> enemies = new HashMap<>();

        for (Component comp : selectedNpcPanel.getComponents()) {
            if (!(comp instanceof CombatantCard card)) return;

            Combatant c = card.getCombatant();
            int qty = card.getInputValue();

            if (c.isEnemy()) enemies.put(c.getName(), qty);
            else friendlies.put(c.getName(), qty);
        }

        Scenario s = Scenario.builder()
                .name("Quick Scenario")
                .with(friendlies)
                .against(enemies)
                .build();

        System.out.println(s);

        root.getQuickScenarios().add(s);
        root.updateScenario();
        dispose();
    }

    private void validateCards() {
        boolean allInputsValid = Filterable.of(selectedNpcPanel.getComponents()).castToAsList(CombatantCard.class)
                .stream().allMatch(CombatantCard::hasValidInput);
        confirmButton.setEnabled(allInputsValid);
    }

}
