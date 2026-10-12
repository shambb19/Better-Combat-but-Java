package creator_menu;

import _global_list.Resource;
import combat_object.combatant.CombatantOption;
import config.Config;
import creator_menu.ide.CampaignEditor;
import input.CampaignReader;
import input.syntax.Key;
import swing.builder.ComponentBuilder;
import swing.builder.TextItemBuilder;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemListener;
import java.net.URL;
import java.util.List;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Supplier;

import static swing.ColorStyles.*;
import static swing.builder.Components.*;
import static swing.builder.PanelBuilder.*;

public class HelpMenu extends JPanel {

    private static final String
            ITEM_HEADER_TUTORIAL =
            "Provided is the list of valid item headers. " +
                    "To create the item, type its header, then press CTRL+SHIFT+SPACE to autofill its parameters. " +
                    "Note that the name parameter is required and should go on the same line as the header.",

    TAG_HEADER =
            "Tags (except <incomplete) are only available in nonstandard rulesets and are put immediately " +
                    "after the header, without spaces.",

    COMBAT_OBJECT_HEADER =
            "Values in blue are not required and, if absent, will default to false, null, or the key's equivalent. " +
                    "Parameters in yellow are only required in the standard ruleset. " +
                    "All parameter lines should be typed in the \"key: value\" form.",

    DAMAGE_IMPLEMENT_FOOTER =
            "* Damage Implements only need to be included if they aren't in the weapon database.",

    CONFIG_HEADER_TUTORIAL =
            "Hover over a value to see the available options for that token.",

    TOKEN_CONFIG_HINT =
            Config.CONFIG_OPEN_TOKEN + " (opens the config block)\n" +
                    Config.CONFIG_CLOSE_TOKEN + " (closes the config block)",

    RULESET_CONFIG_HINT =
            "ruleset.standard (Cath's improved combat ruleset)\n" +
                    "ruleset.steampunk (Steampunk ruleset for the Summer 2026 campaign)",

    DAMAGE_CONFIG_HINT =
            "damage.show (enemy input damage is visible to players)\n" +
                    "damage.hide (enemy input damage is hidden in a password field)",

    QUEUE_CONFIG_HINT =
            "queue.standard (standard 5e initiative style)\n" +
                    "queue.cath (Cath's queue (all enemies take turn between party members))";

    private static final String // combo box option texts
            TUTORIAL_CODE_STANDARD_OPTION = "Example Code (Standard)",
            TUTORIAL_CODE_STEAMPUNK_OPTION = "Example Code (Steampunk)",
            CONFIG_OPTION = "Ruleset Configuration",
            ITEM_OPTION = "Campaign Items",
            TAG_OPTION = "Item Tags",
            PARTY_MEMBER_OPTION = "Party Member Params",
            NPC_ENEMY_OPTION = "NPC/Enemy Params",
            SCENARIO_OPTION = "Combat Scenario Params",
            WEAPON_OPTION = "Weapon Params",
            SPELL_OPTION = "Spell Params",
            GUN_OPTION = "Gun Params";

    private final Map<String, Supplier<JPanel>> comboBoxPanelMap = new HashMap<>(Map.of(
            CONFIG_OPTION, this::getConfigPanel,
            ITEM_OPTION, this::getItemPanel,
            TAG_OPTION, this::getTagPanel,
            PARTY_MEMBER_OPTION, () -> getCombatObjectPanel(".party"),
            NPC_ENEMY_OPTION, () -> getCombatObjectPanel(".npc"),
            SCENARIO_OPTION, () -> getCombatObjectPanel(".scenario"),
            WEAPON_OPTION, () -> getCombatObjectPanel(".weapon"),
            SPELL_OPTION, () -> getCombatObjectPanel(".spell"),
            GUN_OPTION, () -> getCombatObjectPanel(".gun")
    ));

    private final JComboBox<String> optionBox;
    private JPanel activePanel;

    {
        // because Map hates varargs for some fucking dumbass reason
        comboBoxPanelMap.put(TUTORIAL_CODE_STANDARD_OPTION, () -> getTutorialCodePanel(Resource.TUTORIAL_STANDARD));
        comboBoxPanelMap.put(TUTORIAL_CODE_STEAMPUNK_OPTION, () -> getTutorialCodePanel(Resource.TUTORIAL_STEAMPUNK));

        modifyingPanel(this).layoutType(BORDER).vgap(15).border(SPACER_BORDER).build().validated();

        JPanel headerPanel = createPanel(FLOW, "Select an option to learn more:")
                .hgap(10).build().putIn(this, BorderLayout.NORTH);

        String[] options = {
                TUTORIAL_CODE_STANDARD_OPTION, TUTORIAL_CODE_STEAMPUNK_OPTION,
                CONFIG_OPTION, ITEM_OPTION, TAG_OPTION,
                PARTY_MEMBER_OPTION, NPC_ENEMY_OPTION, SCENARIO_OPTION,
                WEAPON_OPTION, SPELL_OPTION, GUN_OPTION
        };
        optionBox = ComponentBuilder.createComboBox(options, b -> {}, null)
                .build().putIn(headerPanel);

        for (String option : options) optionBox.addItemListener(getOptionActionListener(option));
    }

