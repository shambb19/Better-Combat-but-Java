package combat_menu;

import __main.CombatMain;

import javax.swing.*;
import java.awt.*;

public class InspirationBar extends JPanel {

    private int inspirationRolls;
    private int numCompletions;

    private final InspirationProgressBar inspirationProgressBar;

    public static InspirationBar newInstance() {
        return new InspirationBar();
    }

    private InspirationBar() {
        inspirationRolls = 0;
        numCompletions = 0;

        setLayout(new GridLayout(0, 1));

        inspirationProgressBar = new InspirationProgressBar();

        JLabel label = new JLabel("Excess Inspiration 1d4 Points");
        label.setHorizontalAlignment(SwingConstants.CENTER);

        add(inspirationProgressBar);
        add(label);
    }

    public void logInspirationRolls(int d4Roll) {
        inspirationRolls += d4Roll;
        inspirationProgressBar.update();
    }

    class InspirationProgressBar extends JProgressBar {

        public InspirationProgressBar() {
            setStringPainted(true);
            setString("0");
            setMinimum(0);
            setMaximum(10);
            setValue(0);
            setForeground(new Color(217, 13, 235));
        }

        public void update() {
            setValue(inspirationRolls);
            setString(inspirationRolls + " (" + numCompletions + ")");
            if (getValue() == getMaximum()) {
                JOptionPane.showMessageDialog(
                        CombatMain.getMenu(),
                        "10 inspiration points have been rolled. Take appropriate action.",
                        CombatMenu.TITLE,
                        JOptionPane.WARNING_MESSAGE
                );
                inspirationRolls = 0;
                numCompletions++;
                update();
            }
        }

    }

}
