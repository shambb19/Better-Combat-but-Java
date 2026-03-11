package campaign_creator;

import character_info.combatant.Combatant;
import character_info.combatant.NPC;
import util.Message;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import static util.Message.getWithLoopUntilInt;
import static util.Message.template;

public class ListSelectionPanel<T> extends JPanel {

    private final ImplementListPane availableList;
    private final ImplementListPane selectedList;

    public ListSelectionPanel(ArrayList<Object> sourceList, String name) {
        setLayout(new BorderLayout());

        availableList = new ImplementListPane(sourceList, this, false);
        selectedList = new ImplementListPane(null, this, true);

        JLabel available = new JLabel("Available " + name + ":");
        JLabel selected = new JLabel("Selected " + name + ":");

        add(horizontalPanelWith(available, selected), BorderLayout.NORTH);
        add(horizontalPanelWith(availableList, selectedList));
    }

    @SuppressWarnings("unchecked")
    public ArrayList<T> getSelected() {
        ArrayList<T> list = new ArrayList<>();
        for (Object item : selectedList.getList()) {
            list.add((T) item);
        }
        list.removeIf(Objects::isNull);
        return list;
    }

    public HashMap<Combatant, Integer> getSelectedScenario() {
        return selectedList.scenarioQtyList;
    }

    public void swapWithOtherList(Object swappedImplement) {
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

    public void setTo(ArrayList<T> selectedList) {
        reset();
        selectedList.forEach(item -> {
            if (availableList.contains(item)) {
                swapWithOtherList(item);
            }
        });
    }

    public void updateSourceList(ArrayList<Object> newSource) {
        reset();
        availableList.updateSourceList(newSource);
    }

    static class ImplementListPane extends JScrollPane {

        private final ListSelectionPanel root;

        private final boolean isSelectedList;

        private final ArrayList<Object> implementList;
        private final JList<Object> list;

        private final HashMap<Combatant, Integer> scenarioQtyList = new HashMap<>();

        @SuppressWarnings("all")
        public ImplementListPane(ArrayList<Object> allImplements, ListSelectionPanel root, boolean isSelectedList) {
            this.root = root;
            this.isSelectedList = isSelectedList;

            implementList = Objects.requireNonNullElseGet(allImplements, ArrayList::new);
            list = new JList<>(implementList.toArray());

            list.addListSelectionListener(e -> listener());

            setViewportView(list);
        }

        public void add(Object implement) {
            if (implementList.contains(implement)) {
                return;
            }
            if (implement instanceof Combatant c) {
                scenarioQtyList.put(c, 1);
            }
            implementList.add(implement);
            refresh();
        }

        public void remove(Object implement) {
            implementList.remove(implement);
            refresh();
        }

        public void updateSourceList(ArrayList<Object> newList) {
            implementList.clear();
            implementList.addAll(newList);
            list.setListData(implementList.toArray());
        }

        public boolean contains(Object o) {
            return implementList.contains(o);
        }

        public ArrayList<Object> getList() {
            return implementList;
        }

        private void refresh() {
            list.setListData(implementList.toArray());
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