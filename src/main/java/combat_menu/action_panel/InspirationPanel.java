package combat_menu.action_panel;

import __main.Main;
import format.swing_comp.SwingComp;
import format.swing_comp.SwingPane;

import javax.swing.*;
import java.awt.*;

public class InspirationPanel extends JPanel {

    private final ActionPanel root;

    public static InspirationPanel newInstance(ActionPanel root) {
        return new InspirationPanel(root);
    }

    private InspirationPanel(ActionPanel root) {
        this.root = root;

        JLabel label = SwingComp.label("Select a 1d4 Excess Inspiration Roll:").centered().build();
        JPanel buttonPanel = SwingPane.panel().withLayout(SwingPane.TWO_COLUMN).withEmptyBorder(10).build();

        for (int i = 0; i < 4; i++) {
            int roll = i + 1;
            SwingComp.button(String.valueOf(roll), () -> logRoll(roll))
                    .withFont(SwingComp.TITLE)
                    .withEmptyBorder(10)
                    .in(buttonPanel);
        }

        SwingPane.modifiable(this).withLayout(SwingPane.BORDER)
                .with(label, BorderLayout.NORTH)
                .with(buttonPanel, BorderLayout.CENTER)
                .withEmptyBorder(10);
    }

    private void logRoll(int roll) {
        Main.getMenu().logInspiration(roll);
        root.returnToButtons();
    }

}
