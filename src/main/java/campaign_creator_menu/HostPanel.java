package campaign_creator_menu;

import format.ColorStyles;
import org.intellij.lang.annotations.MagicConstant;

import javax.swing.*;
import java.awt.*;

import static swing.swing_comp.SwingPane.*;

public class HostPanel extends JPanel {

    private final CardLayout cardLayout;
    private final JPanel cards;

    private final JButton levelUpButton;

    public static final String COMBATANT_INPUT = "c", SCENARIO_INPUT = "s", LEVEL = "l", DISABLED = "d";

    public HostPanel(
            CombatantInputPanel inputPanel, ScenarioInputPanel scenarioPanel, LevelUpPanel levelPanel,
            CompletedElementsList completedList, DownloadDocDisplayPanel displayPanel
    ) {
        modifiable(this).withLayout(TWO_COLUMN).withGaps(10, 10)
                .withEmptyBorder(10, 10, 10, 10);

        cardLayout = new CardLayout();
        cards = new JPanel(cardLayout);

        JLabel disabledPanel = label("Select a completed combatant or add a new one to continue.")
                .asHeader().disabled().component();

        cards.add(inputPanel, COMBATANT_INPUT);
        cards.add(scenarioPanel, SCENARIO_INPUT);
        cards.add(levelPanel, LEVEL);
        cards.add(disabledPanel, DISABLED);

        cardLayout.show(cards, DISABLED);

        levelUpButton = button("Level up party", () -> changeInputPanel(LEVEL, true))
                .withBackgroundAndForeground(ColorStyles.PERFECT, ColorStyles.TEXT_PRIMARY)
                .withEmptyBorder(6, 10, 6, 10)
                .component();

        panelIn(this).withLayout(BORDER)
                .with(cards, BorderLayout.CENTER)
                .with(levelUpButton, BorderLayout.SOUTH);

        panelIn(this).withLayout(BORDER)
                .with(completedList, BorderLayout.NORTH)
                .with(displayPanel, BorderLayout.CENTER);
    }

    public void changeInputPanel(
            @MagicConstant(valuesFromClass = HostPanel.class) String card,
            boolean isEnabled
    ) {
        if (isEnabled) {
            cardLayout.show(cards, card);
        } else {
            cardLayout.show(cards, DISABLED);
        }
    }

    public void endLevelUp() {
        cardLayout.show(cards, DISABLED);
        levelUpButton.setEnabled(false);
        levelUpButton.setText("Party already leveled up");
    }

}