package campaign_creator_menu;

import org.intellij.lang.annotations.MagicConstant;
import swing.swing_comp.SwingComp;

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

        JButton disabledPanel = SwingComp.button("Select a completed combatant or add a new one to continue.", null)
                .withFont(SwingComp.HEADER)
                .disabled().build();

        cards.add(combatantPanel, COMBATANT_INPUT);
        cards.add(scenarioPanel, SCENARIO_INPUT);
        cards.add(disabledPanel, DISABLED);

        setLeftComponent(cards);
        setRightComponent(displayPanel);

        cardLayout.show(cards, DISABLED);

        setResizeWeight(0.5);
        setDividerLocation(0.5);
    }

    public void changeInputPanel(
            @MagicConstant(valuesFromClass = LowerSplitPane.class) String card,
            boolean isEnabled
    ) {
        if (isEnabled) {
            cardLayout.show(cards, card);
        } else {
            cardLayout.show(cards, DISABLED);
        }
        setDividerLocation(0.5);
    }
}