    private JPanel getTutorialCodePanel(Resource tutorialCode) {
        URL tutorialCodeFile = tutorialCode.getUrl();
        List<String> code = CampaignReader.getLines(tutorialCodeFile, "static resource could not be downloaded");

        CampaignEditor codePanel = new CampaignEditor();
        codePanel.importText(code);
        codePanel.setFocusable(false);

        Dimension size = new Dimension(500, 756);
        return createPanel(FLOW, codePanel).preferredSize(size).maximumSize(size).build().get();
    }

    private JPanel getItemPanel() {
        JLabel footer = ComponentBuilder.createLabel(DAMAGE_IMPLEMENT_FOOTER)
                .fontBuilder(FontBuilder.of(Font.ITALIC, 10f, FG_HINT)).build().get();

        return getInfoPanel(ITEM_HEADER_TUTORIAL,
                getListItem(CombatantOption.PARTY),
                getListItem(CombatantOption.ALLIES),
                getListItem(CombatantOption.ENEMIES),
                getListItem("Combat Scenario", SCENARIO, ".scenario", HEADER),
                getListItem("Weapon*", FOREGROUND, ".weapon", HEADER),
                getListItem("Spell*", FOREGROUND, ".spell", HEADER),
                getListItem("Gun*", FOREGROUND, ".gun", HEADER),
                footer
        );
    }

    private JPanel getTagPanel() {
        BiFunction<String, String, JPanel> lg = (str1, str2) -> getListItem(str1, CONFIG, str2, FOREGROUND);

        return getInfoPanel(TAG_HEADER,
                lg.apply("<armored", "all incoming damage is reduced by 2"),
                lg.apply("<special", "unlocks custom HP values"),
                lg.apply("<incomplete", "item will not be included when the code is run")
        );
    }

    private JPanel getCombatObjectPanel(String header) {
        List<Key> params = Key.getAllParametersFor(header);
        List<Component> contentPaneComponents = new ArrayList<>();

        for (Key key : params) {
            String keyString = key.name().toLowerCase();
            String value = key.getRequirement();

            Color keyColor = switch (key.getRequirementType()) {
                case TRUE -> KEY;
                case FALSE -> KEY_OPTIONAL;
                case STANDARD_ONLY -> KEY_CONDITIONAL;
            };

            JPanel listItem = getListItem(keyString, keyColor, value, VALUE);
            contentPaneComponents.add(listItem);
            contentPaneComponents.add(spacer(0, 15));
        }

        return getInfoPanel(COMBAT_OBJECT_HEADER, contentPaneComponents);
    }

    private JPanel getConfigPanel() {
        return getInfoPanel(CONFIG_HEADER_TUTORIAL,
                getListItem(
                        Config.CONFIG_GENERIC_TOKEN, CONFIG,
                        "Opens or closes the config block", VALUE, TOKEN_CONFIG_HINT),
                getListItem(
                        "ruleset", FOREGROUND,
                        "Sets the campaign's combat ruleset", VALUE, RULESET_CONFIG_HINT),
                getListItem(
                        "damage", FOREGROUND,
                        "Sets the visibility of enemy damage input", VALUE, DAMAGE_CONFIG_HINT),
                getListItem(
                        "queue", FOREGROUND,
                        "Determines the initiative order in combat", VALUE, QUEUE_CONFIG_HINT)
        );
    }

    private JPanel getListItem(CombatantOption combatantOption) {
        return getListItem(
                combatantOption.getExpandedName(), combatantOption.getColor(), combatantOption.getCodeHeader(),
                HEADER
        );
    }

    private JPanel getListItem(String sectionName, Color nameColor, String codeHeader, Color codeColor) {
        return getListItem(sectionName, nameColor, codeHeader, codeColor, null);
    }

    private JPanel getListItem(String sectionName, Color nameColor, String codeHeader, Color codeColor, String toolTipText) {
        JLabel sectionNameLabel = ComponentBuilder.createLabel(sectionName)
                .fontBuilder(FontBuilder.of(Font.BOLD, 13f, nameColor)).build().get();

        JLabel codeHeaderLabel = ComponentBuilder.createLabel(codeHeader).foreground(codeColor).build().get();

        JPanel listItem = createPanel(FLOW_LEFT, sectionNameLabel, codeHeaderLabel).hgap(10)
                .toolTipText(toolTipText).build().get();
        listItem.setMaximumSize(new Dimension(Integer.MAX_VALUE, listItem.getPreferredSize().height));

        return listItem;
    }

    private JPanel getInfoPanel(String headerText, Object... contentComponents) {
        JPanel infoPanel = new JPanel(new BorderLayout());

        ArrayList<Object> components = new ArrayList<>(List.of(contentComponents[0]));
        for (int i = 1; i < contentComponents.length; i++) {
            components.addAll(List.of(contentComponents[i], spacer(0, 15)));
        }
        components.set(components.size() - 1, glue());

        TextItemBuilder.createTextArea(headerText) // header
                .border(BorderFactory.createEmptyBorder(0, 0, 15, 0))
                .build().putIn(infoPanel, BorderLayout.NORTH);

        createPanel(VERTICAL_BOX, components).vgap(15) // contents
                .build().getAsScrollerIn(infoPanel, BorderLayout.CENTER);

        return infoPanel;
    }

    private ItemListener getOptionActionListener(String option) {
        return e -> {
            String selection = (String) optionBox.getSelectedItem();

            if (selection != null && selection.equals(option)) {
                Optional.ofNullable(activePanel).ifPresent(this::remove);
                activePanel = comboBoxPanelMap.get(selection).get();
                add(activePanel, BorderLayout.CENTER);
                revalidate();
                repaint();
            }
        };
    }

}
