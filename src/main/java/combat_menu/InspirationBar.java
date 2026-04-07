package combat_menu;

import format.swing_comp.SwingComp;
import format.swing_comp.SwingPane;
import util.Message;

import javax.swing.*;
import java.awt.*;

public class InspirationBar extends JPanel {

    private int inspirationRolls;
    private int numCompletions;

    private final JProgressBar inspirationProgressBar;

    public static InspirationBar newInstance() {
        return new InspirationBar();
    }

    private InspirationBar() {
        inspirationRolls = 0;
        numCompletions = 0;

        SwingPane.modifiable(this)
                .withLayout(SwingPane.BORDER)
                .withEmptyBorder(10);

        SwingComp.label("Excess Inspiration 1d4 Points")
                .withEmptyBorder(10)
                .in(this, BorderLayout.WEST);

        inspirationProgressBar = SwingComp.progressBar(0, 10, 0, SwingConstants.HORIZONTAL)
                .withForeground(new Color(217, 13, 235))
                .applied(b -> b.setStringPainted(true))
                .applied(b -> b.setString("0"))
                .in(this, BorderLayout.CENTER)
                .build();
    }

    public void logInspirationRolls(int d4Roll) {
        inspirationRolls += d4Roll;

        inspirationProgressBar.setValue(inspirationRolls);
        inspirationProgressBar.setString(inspirationRolls + "(" + numCompletions + ")");
        if (inspirationProgressBar.getValue() == inspirationProgressBar.getMaximum()) {
            Message.template("10 Inspiration points have been rolled. Take appropriate mysterious actions.");
            inspirationRolls = 0;
            numCompletions++;
            logInspirationRolls(0);
        }
    }

}
