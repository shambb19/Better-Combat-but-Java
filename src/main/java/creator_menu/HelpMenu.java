package creator_menu;

import _global_list.Resource;
import config.Config;
import ide.CampaignEditor;
import input.CampaignReader;
import input.syntax.Key;
import util.Message;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemListener;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.*;
import java.util.function.Supplier;

import static swing.ColorStyles.*;
import static swing.fluent.SwingComp.label;
import static swing.fluent.SwingPane.*;

public class HelpMenu extends JPanel {

    private static final String
            ITEM_HEADER_TUTORIAL_TEXT =
            "Provided is the list of valid item headers. " +
                    "To create the item, type its header, then press CTRL+SHIFT+SPACE to autofill its parameters. " +
                    "Note that the name parameter is required and should go on the same line as the header.",

    TAG_HEADER_TEXT =
            "Tags (except <incomplete) are only available in nonstandard rulesets and are put immediately " +
                    "after the header, without spaces.",

    COMBAT_OBJECT_HEADER_TEXT =
            "Values in blue are not required and, if absent, will default to false, null, or the key's equivalent. " +
                    "Parameters in yellow are only required in the standard ruleset. " +
                    "All parameter lines should be typed in the \"key: value\" form.",

    DAMAGE_IMPLEMENT_FOOTER_TEXT =
            "* Damage Implements only need to be included if they aren't in the weapon database.",

    CONFIG_HEADER_TUTORIAL_TEXT =
            "Hover over a value to see the available options for that token.",

    TOKEN_CONFIG_HINT_TEXT =
            Config.CONFIG_OPEN_TOKEN + " (opens the config block)\n" +
                    Config.CONFIG_CLOSE_TOKEN + " (closes the config block)",

    RULESET_CONFIG_HINT_TEXT =
            "ruleset.standard (Cath's improved combat ruleset)\n" +
                    "ruleset.steampunk (Steampunk ruleset for the Summer 2026 campaign)",

    DAMAGE_CONFIG_HINT_TEXT =
            "damage.show (enemy input damage is visible to players)\n" +
                    "damage.hide (enemy input damage is hidden in a password field)",

    QUEUE_CONFIG_HINT_TEXT =
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

        fluent(this).arrangedAs(BORDER, 0, 10).spaced();

        JPanel headerPanel = newArrangedAs(FLOW, 10, 0)
                .collect("Select an option to learn more:")
                .in(this, BorderLayout.NORTH);

        String[] options = {
                TUTORIAL_CODE_STANDARD_OPTION, TUTORIAL_CODE_STEAMPUNK_OPTION,
                CONFIG_OPTION, ITEM_OPTION, TAG_OPTION,
                PARTY_MEMBER_OPTION, NPC_ENEMY_OPTION, SCENARIO_OPTION,
                WEAPON_OPTION, SPELL_OPTION, GUN_OPTION
        };
        optionBox = new JComboBox<>();
        for (String option : options) {
            optionBox.addItem(option);
            optionBox.addItemListener(getOptionActionListener(option));
        }
        optionBox.setSelectedIndex(-1);
        headerPanel.add(optionBox);
    }

    private JPanel getTutorialCodePanel(Resource tutorialCode) {
        List<String> code;
        try {
            URL tutorialCodeFile = tutorialCode.getUrl();
            code = CampaignReader.getLines(tutorialCodeFile);
        } catch (IOException e) {
            Message.showFileErrorMessage(e, Message.READ_ERROR);
            throw new RuntimeException("static resource could not be downloaded");
        }

        CampaignEditor codePanel = new CampaignEditor();
        codePanel.importText(code);
        codePanel.setFocusable(false);

        return newArrangedAs(FLOW).collect(codePanel)
                .withPreferredSize(500, 756)
                .withMaximumSize(500, 756)
                .component();
    }

    private JPanel getItemPanel() {
        return getInfoPanel(ITEM_HEADER_TUTORIAL_TEXT,
                getListItem("Party Member", PARTY, ".party", HEADER), spacer(0, 15),
                getListItem("Friendly NPC", FRIENDLY, ".npc", HEADER), spacer(0, 15),
                getListItem("Enemy Combatant", ENEMY, ".enemy", HEADER), spacer(0, 15),
                getListItem("Combat Scenario", SCENARIO, ".scenario", HEADER), spacer(0, 15),
                getListItem("Weapon*", FOREGROUND, ".weapon", HEADER), spacer(0, 15),
                getListItem("Spell*", FOREGROUND, ".spell", HEADER), spacer(0, 15),
                getListItem("Gun*", FOREGROUND, ".gun", HEADER), spacer(0, 15),
                label(DAMAGE_IMPLEMENT_FOOTER_TEXT, Font.ITALIC, 10f, FG_HINT),
                glue()
        );
    }

    private JPanel getTagPanel() {
        interface ConfigListItemGetter {
            JPanel getItem(String str1, String str2);
        }
        ConfigListItemGetter lg = (str1, str2) -> getListItem(str1, CONFIG, str2, FOREGROUND);

        return getInfoPanel(TAG_HEADER_TEXT,
                lg.getItem("<armored", "all incoming damage is reduced by 2"), spacer(0, 15),
                lg.getItem("<special", "unlocks custom HP values"), spacer(0, 15),
                lg.getItem("<incomplete", "item will not be included when the code is run"), spacer(0, 15),
                glue()
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

        return getInfoPanel(COMBAT_OBJECT_HEADER_TEXT, contentPaneComponents);
    }

    private JPanel getConfigPanel() {
        interface ToolTipListItemGetter {
            JPanel getItem(String str1, String str2, Color colorStr1, String toolTipText);
        }
        ToolTipListItemGetter lg = (str1, str2, colorStr1, toolTipText) -> {
            JPanel listItem = getListItem(str1, colorStr1, str2, VALUE);
            listItem.setToolTipText(toolTipText);
            return listItem;
        };

        return getInfoPanel(CONFIG_HEADER_TUTORIAL_TEXT,
                lg.getItem(Config.CONFIG_GENERIC_TOKEN, "Opens or closes the config block", CONFIG, TOKEN_CONFIG_HINT_TEXT),
                spacer(0, 15),
                lg.getItem("ruleset", "Sets the campaign's combat ruleset", FOREGROUND, RULESET_CONFIG_HINT_TEXT),
                spacer(0, 15),
                lg.getItem("damage", "Sets the visibility of enemy damage input", FOREGROUND, DAMAGE_CONFIG_HINT_TEXT),
                spacer(0, 15),
                lg.getItem("queue", "Determines the initiative order in combat", FOREGROUND, QUEUE_CONFIG_HINT_TEXT),
                glue()
        );
    }

    private JPanel getListItem(String str1, Color color1, String str2, Color color2) {
        JPanel listItem = newArrangedAs(FLOW_LEFT, 10, 0).collect(
                label(str1, Font.BOLD, 13f, color1), label(str2, color2)
        ).transparent().component();
        fluent(listItem).withMaximumSize(Integer.MAX_VALUE, listItem.getPreferredSize().height);

        return listItem;
    }

    private JPanel getInfoPanel(String headerText, Object... contentComponents) {
        JScrollPane contents = newArrangedAs(VERTICAL_BOX, 0, 15)
                .collect(contentComponents)
                .toScroller().component();

        return newArrangedAs(BORDER).borderCollect(
                north(textArea(headerText).withEmptyBorder(0, 0, 15, 0)),
                center(contents)
        ).component();
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
