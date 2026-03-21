package character_info

import __main.Main

class LifeStatus {
    enum class Status {
        ALIVE, UNCONSCIOUS, DEAD
    }

    private var thisStatus: Status = Status.ALIVE

    private var deathSuccesses = 0
    private var deathFails = 0

    /**
     * takes the param death save roll. Checks for values > 20 or < 1 and
     * increments either death successes or fails according to the roll.
     * Calls updateLifeStatus() to handle death/resurrection
     */
    fun rollDeathSave(d20Roll: Int) {
        if (d20Roll > 20 || d20Roll < 1) {
            throw IndexOutOfBoundsException()
        }
        if (d20Roll > 10) {
            deathSuccesses++
        } else {
            deathFails++
        }

        if (deathSuccesses == 3) {
            thisStatus = Status.ALIVE
        } else if (deathFails == 3) {
            thisStatus = Status.DEAD
        }

        Main.logAction()
    }

    fun status(): Status {
        return thisStatus
    }

    val isConscious: Boolean
        get() = thisStatus == Status.ALIVE

    fun setUnconscious() {
        thisStatus = Status.UNCONSCIOUS
    }

    val isAlive: Boolean
        get() = thisStatus != Status.DEAD

    override fun toString(): String {
        return String.format("Defeated (%d-%d)", deathSuccesses, deathFails)
    }
}