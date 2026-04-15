package combat_menu;

import __main.manager.EncounterManager;
import combat_object.combatant.Combatant;
import format.ColorStyles;
import lombok.*;
import lombok.experimental.*;
import org.intellij.lang.annotations.MagicConstant;
import swing.swing_comp.SwingComp;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor(staticName = "newInstance")
public class EncounterListPanel extends JPanel {

    static Color BG = new Color(0x1E, 0x21, 0x28);
    static Color DIVIDER_COLOR = new Color(0x2E, 0x32, 0x40);

    List<CombatantPanel> allPanels = new ArrayList<>();

    {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(BG);
        setOpaque(true);
        populate();
    }

    public void populate() {
        removeAll();
        allPanels.clear();

        addSectionLabel("Party and Allies");
        EncounterManager.getFriendlies().forEach(this::addCombatantRow);

        add(Box.createVerticalStrut(10));
        addSectionLabel("Belligerent Enemies");
        EncounterManager.getEnemies().forEach(this::addCombatantRow);

        add(Box.createVerticalGlue());

        revalidate();
        repaint();
    }

    private void addSectionLabel(String text) {
        SwingComp.label(text.toUpperCase())
                .withForeground(ColorStyles.SECTION_FG)
                .withDerivedFont(Font.BOLD, 12f)
                .onLeft()
                .withEmptyBorder(10, 15, 5, 0)
                .in(this);
    }

    private void addCombatantRow(Combatant c) {
        CombatantPanel panel = CombatantPanel.getPanelFor(c);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        allPanels.add(panel);
        add(panel);
        add(divider());
    }

    private JSeparator divider() {
        JSeparator sep = new JSeparator();
        sep.setForeground(DIVIDER_COLOR);
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        return sep;
    }

    public void updateAll() {
        allPanels.forEach(CombatantPanel::update);
    }

    public void setActionMode(
            @MagicConstant(intValues = {CombatantPanel.TURN, CombatantPanel.ATTACK, CombatantPanel.HEAL}) int mode
    ) {
        allPanels.forEach(p -> p.setActionMode(mode));
    }
}