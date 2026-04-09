package campaign_creator_menu;

import character_info.combatant.Combatant;
import character_info.combatant.NPC;
import damage_implements.Implement;
import format.ColorStyles;
import util.Filter;
import util.Message;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import static swing.swing_comp.SwingPane.*;
import static util.Message.getWithLoopUntilInt;
import static util.Message.template;

public class ListSelectionPanel<T> extends JPanel {

    private final ImplementListPane<T> availableList;
    private final ImplementListPane<T> selectedList;

    public ListSelectionPanel(List<T> sourceList, String name) {
        modifiable(this).withLayout(BORDER);

        ArrayList<T> sourceWithoutManual = new ArrayList<>(sourceList);
        sourceWithoutManual.removeIf(item -> item.toString().startsWith("Manual"));

        availableList = new ImplementListPane<>(sourceWithoutManual, this, true);
        selectedList = new ImplementListPane<>(null, this, false);

        JTextField availableSearch = field()
                .withAction(f -> availableList.logSearch(f.getText()))
                .withHighlight(ColorStyles.GREEN_APPLE, RIGHT, false)
                .build();

        JLabel selectedLabel = label("Selected " + name + ":")
                .withEmptyBorder(10)
                .build();

        panelIn(this, BorderLayout.NORTH)
                .collect(availableSearch, selectedLabel)
                .withLayout(SINGLE_ROW);

        panelIn(this, BorderLayout.CENTER).collect(availableList, selectedList).withLayout(FLOW);
    }

    public List<T> getSelected() {
        return Filter.matchingCondition(selectedList.getList(), Objects::nonNull);
    }

    public HashMap<Combatant, Integer> getSelectedScenario() {
        return selectedList.scenarioQtyList;
    }

    public void swapWithOtherList(T swappedImplement) {
        if (availableList.contains(swappedImplement)) {
            availableList.remove(swappedImplement);
            selectedList.add(swappedImplement);
        } else {
            selectedList.remove(swappedImplement);
            availableList.add(swappedImplement);
        }
        revalidate();
        repaint();
    }

    public void reset() {
        while (!selectedList.getList().isEmpty())
            swapWithOtherList(selectedList.getList().removeFirst());
    }

    public void setTo(List<T> selectedList) {
        reset();
        selectedList.forEach(item -> {
            if (availableList.contains(item))
                swapWithOtherList(item);
        });
    }

    public void updateSourceList(List<T> newSource) {
        reset();
        availableList.updateSourceList(newSource);
    }

    static class ImplementListPane<T> extends JScrollPane {

        private final ListSelectionPanel<T> root;

        private final boolean isAvailableList;

        private final List<T> allImplements;
        private final List<T> implementList;
        private final JList<T> list;

        private final HashMap<Combatant, Integer> scenarioQtyList = new HashMap<>();

        @SuppressWarnings("all")
        public ImplementListPane(List<T> allImplements, ListSelectionPanel root, boolean isAvailableList) {
            this.root = root;
            this.isAvailableList = isAvailableList;

            this.allImplements = new ArrayList<>(Objects.requireNonNullElseGet(allImplements, ArrayList::new));
            implementList = new ArrayList<>(this.allImplements);
            list = new JList<>((T[]) implementList.toArray());

            list.addListSelectionListener(this::logChange);

            setViewportView(list);
            setPreferredSize(new Dimension(260, 160));
        }

        public void add(T implement) {
            if (implementList.contains(implement)) return;

            if (implement instanceof Combatant c)
                scenarioQtyList.put(c, 1);

            implementList.add(implement);
            refresh();
        }

        public void remove(T implement) {
            implementList.remove(implement);
            refresh();
        }

        @SuppressWarnings("unchecked")
        public void updateSourceList(List<T> newList) {
            implementList.clear();
            implementList.addAll(newList);
            list.setListData((T[]) implementList.toArray());
        }

        public boolean contains(T o) {
            return implementList.contains(o);
        }

        public List<T> getList() {
            return implementList;
        }

        @SuppressWarnings("unchecked")
        private void refresh() {
            list.setListData((T[]) implementList.toArray());
            revalidate();
            repaint();
        }

        @SuppressWarnings("unchecked")
        public void logSearch(String search) {
            String searchFormatted = search.toLowerCase().replace(" ", "");

            implementList.clear();

            var implementsMatchingSearch = allImplements.stream()
                    .filter(item -> {
                        String itemFormatted = item.toString().toLowerCase().replace(" ", "");
                        return itemFormatted.contains(searchFormatted);
                    })
                    .toList();

            implementList.addAll(implementsMatchingSearch);

            list.setListData((T[]) implementList.toArray());

            list.revalidate();
            list.repaint();
        }

        private void logChange(ListSelectionEvent e) {
            if (e.getValueIsAdjusting()) return;

            T selection = list.getSelectedValue();

            if (selection == null) return;

            if (selection instanceof Implement || isAvailableList) {
                root.swapWithOtherList(selection);
                return;
            }

            NPC npc = (NPC) selection;

            int input = Message.editOrRemoveOption(npc.name());
            if (input == 1)
                root.swapWithOtherList(list.getSelectedValue());
            else if (input == 0) {
                int qty = getWithLoopUntilInt("Set quantity of " + npc.name() + ".", "Quantity");
                scenarioQtyList.put(npc, qty);

                template("Quantity of " + npc.name() + "set to " + qty + ".");
            }
        }

    }

}