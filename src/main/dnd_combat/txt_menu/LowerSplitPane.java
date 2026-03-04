package txt_menu;

import javax.swing.*;
import java.awt.*;

public class LowerSplitPane extends JSplitPane {

    private final JPanel cards;
    private final CardLayout cardLayout;

    private static final String INPUT_CARD = "INPUT";
    private static final String DISABLED_CARD = "DISABLED";

    public LowerSplitPane(JPanel inputPanel, JPanel displayPanel) {
        super(JSplitPane.HORIZONTAL_SPLIT);

        cardLayout = new CardLayout();
        cards = new JPanel(cardLayout);

        JButton disabledPanel = new JButton("Select a completed combatant or add a new one to continue.");
        disabledPanel.setEnabled(false);
        disabledPanel.putClientProperty("FlatLaf.style", "font: $h1.regular.font");

        cards.add(inputPanel, INPUT_CARD);
        cards.add(disabledPanel, DISABLED_CARD);

        setLeftComponent(cards);
        setRightComponent(displayPanel);

        cardLayout.show(cards, DISABLED_CARD);

        setResizeWeight(0.5);
        setDividerLocation(0.5);
    }

    public void setInputPanelEnabled(boolean isEnabled) {
        if (isEnabled) {
            cardLayout.show(cards, INPUT_CARD);
        } else {
            cardLayout.show(cards, DISABLED_CARD);
        }

        setDividerLocation(0.5);
    }
}