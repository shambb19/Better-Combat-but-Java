package combat_menu.action_panel.damage_panel

import __main.EncounterInfo
import __main.Main
import character_info.combatant.Combatant
import combat_menu.action_panel.ActionPanel
import combat_menu.listener.DieRollListener
import combat_menu.listener.IntegerFieldListener
import damage_implements.Effect
import damage_implements.Implement
import damage_implements.Spell
import damage_implements.Weapon
import format.ColorStyle
import format.SwingStyles
import util.Message
import java.awt.FlowLayout
import java.awt.Font
import java.awt.event.ActionEvent
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
        border = BorderFactory.createEmptyBorder(15, 15, 15, 15)
        layout = BoxLayout(this, BoxLayout.Y_AXIS)

        val damagePanel = damagePanel
        val bonusPanel = bonusPanel
        val summaryPanel = summaryPanel

        okButton.background = ColorStyle.DARKER_RED.color
        val okCancelPanel = SwingStyles.getConfirmCancelPanel(
            okButton,
            { _: ActionEvent? -> logAndFinish() },
            { _: ActionEvent? -> root.returnToButtons() }
        )

        SwingStyles.addComponents(
            this,
            damagePanel, Box.createVerticalStrut(10),
            bonusPanel, Box.createVerticalStrut(10),
            summaryPanel, Box.createVerticalStrut(15),
            okCancelPanel
        )

        setupListeners()
        updateUIState()

        SwingUtilities.invokeLater { mainDamageField.requestFocusInWindow() }
    }

    private val damagePanel: JPanel
        get() {
            val panel = JPanel(FlowLayout(FlowLayout.LEFT))
            SwingStyles.addLabeledBorder(panel, "Main Damage")

            val label = JLabel("Enter " + implement.damageString() + ":")

            mainDamageField.font = mainDamageField.font.deriveFont(16f)
            mainDamageField.horizontalAlignment = JTextField.CENTER

            panel.add(label)
            panel.add(mainDamageField)

            return panel
        }

    private val bonusPanel: JPanel
        get() {
            val panel = JPanel()
            panel.layout = FlowLayout(FlowLayout.LEFT)
            SwingStyles.addLabeledBorder(panel, "Bonus Damage")

            otherBonusDamageField.isEnabled = false

            panel.add(otherBonusDamageCheck)
            panel.add(otherBonusDamageField)

            return panel
        }

    private val summaryPanel: JPanel
        get() {
            val panel = JPanel()
            panel.layout = BoxLayout(panel, BoxLayout.Y_AXIS)
            SwingStyles.addLabeledBorder(panel, "Breakdown")

            if (implement is Weapon && !implement.isManual()) {
                panel.add(JLabel("Stat Bonus: +" + attacker.mod(implement.stat())))
            }
            if (implement.isHalfDamage) {
                panel.add(JLabel("Damage will be halved (Failed Attack)"))
            }
            if (target.isHexedBy(attacker)) {
                panel.add(JLabel("Hex: include +1d6 in bonus field"))
            }

            summaryLabel.font = summaryLabel.font.deriveFont(Font.BOLD, 16f)
            summaryLabel.foreground = ColorStyle.DARKER_RED.color
            summaryLabel.alignmentX = CENTER_ALIGNMENT

            panel.add(Box.createVerticalStrut(5))
            panel.add(summaryLabel)

            return panel
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
            mainDamageField.addKeyListener(IntegerFieldListener())
        } else {
            mainDamageField.addKeyListener(
                DieRollListener(
                    implement.numDice(),
                    implement.dieSize(),
                    mainDamageField
                )
            )
        }

        otherBonusDamageField.addKeyListener(IntegerFieldListener())
        otherBonusDamageCheck.addActionListener {
            val enabled = otherBonusDamageCheck.isSelected
            otherBonusDamageField.isEnabled = enabled

            if (!enabled) {
                otherBonusDamageField.text = ""
            }
            updateUIState()
        }
    }

    private fun calculateTotal(): Int {
        var damage = mainDamage + bonusDamage

        if (implement.isManual) {
            return damage
        }
        if (implement.isHalfDamage) {
            damage /= 2
        }
        if (implement is Weapon) {
            damage += attacker.mod(implement.stat())
        }

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

            if (effect == Effect.ILLUSION) {
                Message.informIllusion(target)
            } else if (effect == Effect.ADVANTAGE_SOON) {
                Message.template("Advantage on next attack against $target this turn.")
            }
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