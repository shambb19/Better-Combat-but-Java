package campaign_creator_menu.input;

import _global_list.DamageImplements;
import combat_object.combatant.NPC;
import combat_object.combatant.PC;
import combat_object.damage_implements.Implement;
import combat_object.damage_implements.Spell;
import combat_object.damage_implements.Weapon;
import format.swing_comp.SwingPane;
import swing_custom.ValidatedField;
import util.Filter;
import util.Message;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import static format.swing_comp.SwingPane.*;

public class ListSelectionPanel<T> extends JPanel {

    private final ImplementListPane<T> availableList, selectedList;
    protected final ValidatedField searchBar;

    @SuppressWarnings("unchecked")
    public static <S extends Implement> ListSelectionPanel<S> implementsFilteredFor(
            Class<S> sourceType, String name, PC referenceCombatant
    ) {
        ListSelectionPanel<S> selectionPanel = new ListSelectionPanel<>(DamageImplements.toList(sourceType), name);

        for (Weapon weapon : referenceCombatant.getWeapons())
            selectionPanel.swapWithOtherList((S) weapon);
        for (Spell spell : referenceCombatant.getSpells())
            selectionPanel.swapWithOtherList((S) spell);

        return selectionPanel;
    }

    public ListSelectionPanel(List<T> sourceList, String name) {
        SwingPane.fluent(this).arrangedAs(SINGLE_ROW, 20, 0);

        ArrayList<T> sourceWithoutManual = new ArrayList<>(sourceList);
        sourceWithoutManual.removeIf(item -> item.toString().startsWith("Manual"));

        availableList = new ImplementListPane<>(sourceWithoutManual, this, true);
        selectedList = new ImplementListPane<>(null, this, false);

        searchBar = new ValidatedField(name, null);
        searchBar.setValidator(availableList::logSearch);

        JLabel selectedLabel = label("Selected " + name + ":", Font.PLAIN, 13f)
                .withEmptyBorder(10, 10, 10, 10).component();

        panelIn(this).arrangedAs(BORDER).borderCollect(
                north(searchBar), center(availableList));

        panelIn(this).arrangedAs(BORDER).borderCollect(
                north(selectedLabel), center(selectedList));
    }

    public List<T> getSelected() {
        return Filter.filteredBy(selectedList.getList(), Objects::nonNull);
    }

    public HashMap<String, Integer> getSelectedScenario() {
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

        private final HashMap<String, Integer> scenarioQtyList = new HashMap<>();

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

            if (implement instanceof NPC c)
                scenarioQtyList.put(c.getName(), 1);

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
        public boolean logSearch(String search) {
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

            return !searchFormatted.isBlank() && !implementsMatchingSearch.isEmpty();
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

            String name = npc.getName();
            int input = Message.showEditOrRemovePrompt(name);
            if (input == 1)
                root.swapWithOtherList(list.getSelectedValue());
            else if (input == 0) {
                int qty = Message.promptIntWithLoop("Set quantity of " + name + ".", "Quantity");
                scenarioQtyList.put(npc.getName(), qty);

                Message.showAsInfoMessage("Quantity of " + name + " set to " + qty + ".");
            }
        }

    }

}