package campaign_creator_menu;

import __main.Main;
import combat_object.CombatObject;
import combat_object.combatant.Combatant;
import combat_object.combatant.NPC;
import combat_object.scenario.Scenario;
import format.swing_comp.SwingPane;
import lombok.*;
import lombok.experimental.*;
import org.intellij.lang.annotations.MagicConstant;
import util.Message;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static format.ColorStyles.SUCCESS;
import static format.swing_comp.SwingComp.fluent;
import static format.swing_comp.SwingComp.*;
import static format.swing_comp.SwingPane.*;

@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@ExtensionMethod(util.Filterable.class)
public class CompletedElementList<T extends CombatObject> extends JPanel {

    public static final Combatant FRIENDLY_NEW = NPC.create(
            "New Ally", -1, -1, false
    );
    public static final Combatant ENEMY_NEW = NPC.create(
            "New Enemy", -1, -1, true
    );
    public static final Scenario SCENARIO_NEW = Scenario.create(
            "New Scenario", new HashMap<>(), new HashMap<>()
    );

    static final Map<CombatObject, String> labelNames = Map.of(
            FRIENDLY_NEW, "Party and Allies:",
            ENEMY_NEW, "Enemies:",
            SCENARIO_NEW, "Scenarios:"
    );

    ScrollPane<T> list;
    T newOption;

    public CompletedElementList(
            List<T> contents,
            @MagicConstant(valuesFromClass = CompletedElementList.class) T newOption,
            CompletedElementsList parent
    ) {
        this.newOption = newOption;
        list = new ScrollPane<>(parent);

        SwingPane.fluent(this).collect(getPanel()).arrangedAs(SINGLE_ROW);

        contents.forEach(list::add);
    }

    private JPanel getPanel() {
        String labelText = labelNames.get(newOption);

        JLabel label = label(labelText, 18f).withEmptyBorder(4).enabled(false).component();

        JButton withNew =
                button(newOption.getName(), SUCCESS,
                        () -> Main.getCreatorMenu().logEdit(newOption, true))
                        .withDerivedFont(Font.PLAIN, 13f)
                        .component();

        return newBorderPanel(0, 4, north(label), center(list), south(withNew)).component();
    }

    public void add(T item) {
        list.add(item);
    }

    public void remove(T item) {
        list.remove(item);
    }

    @SuppressWarnings("unchecked")
    public List<T> toList() {
        List<T> collection = new ArrayList<>();
        for (Object obj : list.model.toArray())
            collection.add((T) obj);
        return collection;
    }

    @FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
    static class ScrollPane<T extends CombatObject> extends JScrollPane {

        CompletedElementsList parent;

        JList<T> list;
        DefaultListModel<T> model;
        ListSelectionListener listener;

        public ScrollPane(CompletedElementsList parent) {
            this.parent = parent;

            model = new DefaultListModel<>();

            list = fluent(new JList<>(model))
                    .applied(l -> l.setSelectionMode(ListSelectionModel.SINGLE_SELECTION))
                    .withEmptyBorder(4).component();

            listener = this::logSelection;
            list.addListSelectionListener(listener);

            setViewportView(list);
        }

        @SuppressWarnings("unchecked")
        public void add(T element) {
            doWithoutListener(() -> {
                T existingVersion = ((T[]) model.toArray()).of().firstWithToStringEquals(element.toString());

                model.removeElement(existingVersion);
                model.addElement(element);
            });
        }

        public void remove(CombatObject element) {
            doWithoutListener(() -> model.removeElement(element));
        }

        private void doWithoutListener(Runnable action) {
            list.removeListSelectionListener(listener);
            action.run();
            list.addListSelectionListener(listener);
            revalidate();
            repaint();
        }

        private void logSelection(ListSelectionEvent e) {
            if (e.getValueIsAdjusting()) return;
            if (list.getSelectedValue() == null) return;

            T selectedValue = list.getSelectedValue();
            list.clearSelection();

            if (selectedValue instanceof Scenario && parent.isNotEnoughForScenario()) {
                Message.showAsInfoMessage("Add more Combatants!");
                return;
            }

            int route = Message.showEditOrRemovePrompt(selectedValue.toString());

            if (route == Message.EDIT_OPTION)
                Main.getCreatorMenu().logEdit(selectedValue, false);
            else if (route == Message.REMOVE_OPTION)
                remove(selectedValue);
        }
    }
}
