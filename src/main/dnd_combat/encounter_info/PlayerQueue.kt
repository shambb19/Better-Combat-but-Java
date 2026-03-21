package encounter_info

import __main.Main
import character_info.combatant.Combatant
import util.Message

class PlayerQueue(
    private val friendlies: List<Combatant>,
    private val enemies: List<Combatant>
) {

    private var friendlyIndex: Int = 0
    private var enemyIndex: Int = -1

    var currentCombatant: Combatant? = null
        private set

    init {
        sortList(friendlies)
        sortList(enemies)

        if (friendlies.isNotEmpty()) {
            currentCombatant = friendlies[friendlyIndex]
        } else if (enemies.isNotEmpty()) {
            enemyIndex = 0
            currentCombatant = enemies[enemyIndex]
        }
    }

    fun endCurrentTurn() {
        if (friendlies.isEmpty() && enemies.isEmpty()) return

        if (enemyIndex == -1) {
            if (enemies.isNotEmpty()) {
                enemyIndex = 0
                currentCombatant = enemies[enemyIndex]
            } else {
                incrementFriendly()
            }
        } else {
            enemyIndex++
            if (enemyIndex < enemies.size) {
                currentCombatant = enemies[enemyIndex]
            } else {
                enemyIndex = -1
                incrementFriendly()
            }
        }

        processTurnStart()
    }

    private fun incrementFriendly() {
        friendlyIndex++
        if (friendlyIndex >= friendlies.size) {
            friendlyIndex = 0
        }
        currentCombatant = friendlies[friendlyIndex]
    }

    private fun processTurnStart() {
        val combatant = currentCombatant ?: return

        combatant.endDealtEffects()

        if (!combatant.lifeStatus().isConscious) {
            if (combatant.lifeStatus().isAlive) {
                val saveRoll = Message.getDeathSaveRoll()
                combatant.lifeStatus().rollDeathSave(saveRoll)
            }

            endCurrentTurn()
            return
        }

        Main.logAction()
    }

    private fun sortList(combatants: List<Combatant>) {
        (combatants as? MutableList<Combatant>)?.sortWith(
            compareByDescending<Combatant> { it.initiative() }.thenBy { it.name() }
        )
    }
}