package combat_menu.action_panel;

import __main.CombatMain;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class InspirationPanel extends JPanel {

    private final ActionPanel root;

    public static InspirationPanel newInstance(ActionPanel root) {
        return new InspirationPanel(root);
    }

    private InspirationPanel(ActionPanel root) {
        this.root = root;

        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(10, 10, 10, 10));

        JLabel label = new JLabel("Select a 1d4 Excess Inspiration Roll:");
        label.setAlignmentX(CENTER_ALIGNMENT);

        JPanel buttonPanel = new JPanel(new GridLayout(0, 2));
        buttonPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        for (int i = 0; i < 4; i++) {
            int roll = i + 1;
            JButton button = new JButton(String.valueOf(roll));
            button.putClientProperty("FlatLaf.style", "font: $h00.font");
            button.setBorder(new EmptyBorder(10, 10, 10, 10));
            button.addActionListener(e -> logRoll(roll));
            buttonPanel.add(button);
        }

        add(label, BorderLayout.NORTH);
        add(buttonPanel, BorderLayout.CENTER);
    }

    private void logRoll(int roll) {
        CombatMain.getMenu().logInspiration(roll);
        root.returnToButtons();
    }

}
