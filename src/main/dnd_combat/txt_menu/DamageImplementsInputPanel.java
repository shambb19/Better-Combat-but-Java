package txt_menu;

import character_info.Combatant;
import damage_implements.Spell;
import damage_implements.Weapon;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Objects;

public class DamageImplementsInputPanel extends JPanel {

    private final boolean isWeapons;

    private final ImplementListPane availableList;
    private final ImplementListPane selectedList;

    public DamageImplementsInputPanel(boolean isWeapons) {
        this.isWeapons = isWeapons;

        setLayout(new GridLayout(0, 2));

        String listName;
        if (isWeapons) {
            availableList = new ImplementListPane(Weapon.getAllAsList(), this);
            listName = "Weapons";
        } else {
            availableList = new ImplementListPane(Spell.getAllAsList(), this);
            listName = "Spells";
        }

        selectedList = new ImplementListPane(null, this);

        add(new JLabel("Available " + listName + ":"));
        add(new JLabel("Selected " + listName + ":"));
        add(availableList);
        add(selectedList);
    }

    public ArrayList<Weapon> getSelectedAsWeapons() {
        ArrayList<Weapon> weaponList = new ArrayList<>();
        for (Object weapon : selectedList.getList()) {
            weaponList.add((Weapon) weapon);
        }
        weaponList.removeIf(Objects::isNull);
        return weaponList;
    }

    public ArrayList<Spell> getSelectedAsSpells() {
        ArrayList<Spell> spellList = new ArrayList<>();
        for (Object spell : selectedList.getList()) {
            spellList.add((Spell) spell);
        }
        return spellList;
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

    public void setTo(Combatant combatant) {
        reset();
        availableList.getList().forEach(implement -> {
            boolean isHasImplement = (isWeapons)
                    ? combatant.hasWeapon(implement)
                    : combatant.hasSpell(implement);
            if (isHasImplement) {
                swapWithOtherList(implement);
            }
        });
    }

    static class ImplementListPane extends JScrollPane {

        private final ArrayList<Object> implementList;
        private final JList<Object> list;

        public ImplementListPane(ArrayList<Object> allImplements, DamageImplementsInputPanel root) {
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