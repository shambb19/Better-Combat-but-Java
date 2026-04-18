package campaign_creator_menu;

import combat_object.CombatObject;
import combat_object.combatant.Combatant;
import combat_object.combatant.NPC;
import combat_object.scenario.Scenario;
import format.ColorStyles;
import lombok.*;
import lombok.experimental.*;
import org.intellij.lang.annotations.MagicConstant;
import swing.swing_comp.SwingPane;
import util.Filter;
import util.Message;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.util.List;
import java.util.*;

import static swing.swing_comp.SwingComp.button;
import static swing.swing_comp.SwingPane.*;


@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class CompletedElementList<T extends CombatObject> extends JPanel {

    public static final Combatant FRIENDLY_NEW = NPC.create(
            "New Ally", -1, -1, false
    );
    public static final Combatant ENEMY_NEW = NPC.create(
            "New Enemy", -1, -1, true
    );
    public static final Scenario SCENARIO_NEW = new Scenario(
            "New Scenario", new HashMap<>(), new HashMap<>()
    );

    static final Map<CombatObject, String> labelNames = Map.of(
            FRIENDLY_NEW, "Party and Allies:",
            ENEMY_NEW, "Enemies:",
            SCENARIO_NEW, "Scenarios:"
    );

    CampaignCreatorMenu root;
    ScrollPane<T> list;
    T newOption;

    public CompletedElementList(
            List<T> contents,
            @MagicConstant(valuesFromClass = CompletedElementList.class) T newOption,
            CompletedElementsList parent,
            CampaignCreatorMenu root
    ) {
        this.root = root;
        this.newOption = newOption;
        list = new ScrollPane<>(parent, root);

        SwingPane.fluent(this).collect(getPanel()).arrangedAs(SINGLE_ROW);

        contents.forEach(list::add);
    }

    private JPanel getPanel() {
        String labelText = labelNames.get(newOption);

        JLabel label = label(labelText, Font.PLAIN, 18f, ColorStyles.TEXT_PRIMARY)
                .withEmptyBorder(4, 4, 4, 4)
                .enabled(false).component();

        JButton withNew = button(newOption.getName(), ColorStyles.SUCCESS,
                () -> root.logEdit(newOption, true))
                .withDerivedFont(Font.PLAIN, 13f)
                .component();

        return newArrangedAs(BORDER, 0, 4)
                .borderCollect(
                        north(label), center(list), south(withNew)
                ).component();
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

    static class ScrollPane<T extends CombatObject> extends JScrollPane {

        private final CompletedElementsList parent;
        private final CampaignCreatorMenu root;

        private final JList<T> list;
        private final DefaultListModel<T> model;
        private final ListSelectionListener listener;

        public ScrollPane(CompletedElementsList parent, CampaignCreatorMenu root) {
            this.parent = parent;
            this.root = root;

            model = new DefaultListModel<>();

            list = fluent(new JList<>(model))
                    .applied(l -> l.setSelectionMode(ListSelectionModel.SINGLE_SELECTION))
                    .withEmptyBorder(4, 4, 4, 4).component();

            listener = this::logSelection;
            list.addListSelectionListener(listener);

            setViewportView(list);
        }

        @SuppressWarnings("unchecked")
        public void add(T element) {
            doWithoutListener(() -> {
                List<T> modelList = (List<T>) (Object) Arrays.asList(model.toArray());
                T existingVersion = Filter.firstWithToStringEquals(modelList, element.toString());

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
                Message.template("Add more Combatants!");
                return;
            }

            int route = Message.editOrRemoveOption(selectedValue.toString());

            if (route == Message.EDIT_OPTION)
                root.logEdit(selectedValue, false);
            else if (route == Message.REMOVE_OPTION)
                remove(selectedValue);
        }
    }
}
