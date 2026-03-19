package campaign_creator_menu;

import character_info.combatant.Combatant;
import character_info.combatant.NPC;
import character_info.combatant.PC;
import encounter_info.Battle;
import encounter_info.Scenario;
import format.SwingStyles;
import util.Message;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class CompletedElementsList extends JPanel {

    private static final Combatant FRIENDLY_NEW = new NPC(
            "New Ally", -1, -1, false
    );
    private static final Combatant ENEMY_NEW = new NPC(
            "New Enemy", -1, -1, true
    );
    private static final Scenario SCENARIO_NEW = new Scenario(
            "New Scenario", new HashMap<>(), new HashMap<>()
    );

    private final TxtMenu root;
    private ScrollPane<Combatant> friendlyPane;
    private ScrollPane<Combatant> enemyPane;
    private ScrollPane<Scenario> scenarioPane;

    public CompletedElementsList(Battle input, TxtMenu root) {
        this.root = root;
        construct();

        input.friendlies().forEach(friendlyPane::add);
        input.enemies().forEach(enemyPane::add);
    }

    private void construct() {
        setLayout(new GridLayout(0, 3));
        SwingStyles.addLabeledBorder(this, "Completed Elements");

        friendlyPane = new ScrollPane<>(FRIENDLY_NEW, root, this);
        enemyPane = new ScrollPane<>(ENEMY_NEW, root, this);
        scenarioPane = new ScrollPane<>(SCENARIO_NEW, root, this);

        add(getPanel("Party and Allies:", friendlyPane));
        add(getPanel("Enemies:", enemyPane));
        add(getPanel("Scenarios:", scenarioPane));
    }

    /**
     * @param combatant the completed combatant that will be added to its pane
     */
    public void addCombatant(Combatant combatant) {
        if (combatant.isEnemy()) {
            enemyPane.add(combatant);
        } else {
            friendlyPane.add(combatant);
        }
    }

    public ArrayList<Combatant> getFriendlyNPCs() {
        ArrayList<Combatant> friendlies = new ArrayList<>();
        for (int i = 0; i < friendlyPane.model.getSize(); i++) {
            friendlies.add(friendlyPane.model.elementAt(i));
        }
        friendlies.removeIf(friendly -> friendly instanceof PC);
        return friendlies;
    }

    public ArrayList<Combatant> getEnemies() {
        ArrayList<Combatant> enemies = new ArrayList<>();
        for (int i = 0; i < enemyPane.model.getSize(); i++) {
            enemies.add(enemyPane.model.elementAt(i));
        }
        return enemies;
    }

    /**
     * @param scenario the completed scenario that will be added to its pane
     */
    public void addScenario(Scenario scenario) {
        scenarioPane.add(scenario);
    }

    public void findAndLocateCopy(Combatant copy) {
        friendlyPane.remove(copy);
        enemyPane.remove(copy);
    }

    public boolean isNotEnoughForScenario() {
        int friendlyCount = (int) Arrays.stream(friendlyPane.model.toArray()).filter(c -> c instanceof PC).count();

        return enemyPane.model.isEmpty() || friendlyCount < 1;
    }

    private JPanel getPanel(String labelText, JScrollPane mainPanel) {
        JPanel panel = new JPanel(new BorderLayout());

        JButton label = new JButton(labelText);
        label.setEnabled(false);
        label.setBorder(new EmptyBorder(4, 4, 4, 4));
        label.putClientProperty("FlatLaf.style", "font: $h1.regular.font");

        panel.add(label, BorderLayout.NORTH);
        panel.add(mainPanel, BorderLayout.CENTER);

        return panel;
    }

    static class ScrollPane<T> extends JScrollPane {

        private final TxtMenu root;
        private final CompletedElementsList parent;

        private final T NEW_OPTION;

        private final JList<T> list;
        private final DefaultListModel<T> model;
        private final ListSelectionListener listener;

        public ScrollPane(T newOption, TxtMenu root, CompletedElementsList parent) {
            this.root = root;
            this.parent = parent;

            NEW_OPTION = newOption;

            model = new DefaultListModel<>();
            model.addElement(NEW_OPTION);

            list = new JList<>(model);
            list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            list.setBorder(new EmptyBorder(4, 4, 4, 4));

            listener = listener();
            list.addListSelectionListener(listener);

            setViewportView(list);
        }

        public void add(T element) {
            list.removeListSelectionListener(listener);

            T existingElement = hasWithName(element);
            model.removeElement(existingElement);
            model.addElement(element);
            moveTemplateToLast();

            list.addListSelectionListener(listener);

            revalidate();
            repaint();
        }

        public void remove(T element) {
            list.removeListSelectionListener(listener);

            model.removeElement(element);
            moveTemplateToLast();

            list.addListSelectionListener(listener);

            revalidate();
            repaint();
        }

        private void moveTemplateToLast() {
            model.removeElement(NEW_OPTION);
            model.addElement(NEW_OPTION);

            revalidate();
            repaint();
        }

        private ListSelectionListener listener() {
            return e -> {
                if (e.getValueIsAdjusting()) {
                    return;
                }
                if (list.getSelectedValue() == null) {
                    return;
                }

                T selectedValue = list.getSelectedValue();
                list.clearSelection();

                if (selectedValue instanceof Scenario
                        && parent.isNotEnoughForScenario()) {
                    Message.template("Add more Combatants!");
                    return;
                }

                if (selectedValue.hashCode() == NEW_OPTION.hashCode()) {
                    switch (selectedValue) {
                        case Combatant c -> root.editCombatant(c, true);
                        case Scenario s -> root.editScenario(s, true);
                        default -> {
                        }
                    }
                    return;
                }

                int route = Message.editOrRemoveOption(selectedValue.toString());

                if (route == 0) {
                    switch (selectedValue) {
                        case Combatant c -> root.editCombatant(c, false);
                        case Scenario s -> root.editScenario(s, false);
                        default -> {
                        }
                    }
                } else if (route == 1) {
                    remove(selectedValue);
                }
            };
        }

        private T hasWithName(T newElement) {
            for (int i = 0; i < model.getSize(); i++) {
                T element = model.getElementAt(i);
                String elementName = element.toString();

                switch (newElement) {
                    case Combatant c when elementName.equals(c.name()) -> {
                        return model.getElementAt(i);
                    }
                    case Scenario s when elementName.equals(s.name()) -> {
                        return model.getElementAt(i);
                    }
                    default -> {
                    }
                }
            }
            return null;
        }

    }

}