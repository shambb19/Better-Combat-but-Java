package txt_menu;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Objects;

public class ListSelectionPanel<T> extends JPanel {

    private final ImplementListPane availableList;
    private final ImplementListPane selectedList;

    public ListSelectionPanel(ArrayList<Object> sourceList, String name) {
        setLayout(new GridLayout(0, 2));

        availableList = new ImplementListPane(sourceList, this);
        selectedList = new ImplementListPane(null, this);

        add(new JLabel("Available " + name + ":"));
        add(new JLabel("Selected " + name + ":"));
        add(availableList);
        add(selectedList);
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

        private final ArrayList<Object> implementList;
        private final JList<Object> list;

        @SuppressWarnings("all")
        public ImplementListPane(ArrayList<Object> allImplements, ListSelectionPanel root) {
            implementList = Objects.requireNonNullElseGet(allImplements, ArrayList::new);
            list = new JList<>(implementList.toArray());
            list.addListSelectionListener(e -> root.swapWithOtherList(list.getSelectedValue()));
            setViewportView(list);
        }

        public void add(Object implement) {
            if (implementList.contains(implement)) {
                return;
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

    }

}