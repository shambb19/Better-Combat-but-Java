package campaign_creator_menu;

import character_info.combatant.Combatant;
import character_info.combatant.NPC;
import character_info.combatant.PC;
import encounter_info.Battle;
import encounter_info.Scenario;
import format.swing_comp.SwingComp;
import util.Filter;
import util.Message;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static format.swing_comp.SwingComp.HEADER;
import static format.swing_comp.SwingComp.button;
import static format.swing_comp.SwingPane.*;

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

    private final ScrollPane<Combatant> friendlyPane;
    private final ScrollPane<Combatant> enemyPane;
    private final ScrollPane<Scenario> scenarioPane;

    public CompletedElementsList(Battle input, CampaignCreatorMenu root) {

        friendlyPane = new ScrollPane<>(FRIENDLY_NEW, root, this);
        enemyPane = new ScrollPane<>(ENEMY_NEW, root, this);
        scenarioPane = new ScrollPane<>(SCENARIO_NEW, root, this);

        modifiable(this).collect(
                        getPanel("Party and Allies:", friendlyPane),
                        getPanel("Enemies:", enemyPane),
                        getPanel("Scenarios:", scenarioPane)
                )
                .withLayout(SINGLE_ROW)
                .withLabeledBorder("Completed Elements");

        input.friendlies().forEach(friendlyPane::add);
        input.enemies().forEach(enemyPane::add);
    }

    /**
     * @param combatant the completed combatant that will be added to its pane
     */
    public void addCombatant(Combatant combatant) {
        if (combatant.isEnemy())
            enemyPane.add(combatant);
        else
            friendlyPane.add(combatant);
    }

    public List<Combatant> getFriendlyNPCs() {
        return filledList(friendlyPane.model)
                .stream()
                .filter(combatant -> combatant instanceof NPC)
                .toList();
    }

    public List<Combatant> getEnemies() {
        return filledList(enemyPane.model);
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
        JButton label = button(labelText, null)
                .withEmptyBorder(4)
                .withFont(HEADER)
                .disabled()
                .build();

        return panel().withLayout(BORDER)
                .with(label, BorderLayout.NORTH)
                .with(mainPanel, BorderLayout.CENTER)
                .build();
    }

    private ArrayList<Combatant> filledList(DefaultListModel<Combatant> source) {
        ArrayList<Combatant> destination = new ArrayList<>();
        Arrays.stream(source.toArray())
                .forEach(item -> destination.add((Combatant) item));

        return destination;
    }

    static class ScrollPane<T> extends JScrollPane {

        private final CampaignCreatorMenu root;
        private final CompletedElementsList parent;

        private final T NEW_OPTION;

        private final JList<T> list;
        private final DefaultListModel<T> model;
        private final ListSelectionListener listener;

        public ScrollPane(T newOption, CampaignCreatorMenu root, CompletedElementsList parent) {
            this.root = root;
            this.parent = parent;

            NEW_OPTION = newOption;

            model = new DefaultListModel<>();
            model.addElement(NEW_OPTION);

            list = SwingComp.list(model).withEmptyBorder(4).build();

            listener = this::logSelection;
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

        private void logSelection(ListSelectionEvent e) {
            if (e.getValueIsAdjusting())
                return;

            if (list.getSelectedValue() == null)
                return;

            T selectedValue = list.getSelectedValue();
            list.clearSelection();

            if (selectedValue instanceof Scenario && parent.isNotEnoughForScenario()) {
                Message.template("Add more Combatants!");
                return;
            }

            if (selectedValue.equals(NEW_OPTION)) {
                root.logEdit(selectedValue, true);
                return;
            }

            int route = Message.editOrRemoveOption(selectedValue.toString());

            if (route == 0)
                root.logEdit(selectedValue, false);
            else if (route == 1)
                remove(selectedValue);
        }

        @SuppressWarnings("unchecked")
        private T hasWithName(T newElement) {
            List<T> modelList = (List<T>) Arrays.asList(model.toArray());

            return Filter.firstWithToStringEquals(modelList, newElement.toString());
        }

    }

}