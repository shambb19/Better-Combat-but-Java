package campaign_creator_menu;

import campaign_creator_menu.input.ListSelectionPanel;
import combat_object.combatant.PC;
import combat_object.combatant.info.AbilityModifier;
import combat_object.damage_implements.Spell;
import combat_object.damage_implements.Weapon;
import encounter.Encounter;
import lombok.*;
import lombok.experimental.*;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static format.ColorStyles.BACKGROUND;
import static format.ColorStyles.SUCCESS;
import static format.swing_comp.SwingComp.fluent;
import static format.swing_comp.SwingComp.*;
import static format.swing_comp.SwingPane.*;

@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@ExtensionMethod(util.Filter.class)
public class LevelUpPanel extends JPanel {

    static String INSTRUCTIONS = "Edit any combatant information on by the level-up. " +
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

        party = encounter.getFriendlies().castTo(PC.class).toArray(new PC[0]);

        setLayout(new BorderLayout());

        editingLabel = label("Editing " + party[0]).withDerivedFont(Font.PLAIN, 18f).onLeft().component();

        progressBar = new JProgressBar(SwingConstants.HORIZONTAL, 0, party.length + 1);
        fluent(progressBar).withEmptyBorder(10, 10, 10, 10);

        JPanel labelBarPanel = newArrangedAs(BORDER).borderCollect(
                west(editingLabel), center(progressBar)).component();

        panelIn(this, BorderLayout.NORTH).arrangedAs(VERTICAL_BOX)
                .collect(textArea(INSTRUCTIONS).withDerivedFont(Font.PLAIN, 15f), labelBarPanel);

        cardLayout = new CardLayout();
        cards = new JPanel(cardLayout);

        for (PC partyMember : party)
            cards.add(panelFor(partyMember), partyMember.getName());

        add(cards, BorderLayout.CENTER);

        button("Next", SUCCESS, this::showNext)
                .withCancelOption(root::finishLevelUpProcess).in(this, BorderLayout.SOUTH);
    }

    private JPanel panelFor(PC pc) {
        JPanel combatantPanel = newArrangedAs(BORDER).component();

        JPanel statLine = panelIn(combatantPanel, BorderLayout.NORTH).arrangedAs(FLOW).component();
        label("Click to increment").transparent().in(statLine);

        for (AbilityModifier stat : AbilityModifier.values())
            if (!stat.equals(AbilityModifier.OPTION))
                button(stat.name() + ": " + pc.getStats().get(stat), BACKGROUND, null)
                        .withAction(b -> {
                            pc.getStats().increment(stat);
                            b.setText(stat.name() + ": " + pc.getStats().get(stat));
                        }).in(statLine);

        JPanel implementsPanel = panelIn(combatantPanel, BorderLayout.CENTER).arrangedAs(BORDER)
                .borderCollect(north("Select New Spells")).component();

        ListSelectionPanel<Weapon> weaponSelectionPanel = ListSelectionPanel.implementsFilteredFor(Weapon.class, "Weapons", pc);
        ListSelectionPanel<Spell> spellSelectionPanel = ListSelectionPanel.implementsFilteredFor(Spell.class, "Spells", pc);

        panelsMap.put(pc, new SelectionPanels(weaponSelectionPanel, spellSelectionPanel));

        panelIn(implementsPanel, BorderLayout.CENTER).arrangedAs(ONE_COLUMN)
                .collect(weaponSelectionPanel, spellSelectionPanel);

        return combatantPanel;
    }

    private void showNext() {
        partyIndex++;
        if (partyIndex == party.length) {
            finishLevelUp();
            return;
        }

        editingLabel.setText("Editing " + party[partyIndex]);
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
