package txt_menu;

import character_info.Combatant;
import scenario_info.Battle;
import scenario_info.Scenario;

import javax.swing.*;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.util.ArrayList;

import static util.Message.template;

public class CompletedElementsList extends JPanel {

    private final TxtMenu root;

    private ScrollPane<Combatant> friendlyPane;
    private ScrollPane<Combatant> enemyPane;
    private ScrollPane<Scenario> scenarioPane;

    private static final Combatant FRIENDLY_NEW = new Combatant(
            "New Ally", -1, -1, false
    );
    private static final Combatant ENEMY_NEW = new Combatant(
            "New Enemy", -1, -1, true
    );
    private static final Scenario SCENARIO_NEW = new Scenario(
            "New Scenario", null, null
    );

    public CompletedElementsList(TxtMenu root) {
        this.root = root;
        construct();
    }

    public CompletedElementsList(Battle input, TxtMenu root) {
        this.root = root;
        construct();

        input.friendlies().forEach(friendlyPane::add);
        input.enemies().forEach(enemyPane::add);
    }

    private void construct() {
        setLayout(new GridLayout(0, 3));

        friendlyPane = new ScrollPane<>(FRIENDLY_NEW, root, this);
        enemyPane = new ScrollPane<>(ENEMY_NEW, root, this);
        scenarioPane = new ScrollPane<>(SCENARIO_NEW, root, this);

        add(getPanel("Party and Allies:", friendlyPane));
        add(getPanel("Enemies:", enemyPane));
        add(getPanel("Scenarios:", scenarioPane));
    }

    public void addFriendly(Combatant combatant) {
        friendlyPane.add(combatant);
    }

    public ArrayList<Object> getFriendlyNPCs() {
        ArrayList<Object> friendlies = new ArrayList<>();
        for (int i = 0; i < friendlyPane.model.getSize(); i++) {
            friendlies.add(friendlyPane.model.elementAt(i));
        }
        friendlies.removeIf(friendly -> !((Combatant) friendly).isNPC());
        return friendlies;
    }

    public void addEnemy(Combatant combatant) {
        enemyPane.add(combatant);
    }

    public ArrayList<Object> getEnemies() {
        ArrayList<Object> enemies = new ArrayList<>();
        for (int i = 0; i < enemyPane.model.getSize(); i++) {
            enemies.add(enemyPane.model.elementAt(i));
        }
        return enemies;
    }

    public void addScenario(Scenario scenario) {
        scenarioPane.add(scenario);
    }

    public void findAndLocateCopy(Combatant copy) {
        friendlyPane.remove(copy);
        enemyPane.remove(copy);
    }

    public boolean isNotEnoughCombatants() {
        return friendlyPane.model.isEmpty() || enemyPane.model.isEmpty();
    }

    private JPanel getPanel(String labelText, JScrollPane mainPanel) {
        JPanel panel = new JPanel(new BorderLayout());

        JButton label = new JButton(labelText);
        label.setEnabled(false);
        label.setBorder(null);
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

            listener = listener();
            list.addListSelectionListener(listener);

            setViewportView(list);
        }

        public void add(T element) {
            list.removeListSelectionListener(listener);

            T existingElement = hasWithName(element);
            if (existingElement != null) {
                model.removeElement(existingElement);
            }

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
                if (!e.getValueIsAdjusting()) {
                    T selectedValue = list.getSelectedValue();

                    boolean isNew = selectedValue.equals(NEW_OPTION);

                    String name;
                    Combatant selectedCombatant = null;
                    Scenario selectedScenario = null;
                    if (selectedValue instanceof Combatant combatant) {
                        name = combatant.name();
                        selectedCombatant = combatant;
                    } else {
                        if (parent.isNotEnoughCombatants()) {
                            template("Add more combatants to create scenarios!");
                            return;
                        }

                        name = ((Scenario) selectedValue).name();
                        selectedScenario = (Scenario) selectedValue;
                    }

                    String message = "Would you like to edit " + name + "?";
                    if (isNew) {
                        message = "Would you like to add a " + name.toLowerCase() + "?";
                    }

                    int result = JOptionPane.showConfirmDialog(
                            root,
                            message,
                            TxtMenu.TITLE,
                            JOptionPane.YES_NO_OPTION
                    );
                    if (result == JOptionPane.YES_OPTION) {
                        if (selectedValue instanceof Combatant) {
                            root.editCombatant(selectedCombatant, isNew);
                        } else {
                            root.editScenario(selectedScenario, isNew);
                        }
                    }
                }
            };
        }

        private T hasWithName(T newElement) {
            for (int i = 0; i < model.getSize(); i++) {
                T element = model.getElementAt(i);

                if (newElement instanceof Combatant newCombatant) {
                    String elementName = ((Combatant) element).name();

                    if (elementName.equals(newCombatant.name())) {
                        return model.getElementAt(i);
                    }
                } else if (newElement instanceof Scenario newScenario) {
                    String elementName = ((Scenario) element).name();

                    if (elementName.equals(newScenario.name())) {
                        return model.getElementAt(i);
                    }
                }
            }
            return null;
        }

    }

}