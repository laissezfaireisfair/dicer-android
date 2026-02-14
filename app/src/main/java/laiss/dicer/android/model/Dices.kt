package laiss.dicer.android.model

import arrow.core.raise.context.either
import arrow.core.raise.context.ensure
import kotlin.math.pow

enum class Dice(val faces: Int) {
    D2(2), D4(4), D6(6), D8(8), D10(10), D12(12), D20(20), D100(100);

    val expectation by lazy { (1.0 + faces) / 2 }

    val dispersion by lazy { (1.0 + faces * faces) / 2 - expectation.pow(2) }

    val distribution by lazy {
        Distribution().apply { (1..faces).forEach { this[it.toDouble()] = 1.0 / faces } }
    }

    override fun toString() = "d$faces"
}

class DiceSet private constructor(private val countByDice: Map<Dice, Int>) {
    val expectation by lazy {
        countByDice.asSequence().sumOf { (dice, count) -> dice.expectation * count }
    }

    val dispersion by lazy {
        countByDice.asSequence().sumOf { (dice, count) -> dice.dispersion * count }
    }

    val distribution by lazy {
        Distribution()
            .apply { set(0.0, 1.0) }
            .apply {
                countByDice
                    .asSequence()
                    .map { (dice, count) -> generateSequence(dice) { it }.take(count) }
                    .flatten()
                    .forEach { plusAssign(it.distribution) }
            }
    }

    override fun toString(): String = countByDice
        .filter { (_, count) -> count > 0 }
        .map { (dice, count) -> if (count > 1) "$count$dice" else "$dice" }
        .joinToString(" + ") { it }

    companion object {
        fun create(countByDice: Map<Dice, Int>) = either {
            ensure(countByDice.values.all { it >= 0 }) { NegativeDiceCount }
            DiceSet(countByDice)
        }
    }
}

data object NegativeDiceCount