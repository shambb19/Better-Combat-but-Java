package combat_menu.action_panel.damage_panel

import __main.EncounterInfo
import __main.Main
import character_info.combatant.Combatant
import combat_menu.action_panel.ActionPanel
import damage_implements.Effect
import damage_implements.Implement
import damage_implements.Spell
import damage_implements.Weapon
import format.ColorStyle
import format.swing_comp.SwingComp
import format.swing_comp.SwingComp.button
import format.swing_comp.SwingComp.modifiable
import format.swing_comp.SwingPane
import util.Message
import javax.swing.*
import javax.swing.event.DocumentEvent
import javax.swing.event.DocumentListener
import kotlin.math.max

class DamageAmountPanel private constructor(
    private val implement: Implement,
    private val target: Combatant,
    private val root: ActionPanel
) : JPanel() {

    private val attacker: Combatant = EncounterInfo.getCurrentCombatant()

    private val mainDamageField = JTextField(8)
    private val otherBonusDamageCheck = JCheckBox("Other Bonus Damage?")
    private val otherBonusDamageField = JTextField(8)
    private val summaryLabel = JLabel("Total Damage: 0")
    private val okButton = JButton("Deal Damage")

    init {
        val damagePanel = damagePanel
        val bonusPanel = bonusPanel
        val summaryPanel = summaryPanel

        val okCancelPanel = button("Deal Damage", this::logAndFinish)
            .withBackground(ColorStyle.DARKER_RED.color)
            .withCancelOption(root::returnToButtons)
            .build()

        SwingPane.modifiable(this).collect(
            damagePanel, SwingComp.gap(10),
            bonusPanel, SwingComp.gap(10),
            summaryPanel, SwingComp.gap(15),
            okCancelPanel
        ).withLayout(SwingPane.VERTICAL_BOX)
            .withEmptyBorder(15)

        setupListeners()
        updateUIState()

        SwingUtilities.invokeLater { mainDamageField.requestFocusInWindow() }
    }

    private val damagePanel: JPanel
        get() {
            return SwingPane.flowPair(
                "Enter " + implement.damageString() + ":",
                modifiable(mainDamageField).centered(),
                true
            )
                .withLabeledBorder("Main Damage")
                .build()
        }

    private val bonusPanel: JPanel
        get() {
            modifiable(otherBonusDamageField)
                .enabledWhen(otherBonusDamageCheck, true)
                .onlyIntegers()

            return SwingPane.flowPair(otherBonusDamageCheck, otherBonusDamageField, true)
                .withLabeledBorder("Bonus Damage")
                .disabled()
                .build()
        }

    private val summaryPanel: JPanel
        get() {
            return SwingPane.panel()
                .collectIf(
                    implement is Weapon && !implement.isManual,
                    "Stat Bonus: +" + attacker.mod(implement.stat())
                )
                .collectIf(
                    implement.isHalfDamage,
                    "Damage will be halved (Failed Attack)"
                )
                .collectIf(
                    target.isHexedBy(attacker),
                    "Hex: include +1d6 in bonus field"
                )
                .collect(
                    SwingComp.gap(5),
                    modifiable(summaryLabel).bold(16f).withForeground(ColorStyle.DARKER_RED.color).centered()
                )
                .withLayout(SwingPane.VERTICAL_BOX)
                .build()
        }

    private fun setupListeners() {
        val updateListener: DocumentListener = object : DocumentListener {
            override fun insertUpdate(e: DocumentEvent) {
                updateUIState()
            }

            override fun removeUpdate(e: DocumentEvent) {
                updateUIState()
            }

            override fun changedUpdate(e: DocumentEvent) {
                updateUIState()
            }
        }

        mainDamageField.document.addDocumentListener(updateListener)
        otherBonusDamageField.document.addDocumentListener(updateListener)

        if (implement.isManual) {
            modifiable(mainDamageField).onlyIntegers()
        } else {
            modifiable(mainDamageField).forIntegersOnRange(1, implement.numDice() * implement.dieSize())
        }

        otherBonusDamageCheck.addActionListener { updateUIState() }
    }

    private fun calculateTotal(): Int {
        var damage = mainDamage + bonusDamage

        if (implement.isManual)
            return damage
        if (implement.isHalfDamage)
            damage /= 2
        if (implement is Weapon)
            damage += attacker.mod(implement.stat())

        return max(0.0, damage.toDouble()).toInt()
    }

    private val mainDamage: Int
        get() {
            val text = mainDamageField.text.trim { it <= ' ' }
            if (text.isEmpty()) return 0
            return text.toInt()
        }

    private val bonusDamage: Int
        get() {
            if (!otherBonusDamageCheck.isSelected) return 0

            val text = otherBonusDamageField.text.trim { it <= ' ' }
            if (text.isEmpty()) return 0

            return text.toInt()
        }

    private fun updateUIState() {
        okButton.background = ColorStyle.DARKER_RED.color
        try {
            val total = calculateTotal()
            if (total > 0) {
                okButton.isEnabled = true
                okButton.text = "Deal $total Damage"
                summaryLabel.text = "Total Damage: $total"
            } else {
                okButton.isEnabled = false
                okButton.text = "Enter Damage"
                summaryLabel.text = "Total Damage: 0"
            }
        } catch (e: NumberFormatException) {
            okButton.isEnabled = false
            okButton.text = "Numbers Only"
        }
    }

    private fun logAndFinish() {
        val total = calculateTotal()
        target.damage(total)

        if (implement is Spell) {
            val effect = implement.effect()
            attacker.putEffect(target, effect)

            if (effect == Effect.ILLUSION)
                Message.informIllusion(target)
            else if (effect == Effect.ADVANTAGE_SOON)
                Message.template("Advantage on next attack against $target this turn.")
        }

        Main.logAction()

        root.returnToButtons()
    }

    companion object {
        @JvmStatic
        fun newInstance(implement: Implement, target: Combatant, root: ActionPanel): DamageAmountPanel {
            return DamageAmountPanel(implement, target, root)
        }
    }
}