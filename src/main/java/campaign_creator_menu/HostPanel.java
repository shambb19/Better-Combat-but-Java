package campaign_creator_menu;

import campaign_creator_menu.input.CombatantInputPanel;
import campaign_creator_menu.input.ScenarioInputPanel;
import org.intellij.lang.annotations.MagicConstant;
import swing.fluent.SwingPane;

import javax.swing.*;
import java.awt.*;

import static swing.ColorStyles.PERFECT;
import static swing.fluent.SwingComp.button;
import static swing.fluent.SwingComp.label;
import static swing.fluent.SwingPane.*;

public class HostPanel extends JPanel {

    private final CardLayout cardLayout;
    private final JPanel cards;

    private final JButton levelUpButton;

    public static final String COMBATANT_INPUT = "c", SCENARIO_INPUT = "s", LEVEL = "l", DISABLED = "d";

    public HostPanel(
            CombatantInputPanel inputPanel, ScenarioInputPanel scenarioPanel, LevelUpPanel levelPanel,
            CompletedElementsList completedList, DownloadDocDisplayPanel displayPanel
    ) {
        SwingPane.fluent(this).arrangedAs(TWO_COLUMN, 10, 10).withEmptyBorder(10);

        cardLayout = new CardLayout();
        cards = new JPanel(cardLayout);

        JLabel disabledPanel = label("Select a completed combatant or add a new one to continue.", Font.PLAIN, 18f)
                .enabled(false).component();

        cards.add(inputPanel, COMBATANT_INPUT);
        cards.add(scenarioPanel, SCENARIO_INPUT);
        cards.add(levelPanel, LEVEL);
        cards.add(disabledPanel, DISABLED);

        cardLayout.show(cards, DISABLED);

        levelUpButton = button("Level up party", PERFECT, () -> changeInputPanel(LEVEL, true))
                .component();

        panelIn(this).arrangedAs(BORDER).borderCollect(
                center(cards), south(levelUpButton));

        panelIn(this).arrangedAs(BORDER).borderCollect(
                north(completedList), center(displayPanel));
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