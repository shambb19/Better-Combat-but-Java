package combat_menu.encounter_selection;

import _manager.EncounterManager;
import combat_object.combatant.Combatant;
import combat_object.combatant.NPC;
import combat_object.scenario.Scenario;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.ExtensionMethod;
import lombok.experimental.FieldDefaults;
import swing.ColorStyles;
import util.Filterable;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static swing.fluent.SwingComp.*;
import static swing.fluent.SwingPane.*;
import static swing.ColorStyles.*;

@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@ExtensionMethod(Filterable.class)
public class CombatantScroller extends JScrollPane {

    @AllArgsConstructor @FieldDefaults(makeFinal = true) public enum CombatantOption {
        PARTY("Party", ColorStyles.PARTY), ALLIES("Ally", FRIENDLY), ENEMIES("Enemy", ENEMY);
        String text;
        Color color;
    }

    @Getter Component host;
    boolean isParty;
    boolean showAbsent;
    boolean alwaysShowInput;
    String inputType;
    Runnable validator;
    CombatantOption toExclude;

    JPanel content;
    HashMap<Combatant, CombatantCard> combatantCardMap = new HashMap<>();

    private CombatantScroller(
            Component host,
            boolean isParty, boolean showAbsent, boolean alwaysShowInput, String inputType, Runnable validator,
            CombatantOption toExclude
    ) {
        this.host = host;
        this.isParty = isParty;
        this.showAbsent = showAbsent;
        this.alwaysShowInput = alwaysShowInput;
        this.inputType = inputType;
        this.validator = validator;
        this.toExclude = toExclude;

        if (toExclude == CombatantOption.PARTY) throw new IllegalArgumentException(
                "CombatantScroller.<init>: cannot exclude party"
        );

        content = newArrangedAs(VERTICAL_BOX).transparent().component();

        if (isParty) {
            addSectionLabel(CombatantOption.PARTY);
            EncounterManager.getParty().forEach(this::addCard);
        }

        setViewportView(content);
        getVerticalScrollBar().setUnitIncrement(16);
        setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        fluent(this).withEmptyBorder(10, 12, 10, 12).withBackground(BACKGROUND);
    }

    private CombatantScroller(
            Component host,
            boolean isParty, boolean showAbsent, boolean alwaysShowInput, String inputType, Runnable validator
    ) {
        this(host, isParty, showAbsent, alwaysShowInput, inputType, validator, null);
    }

    public CombatantCard addCard(Combatant combatant) {
        boolean showInput = alwaysShowInput;
        if (!alwaysShowInput) showInput = combatant.isEnemy();

        CombatantCard card;
        if (isForQuickAdd()) card = CombatantCard.quickAddCard(this, combatant, inputType, validator);
        else card = new CombatantCard(this, combatant, inputType, showAbsent, validator).withUpdatedVisibility(!showInput, !showAbsent);

        combatantCardMap.put(combatant, card);

        if (isParty) fluent(content).collect(card, spacer(0, 6));
        else rebuildSections();

        return card;
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
        fluent(content).collect(card, spacer(0, 6));
    }

    public void setScenario(Scenario scenario) {
        if (isParty) throw new IllegalArgumentException(
                "CombatantCardPanel.setScenario: party illegally affected by scenario change");

        combatantCardMap.clear();
        content.removeAll();
        scenario.npcListAll(false).forEach(npc -> {
            boolean showInput = alwaysShowInput || npc.isEnemy();
            CombatantCard card = new CombatantCard(this, npc, inputType, showAbsent, validator)
                    .withUpdatedVisibility(!showInput, !showAbsent);
            combatantCardMap.put(npc, card);
        });
        rebuildSections();
    }

    private void rebuildSections() {
        var combatantsSplit = combatantCardMap.keySet().stream()
                .collect(Collectors.partitioningBy(Combatant::isEnemy));

        content.removeAll();

        Consumer<CombatantOption> cardAdder = section -> {
            if (toExclude == section) return;

            boolean isEnemies = section == CombatantOption.ENEMIES;
            List<Combatant> validCombatants = combatantsSplit.get(isEnemies);

            if (!isForQuickAdd()) addSectionLabel(section);
            validCombatants.forEach(c -> addCard(combatantCardMap.get(c)));
            addCard(CombatantCard.promptCard(this, section));
        };

        List.of(CombatantOption.ALLIES, CombatantOption.ENEMIES).forEach(cardAdder);

        content.revalidate();
        content.repaint();
    }

    private void addSectionLabel(CombatantOption option) {
        JLabel label = label(option.text.toUpperCase(), Font.BOLD, 16f, option.color)
                .withEmptyBorder(10, 2, 6, 2).onLeft().component();
        content.add(label);
    }

    public CombatantCard[] getCombatantCards() {
        return content.getComponents().of()
                .castTo(CombatantCard.class)
                .filteredByAsList(Objects::nonNull).toArray(new CombatantCard[0]);
    }

    public boolean areAllCardsValid() {
        return content.getComponents().of()
                .castToAsList(CombatantCard.class).stream().allMatch(CombatantCard::hasValidInput);
    }

    public boolean containsEnemies() {
        return content.getComponents().of().castToAsList(CombatantCard.class).stream()
                .map(CombatantCard::getCombatant).filter(Objects::nonNull)
                .anyMatch(Combatant::isEnemy);
    }

    public boolean isForQuickAdd() {
        return toExclude != null;
    }

    public static CombatantScroller partyPanel(
            Component host, String inputType, boolean showAbsent, boolean alwaysShowInput, Runnable validator
    ) {
        return new CombatantScroller(host, true, showAbsent, alwaysShowInput, inputType, validator);
    }

    public static CombatantScroller npcPanel(
            Component host,
            String inputType, boolean showAbsent, boolean alwaysShowInput, Runnable validator,
            CombatantOption toExclude
    ) {
        return new CombatantScroller(host, false, showAbsent, alwaysShowInput, inputType, validator, toExclude);
    }

    public static CombatantScroller npcPanel(
            Component host, String inputType, boolean showAbsent, boolean alwaysShowInput, Runnable validator
    ) {
        return new CombatantScroller(host, false, showAbsent, alwaysShowInput, inputType, validator);
    }

}