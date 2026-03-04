package txt_menu;

import character_info.Combatant;
import scenario_info.Battle;

import javax.swing.*;
import javax.swing.event.ListSelectionListener;
import java.awt.*;

public class CompletedCombatantLists extends JPanel {

    private final TxtMenu root;

    private TeamScrollPane friendlyPane;
    private TeamScrollPane enemyPane;

    private static final Combatant FRIENDLY_NEW = new Combatant(
        "New Ally", -1, -1, false
    );
    private static final Combatant ENEMY_NEW = new Combatant(
        "New Enemy", -1, -1, true
    );

    public CompletedCombatantLists(TxtMenu root) {
        this.root = root;
        construct();
    }

    public CompletedCombatantLists(Battle input, TxtMenu root) {
        this.root = root;
        construct();

        input.friendlies().forEach(friendlyPane::add);
        input.enemies().forEach(enemyPane::add);
    }

    private void construct() {
        setLayout(new GridLayout(0, 2));

        friendlyPane = new TeamScrollPane(false, this);
        enemyPane = new TeamScrollPane(true, this);

        add(getTeamPanel("Party and Allies:", friendlyPane));
        add(getTeamPanel("Enemies:", enemyPane));
    }

    public void addFriendly(Combatant combatant) {
        friendlyPane.add(combatant);
    }

    public void addEnemy(Combatant combatant) {
        enemyPane.add(combatant);
    }

    public void findAndLocateCopy(Combatant copy) {
        friendlyPane.remove(copy);
        enemyPane.remove(copy);
    }

    public void logSelection(Combatant selection, String message, boolean isNew) {
        int result = JOptionPane.showConfirmDialog(
            root,
            message,
            TxtMenu.TITLE,
            JOptionPane.YES_NO_OPTION
        );
        if (result == JOptionPane.YES_OPTION) {
            root.editCombatant(selection, isNew);
        }
    }

    private JPanel getTeamPanel(String labelText, JScrollPane mainPanel) {
        JPanel panel = new JPanel(new BorderLayout());

        JButton label = new JButton(labelText);
        label.setEnabled(false);
        label.setBorder(null);
        label.putClientProperty("FlatLaf.style", "font: $h1.regular.font");

        panel.add(label, BorderLayout.NORTH);
        panel.add(mainPanel, BorderLayout.CENTER);

        return panel;
    }

    static class TeamScrollPane extends JScrollPane {

        private final CompletedCombatantLists root;

        private final Combatant NEW_OPTION;

        private final JList<Combatant> list;
        private final DefaultListModel<Combatant> model;
        private final ListSelectionListener listener;

        public TeamScrollPane(boolean isEnemies, CompletedCombatantLists root) {
            this.root = root;

            if (isEnemies) {
                NEW_OPTION = ENEMY_NEW;
            } else {
                NEW_OPTION = FRIENDLY_NEW;
            }

            model = new DefaultListModel<>();
            model.addElement(NEW_OPTION);

            list = new JList<>(model);
            list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

            listener = getListener();
            list.addListSelectionListener(listener);

            setViewportView(list);
        }

        public void add(Combatant combatant) {
            list.removeListSelectionListener(listener);

            Combatant existingCombatant = hasWithName(combatant);
            if (existingCombatant != null) {
                model.removeElement(existingCombatant);
            }

            model.addElement(combatant);
            moveTemplateToLast();

            list.addListSelectionListener(listener);

            revalidate();
            repaint();
        }

        public void remove(Combatant combatant) {
            list.removeListSelectionListener(listener);

            model.removeElement(combatant);
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

        private void logSelection(Combatant selection) {
            boolean isNew = selection.equals(NEW_OPTION);

            String message = "Would you like to edit " + selection.name() + "?";
            if (isNew) {
                message = "Would you like to add a " + selection.name().toLowerCase() + "?";
            }
            root.logSelection(selection, message, isNew);
        }

        private ListSelectionListener getListener() {
            return e -> {
                if (!e.getValueIsAdjusting()) {
                    logSelection(list.getSelectedValue());
                }
            };
        }

        private Combatant hasWithName(Combatant newCombatant) {
            for (int i = 0; i < model.getSize(); i++) {
                if (newCombatant.name().equals(model.getElementAt(i).name())) {
                    return model.getElementAt(i);
                }
            }
            return null;
        }

    }

}
