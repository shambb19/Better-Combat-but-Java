package gui;

import combat.Main;

import javax.swing.*;
import java.awt.*;

public class InspirationBar extends JPanel {

    private int inspirationRolls;
    private int numCompletions;

    private final InspirationProgressBar inspirationProgressBar;

    public InspirationBar() {
        inspirationRolls = 0;
        numCompletions = 0;

        setLayout(new GridLayout(0, 1));

        inspirationProgressBar = new InspirationProgressBar();

        add(new JLabel("Excess Inspiration 1d4 Points"));
        add(inspirationProgressBar);
        add(new JLabel(""));
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
                        Main.menu,
                        "10 inspiration points have been rolled. Take appropriate action.",
                        Main.TITLE,
                        JOptionPane.WARNING_MESSAGE
                );
                inspirationRolls = 0;
                numCompletions++;
                update();
            }
        }

    }

}
