package campaign_creator_menu;

import combat_object.CombatObject;
import combat_object.combatant.Combatant;
import combat_object.combatant.NPC;
import combat_object.scenario.Scenario;
import format.ColorStyles;
import lombok.*;
import lombok.experimental.*;
import org.intellij.lang.annotations.MagicConstant;
import swing.swing_comp.SwingComp;
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

        modifiable(this).collect(getPanel()).withLayout(SINGLE_ROW);

        contents.forEach(list::add);
    }

    private JPanel getPanel() {
        String labelText = labelNames.get(newOption);
        String buttonText = newOption.getName();

        JLabel label = label(labelText)
                .withDerivedFont(Font.PLAIN, 18f)
                .withForeground(ColorStyles.TEXT_PRIMARY)
                .withEmptyBorder(4, 4, 4, 4)
                .disabled()
                .component();

        JButton withNew = button(buttonText, () -> root.logEdit(newOption, true))
                .withBackgroundAndForeground(ColorStyles.SUCCESS, ColorStyles.TEXT_PRIMARY)
                .withEmptyBorder(10, 0, 10, 0)
                .withDerivedFont(Font.PLAIN, 13f)
                .component();

        return panel().withLayout(BORDER)
                .withGaps(0, 4)
                .with(label, BorderLayout.NORTH)
                .with(list, BorderLayout.CENTER)
                .with(withNew, BorderLayout.SOUTH)
                .component();
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

            list = SwingComp.list(model).withEmptyBorder(4, 4, 4, 4).component();

            listener = this::logSelection;
            list.addListSelectionListener(listener);

            setViewportView(list);
        }

        @SuppressWarnings("unchecked")
        public void add(T element) {
            list.removeListSelectionListener(listener);

            List<T> modelList = (List<T>) (Object) Arrays.asList(model.toArray());
            T existingVersion = Filter.firstWithToStringEquals(modelList, element.toString());

            model.removeElement(existingVersion);
            model.addElement(element);

            list.addListSelectionListener(listener);

            revalidate();
            repaint();
        }

        public void remove(CombatObject element) {
            list.removeListSelectionListener(listener);

            model.removeElement(element);

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
