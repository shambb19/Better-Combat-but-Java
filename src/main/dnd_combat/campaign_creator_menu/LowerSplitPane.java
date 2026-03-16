package campaign_creator_menu;

import javax.swing.*;
import java.awt.*;

public class LowerSplitPane extends JSplitPane {

    private final JPanel cards;
    private final CardLayout cardLayout;

    public static final String COMBATANT_INPUT = "COMBATANT";
    public static final String SCENARIO_INPUT = "SCENARIO";
    public static final String DISABLED = "DISABLED";

    public LowerSplitPane(JPanel combatantPanel, JPanel scenarioPanel, JPanel displayPanel) {
        super(JSplitPane.HORIZONTAL_SPLIT);

        cardLayout = new CardLayout();
        cards = new JPanel(cardLayout);

        JButton disabledPanel = new JButton("Select a completed combatant or add a new one to continue.");
        disabledPanel.setEnabled(false);
        disabledPanel.putClientProperty("FlatLaf.style", "font: $h1.regular.font");

        cards.add(combatantPanel, COMBATANT_INPUT);
        cards.add(scenarioPanel, SCENARIO_INPUT);
        cards.add(disabledPanel, DISABLED);

        setLeftComponent(cards);
        setRightComponent(displayPanel);

        cardLayout.show(cards, DISABLED);

        setResizeWeight(0.5);
        setDividerLocation(0.5);
    }

    public void changeInputPanel(String card) {
        cardLayout.show(cards, card);

        setDividerLocation(0.5);
    }
}