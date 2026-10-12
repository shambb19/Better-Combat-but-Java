package combat_menu.encounter_selection;

import _manager.EncounterManager;
import combat_object.combatant.Combatant;
import combat_object.combatant.CombatantOption;
import combat_object.combatant.NPC;
import combat_object.scenario.Scenario;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.ExtensionMethod;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.jetbrains.annotations.Nullable;
import swing.ColorStyles;
import swing.builder.ComponentBuilder;
import util.Filterable;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.function.Consumer;

import static swing.builder.Components.*;
import static swing.builder.PanelBuilder.*;

@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@ExtensionMethod(Filterable.class)
@RequiredArgsConstructor
public class CombatantScroller extends JScrollPane {

    @Getter Component host;
    boolean isParty;
    @Getter boolean isForQuickAdd;
    String inputType;
    Runnable validator;
    @Nullable CombatantOption toExclude;

    @NonFinal JPanel content;
    ArrayList<CombatantCard> combatantCards;

    private CombatantScroller(
            Component host,
            boolean isParty, boolean alwaysShowInput,
            String inputType, Runnable validator,
            CombatantOption toExclude
    ) {
        this(host, isParty, alwaysShowInput, inputType, validator, toExclude, new ArrayList<>());

        if (toExclude == CombatantOption.PARTY) throw new IllegalArgumentException(
                "CombatantScroller.<init>: cannot exclude party"
        );

        content = createPanel(VERTICAL_BOX)
                .background(ColorStyles.BG_DARK)
                .build().get();

        if (isParty) {
            addSectionLabel(CombatantOption.PARTY);
            EncounterManager.getParty().forEach(this::addCard);
        }

        setViewportView(content);
        getVerticalScrollBar().setUnitIncrement(16);
        setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 10));
        getViewport().setBackground(ColorStyles.BG_DARK);
    }

    private CombatantScroller(
            Component host,
            boolean isParty, boolean alwaysShowInput, String inputType, Runnable validator
    ) {
        this(host, isParty, alwaysShowInput, inputType, validator, null);
    }

    public void addCard(Combatant combatant) {
        CombatantCard card;
        if (isForQuickAdd) card = CombatantCard.getCombatantQuickAddCard(this, combatant, inputType, validator);
        else card = CombatantCard.getCombatantCard(this, combatant, inputType, true, validator);

        combatantCards.add(card);

        if (isParty) addCard(card);
        else rebuildSections();
    }

    /**
     * Precondition: all npcs provided are on the same team
     */
    public void addCardBulk(HashMap<NPC, Integer> combatantMap) {
        boolean isEnemies = combatantMap.keySet().stream().allMatch(Combatant::isEnemy);

        var scenarioBuilder = Scenario.builder().name("bulk add");
        if (isEnemies) scenarioBuilder.with(new HashMap<>()).against(combatantMap);
        else scenarioBuilder.with(combatantMap).against(new HashMap<>());

        scenarioBuilder.build().npcListAll(false).forEach(this::addCard);
    }

    private void addCard(CombatantCard card) {
        addConstraintlessComponentsTo(content, card, spacer(0, 6));
    }

    public void setScenario(Scenario scenario) {
        if (isParty) throw new IllegalArgumentException(
                "CombatantCardPanel.setScenario: party illegally affected by scenario change");

        combatantCards.clear();
        content.removeAll();
        scenario.npcListAll(false).forEach(npc -> {
            boolean showInput = !isForQuickAdd || npc.isEnemy();
            CombatantCard card = CombatantCard.getCombatantCard(this, npc, inputType, showInput, validator);
            combatantCards.add(card);
        });
        rebuildSections();
    }

    public void focusNext(CombatantCard hasFocus) {
        CombatantCard toFocus = hasFocus;
        for (int i = 0; i < combatantCards.size(); i++) {
            if (!combatantCards.get(i).equals(hasFocus)) continue;
            try {
                toFocus = combatantCards.get(i + 1);
            } catch (IndexOutOfBoundsException ignored) {
                toFocus = combatantCards.getFirst();
            }
        }
        toFocus.input.grabFocus();
    }

    private void rebuildSections() {
        List<Combatant> combatants = combatantCards.stream().map(c -> c.combatant).toList();

        var combatantsSplit = Combatant.enemyPartition(combatants);

        content.removeAll();

        Consumer<CombatantOption> cardAdder = section -> {
            if (toExclude == section) return;

            boolean isEnemies = section.isEnemies();
            List<Combatant> validCombatants = combatantsSplit.get(isEnemies);

            if (!isForQuickAdd) addSectionLabel(section);

            validCombatants.forEach(c -> {
                CombatantCard thisCard = Filterable.of(combatantCards).firstOrElseThrow(card -> card.combatant, c);
                addCard(thisCard);
            });
            addCard(CombatantCard.getAddNewCombatantCard(this, section));
        };

        List.of(CombatantOption.ALLIES, CombatantOption.ENEMIES).forEach(cardAdder);

        content.revalidate();
        content.repaint();
    }

    private void addSectionLabel(CombatantOption option) {
        ComponentBuilder.createLabel(option.name())
                .fontBuilder(FontBuilder.of(Font.BOLD, 16f, option.getColor()))
                .border(BorderFactory.createEmptyBorder(10, 2, 6, 2))
                .alignmentX(LEFT_ALIGNMENT)
                .build().putIn(content);
    }

    public CombatantCard[] getCombatantCards() {
        return content.getComponents().of()
                .castTo(CombatantCard.class)
                .filteredByAsList(Objects::nonNull).toArray(new CombatantCard[0]);
    }

    public boolean areAllCardsValid() {
        return content.getComponents().of().castToAsList(CombatantCard.class).stream()
                .filter(c -> !c.checkBox.isSelected()).allMatch(CombatantCard::hasValidInput);
    }

    public boolean containsEnemies() {
        return content.getComponents().of().castToAsList(CombatantCard.class).stream()
                .map(c -> c.combatant).filter(Objects::nonNull)
                .anyMatch(Combatant::isEnemy);
    }

    public static CombatantScroller createForEncounterSelectionPanel(
            Component host, Runnable validator, boolean isParty
    ) {
        return new CombatantScroller(host, isParty, true, "Initiative", validator);
    }

    public static CombatantScroller getNpcQuickAddPanel(
            Component host, Runnable validator, CombatantOption toExclude
    ) {
        return new CombatantScroller(host, false, false, "Include", validator, toExclude);
    }

}