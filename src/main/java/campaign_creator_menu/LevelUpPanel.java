package campaign_creator_menu;

import combat_object.combatant.AbilityModifier;
import combat_object.combatant.PC;
import combat_object.damage_implements.Spell;
import combat_object.damage_implements.Weapon;
import encounter_info.Encounter;
import format.ColorStyles;
import lombok.*;
import lombok.experimental.*;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static swing.swing_comp.SwingPane.*;

@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class LevelUpPanel extends JPanel {

    static String INSTRUCTIONS = "Edit any combatant information affected by the level-up. " +
            "Note that HP and proficiency bonus have been automatically increased by the standard amount " +
            "for the player's class and new level.";

    CampaignCreatorMenu root;

    JLabel editingLabel;
    JProgressBar progressBar;
    CardLayout cardLayout;
    JPanel cards;

    PC[] party;
    Map<PC, SelectionPanels> panelsMap;
    @NonFinal int partyIndex = 0;

    public LevelUpPanel(Encounter encounter, CampaignCreatorMenu root) {
        this.root = root;
        panelsMap = new HashMap<>();

        party = encounter.getFriendlies().stream()
                .filter(PC.class::isInstance).map(PC.class::cast).toList().toArray(new PC[0]);

        setLayout(new BorderLayout());

        editingLabel = label("Editing " + party[0].getName()).asHeader().onLeft().component();

        progressBar = new JProgressBar(SwingConstants.HORIZONTAL, 0, party.length + 1);
        modifiable(progressBar).withEmptyBorder(10, 10, 10, 10);

        JPanel labelBarPanel = panel().withLayout(BORDER)
                .with(editingLabel, BorderLayout.WEST)
                .with(progressBar, BorderLayout.CENTER)
                .component();

        panelIn(this, BorderLayout.NORTH).withLayout(VERTICAL_BOX)
                .collect(textArea(INSTRUCTIONS).withDerivedFont(Font.PLAIN, 15f).centered(), labelBarPanel);

        cardLayout = new CardLayout();
        cards = new JPanel(cardLayout);

        for (PC partyMember : party)
            cards.add(panelFor(partyMember), partyMember.getName());

        add(cards, BorderLayout.CENTER);

        button("Next", this::showNext).withBackgroundAndForeground(ColorStyles.SUCCESS, ColorStyles.TEXT_PRIMARY)
                .withCancelOption(root::finishLevelUpProcess).in(this, BorderLayout.SOUTH);
    }

    private JPanel panelFor(PC pc) {
        JPanel combatantPanel = panel().withLayout(BORDER).component();

        JPanel statLine = panelIn(combatantPanel, BorderLayout.NORTH).withLayout(FLOW).component();
        label("Click to increment").transparent().in(statLine);

        for (AbilityModifier stat : AbilityModifier.values())
            if (!stat.equals(AbilityModifier.OPTION))
                button(stat.name() + ": " + pc.getStats().get(stat), null)
                        .withAction(b -> {
                            pc.getStats().increment(stat);
                            b.setText(stat.name() + ": " + pc.getStats().get(stat));
                        }).in(statLine);

        JPanel implementsPanel = panelIn(combatantPanel, BorderLayout.CENTER).withLayout(BORDER)
                .with("Select new spells", BorderLayout.NORTH).component();

        ListSelectionPanel<Weapon> weaponSelectionPanel = ListSelectionPanel.implementsFilteredFor(Weapon.class, "Weapons", pc);
        ListSelectionPanel<Spell> spellSelectionPanel = ListSelectionPanel.implementsFilteredFor(Spell.class, "Spells", pc);

        panelsMap.put(pc, new SelectionPanels(weaponSelectionPanel, spellSelectionPanel));

        panelIn(implementsPanel, BorderLayout.CENTER).withLayout(ONE_COLUMN)
                .collect(weaponSelectionPanel, spellSelectionPanel);

        return combatantPanel;
    }

    private void showNext() {
        partyIndex++;
        if (partyIndex == party.length) {
            finishLevelUp();
            return;
        }

        editingLabel.setText("Editing " + party[partyIndex].getName());
        progressBar.setValue(partyIndex + 1);
        cardLayout.show(cards, party[partyIndex].getName());
    }

    private void finishLevelUp() {
        for (PC partyMember : party) {
            List<Weapon> newWeapons = panelsMap.get(partyMember).weaponPanel.getSelected();
            List<Spell> newSpells = panelsMap.get(partyMember).spellPanel.getSelected();

            partyMember.getWeapons().addAll(newWeapons);
            partyMember.getSpells().addAll(newSpells);
        }
        root.finishLevelUpProcess();
    }

    private record SelectionPanels(ListSelectionPanel<Weapon> weaponPanel, ListSelectionPanel<Spell> spellPanel) {
    }

}
