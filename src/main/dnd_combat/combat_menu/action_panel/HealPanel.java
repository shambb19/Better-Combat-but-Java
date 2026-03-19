package combat_menu.action_panel;

import __main.CombatMain;
import character_info.combatant.Combatant;
import character_info.combatant.NPC;
import format.ColorStyle;
import format.SwingStyles;
import util.Locators;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
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
    private final JButton okButton;

    public static HealPanel newInstance(ActionPanel root) {
        return new HealPanel(root);
    }

    private HealPanel(ActionPanel root) {
        this.root = root;

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(new EmptyBorder(15, 15, 15, 15));
        addAncestorListener(resetOnSelection());

        targetComboBox = new JComboBox<>(new Vector<>(getEligibleCombatants()));
        setupComboBoxAppearance();
        targetComboBox.addActionListener(e -> handleSelectionChange());

        healthBar = new JProgressBar(SwingConstants.HORIZONTAL);
        healthBar.setStringPainted(true);
        healthBar.setPreferredSize(new Dimension(0, 25));
        healthBar.setFont(new Font("SansSerif", Font.BOLD, 12));

        healthSlider = new JSlider(0, 100, 0);
        healthSlider.setEnabled(false);
        healthSlider.addChangeListener(e -> updateHealPreview());

        okButton = new JButton("Select a Combatant");
        okButton.setBackground(ColorStyle.GREEN_APPLE.getColor());
        okButton.setForeground(Color.BLACK);
        okButton.setEnabled(false);

        JPanel confirmCancelPanel = SwingStyles.getConfirmCancelPanel(
                okButton,
                e -> logAndContinue(),
                e -> root.returnToButtons()
        );

        add(targetComboBox);
        add(createActionCenter());
        add(confirmCancelPanel);
    }

    private JPanel createActionCenter() {
        JPanel center = new JPanel();
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));

        JLabel header = new JLabel("Drag to Change Heal Amount");
        header.setForeground(Color.GRAY);
        header.setBorder(new EmptyBorder(0, 5, 0, 0));

        JPanel sliderContainer = new JPanel();
        sliderContainer.setLayout(new BoxLayout(sliderContainer, BoxLayout.Y_AXIS));
        sliderContainer.add(healthBar);
        sliderContainer.add(Box.createVerticalStrut(10));
        sliderContainer.add(healthSlider);

        center.add(header);
        center.add(sliderContainer);
        return center;
    }

    private void updateHealPreview() {
        Combatant target = (Combatant) targetComboBox.getSelectedItem();
        if (target == null) return;

        int projectedHp = healthSlider.getValue();
        int healAmount = projectedHp - target.hp();

        healthBar.setValue(projectedHp);
        healthBar.setString(String.format("%d / %d (+%d HP)", projectedHp, target.maxHp(), healAmount));

        healthBar.setForeground(ColorStyle.getPercentColor(projectedHp, target.maxHp()));

        okButton.setText("Heal " + target.name() + " for " + healAmount + " HP");
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

        okButton.setEnabled(true);
        updateHealPreview();
    }

    private void logAndContinue() {
        try {
            Combatant target = (Combatant) targetComboBox.getSelectedItem();
            if (target != null) {
                int healAmount = healthSlider.getValue() - target.hp();
                target.heal(healAmount);
                CombatMain.logAction();
                root.returnToButtons();
            }
        } catch (Exception ignored) {
        }
    }

    private void setupComboBoxAppearance() {
        targetComboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object val, int i, boolean isSel, boolean cellFocus) {
                super.getListCellRendererComponent(list, val, i, isSel, cellFocus);
                if (val instanceof Combatant c) {
                    setText(String.format("%s (%d/%d HP)", c.name(), c.hp(), c.maxHp()));
                } else {
                    setText("Choose a combatant...");
                }
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
                okButton.setText("Select a Combatant");
                okButton.setEnabled(false);
            }

            public void ancestorRemoved(AncestorEvent e) {
            }

            public void ancestorMoved(AncestorEvent e) {
            }
        };
    }
}