package combat_menu.action_panel;

import __main.Main;
import character_info.combatant.Combatant;
import character_info.combatant.NPC;
import format.ColorStyle;
import format.swing_comp.SwingComp;
import format.swing_comp.SwingPane;
import util.Locators;

import javax.swing.*;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import java.awt.*;
import java.util.List;
import java.util.Vector;

public class HealPanel extends JPanel {

    private final ActionPanel root;
    private static final NPC DEFAULT_SELECTION = new NPC("Select a Combatant", 0, 0, false);

    private final JComboBox<Combatant> targetComboBox;
    private final JSlider healthSlider;
    private final JProgressBar healthBar;

    public static HealPanel newInstance(ActionPanel root) {
        return new HealPanel(root);
    }

    private HealPanel(ActionPanel root) {
        this.root = root;

        addAncestorListener(resetOnSelection());

        targetComboBox = new JComboBox<>(new Vector<>(getEligibleCombatants()));
        setupComboBoxAppearance();
        targetComboBox.addActionListener(e -> handleSelectionChange());

        healthBar = SwingComp.progressBar(0, 100, 0, SwingConstants.HORIZONTAL)
                .withSize(0, 25)
                .bold(12f)
                .applied(b -> b.setStringPainted(true))
                .build();

        healthSlider = new JSlider(0, 100, 0);
        healthSlider.setEnabled(false);
        healthSlider.addChangeListener(e -> updateHealPreview());

        JPanel confirmCancelPanel = SwingComp.button("Heal", this::logAndContinue)
                .withBackground(ColorStyle.DARKER_GREEN.getColor())
                .withCancelOption(root::returnToButtons)
                .build();

        SwingPane.modifiable(this)
                .collect(targetComboBox, createActionCenter(), confirmCancelPanel)
                .withLayout(SwingPane.VERTICAL_BOX)
                .withEmptyBorder(15);
    }

    private JPanel createActionCenter() {
        JLabel header = SwingComp.label("Drag to Change Heal Amount")
                .withEmptyBorder(5)
                .withForeground(Color.GRAY)
                .build();

        JPanel sliderContainer = SwingPane.panel().collect(header, SwingComp.gap(10), healthSlider)
                .withLayout(SwingPane.VERTICAL_BOX)
                .build();

        return SwingPane.panel().collect(header, sliderContainer).withLayout(SwingPane.VERTICAL_BOX).build();
    }

    private void updateHealPreview() {
        Combatant target = (Combatant) targetComboBox.getSelectedItem();
        if (target == null) return;

        int projectedHp = healthSlider.getValue();
        int healAmount = projectedHp - target.hp();

        healthBar.setValue(projectedHp);
        healthBar.setString(String.format("%d / %d (+%d HP)", projectedHp, target.maxHp(), healAmount));

        healthBar.setForeground(ColorStyle.getPercentColor(projectedHp, target.maxHp()));
    }

    private void handleSelectionChange() {
        Combatant selected = (Combatant) targetComboBox.getSelectedItem();
        if (selected == null || selected == DEFAULT_SELECTION) return;

        healthBar.setMinimum(0);
        healthBar.setMaximum(selected.maxHp());

        healthSlider.setEnabled(true);
        healthSlider.setMinimum(selected.hp());
        healthSlider.setMaximum(selected.maxHp());
        healthSlider.setValue(selected.hp());

        updateHealPreview();
    }

    private void logAndContinue() {
        try {
            Combatant target = (Combatant) targetComboBox.getSelectedItem();
            if (target == null) return;

            int healAmount = healthSlider.getValue() - target.hp();
            target.heal(healAmount);
            Main.logAction();
            root.returnToButtons();
        } catch (Exception ignored) {
        }
    }

    private void setupComboBoxAppearance() {
        targetComboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object val, int i, boolean isSel, boolean cellFocus) {
                super.getListCellRendererComponent(list, val, i, isSel, cellFocus);
                if (val instanceof Combatant c)
                    setText(String.format("%s (%d/%d HP)", c.name(), c.hp(), c.maxHp()));
                else
                    setText("Choose a combatant...");

                return this;
            }
        });
        targetComboBox.setSelectedIndex(-1);
    }

    private List<Combatant> getEligibleCombatants() {
        return Locators.getTargetList(false).stream()
                .filter(c -> c == DEFAULT_SELECTION || c.hp() < c.maxHp())
                .toList();
    }

    private AncestorListener resetOnSelection() {
        return new AncestorListener() {
            @Override
            public void ancestorAdded(AncestorEvent e) {
                targetComboBox.setSelectedIndex(-1);
                healthSlider.setEnabled(false);
                healthBar.setValue(0);
                healthBar.setString("");
            }

            public void ancestorRemoved(AncestorEvent e) {
            }

            public void ancestorMoved(AncestorEvent e) {
            }
        };
    }
}