package campaign_creator;

import character_info.combatant.Combatant;
import character_info.combatant.NPC;
import util.Message;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import static util.Message.getWithLoopUntilInt;
import static util.Message.template;

public class ListSelectionPanel<T> extends JPanel {

    private final ImplementListPane<T> availableList;
    private final ImplementListPane<T> selectedList;

    public ListSelectionPanel(ArrayList<T> sourceList, String name) {
        setLayout(new BorderLayout());

        availableList = new ImplementListPane<>(sourceList, this, false);
        selectedList = new ImplementListPane<>(null, this, true);

        JLabel available = new JLabel("Available " + name + ":");
        JLabel selected = new JLabel("Selected " + name + ":");

        add(horizontalPanelWith(available, selected), BorderLayout.NORTH);
        add(horizontalPanelWith(availableList, selectedList));
    }

    public ArrayList<T> getSelected() {
        ArrayList<T> list = new ArrayList<>(selectedList.getList());
        list.removeIf(Objects::isNull);
        return list;
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
        while (!selectedList.getList().isEmpty()) {
            swapWithOtherList(selectedList.getList().removeFirst());
        }
    }

    public void setTo(List<T> selectedList) {
        reset();
        selectedList.forEach(item -> {
            if (availableList.contains(item)) {
                swapWithOtherList(item);
            }
        });
    }

    public void updateSourceList(ArrayList<T> newSource) {
        reset();
        availableList.updateSourceList(newSource);
    }

    static class ImplementListPane<T> extends JScrollPane {

        private final ListSelectionPanel<T> root;

        private final boolean isSelectedList;

        private final ArrayList<T> implementList;
        private final JList<T> list;

        private final HashMap<Combatant, Integer> scenarioQtyList = new HashMap<>();

        @SuppressWarnings("all")
        public ImplementListPane(ArrayList<T> allImplements, ListSelectionPanel root, boolean isSelectedList) {
            this.root = root;
            this.isSelectedList = isSelectedList;

            implementList = Objects.requireNonNullElseGet(allImplements, ArrayList::new);
            list = new JList<>((T[]) implementList.toArray());

            list.addListSelectionListener(e -> listener());

            setViewportView(list);
        }

        public void add(T implement) {
            if (implementList.contains(implement)) {
                return;
            }
            if (implement instanceof Combatant c) {
                scenarioQtyList.put(c, 1);
            }
            implementList.add(implement);
            refresh();
        }

        public void remove(T implement) {
            implementList.remove(implement);
            refresh();
        }

        @SuppressWarnings("unchecked")
        public void updateSourceList(ArrayList<T> newList) {
            implementList.clear();
            implementList.addAll(newList);
            list.setListData((T[]) implementList.toArray());
        }

        public boolean contains(T o) {
            return implementList.contains(o);
        }

        public ArrayList<T> getList() {
            return implementList;
        }

        @SuppressWarnings("unchecked")
        private void refresh() {
            list.setListData((T[]) implementList.toArray());
            revalidate();
            repaint();
        }

        private void listener() {
            if (!isSelectedList) {
                root.swapWithOtherList(list.getSelectedValue());
                return;
            }
            if (!(list.getSelectedValue() instanceof Combatant combatant)) {
                return;
            }
            if (!(combatant instanceof NPC npc)) {
                template("Quantity of PCs is limited to 1.");
                return;
            }
            int input = Message.editOrRemoveOption(npc.name());
            if (input == 1) {
                root.swapWithOtherList(list.getSelectedValue());
            } else if (input == 0) {
                int qty = getWithLoopUntilInt("Set quantity of " + npc.name() + ".", "Quantity");
                scenarioQtyList.put(combatant, qty);
                template("Quantity set to " + qty + ".");
            }
        }

    }

    private JPanel horizontalPanelWith(JComponent left, JComponent right) {
        JPanel panel = new JPanel(new GridLayout(0, 2));
        panel.add(left);
        panel.add(right);
        return panel;
    }

}