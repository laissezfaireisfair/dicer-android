package laiss.dicer.android.model

import arrow.core.raise.context.either
import arrow.core.raise.context.ensure

class Stats(private val diceSet: DiceSet, private val bonus: Int) {
    val expectation by lazy { diceSet.expectation + bonus }

    val dispersion by lazy { diceSet.dispersion }

    val distribution by lazy {
        Distribution().apply {
            diceSet.distribution.allPossibleOutcomes().forEach { (value, probability) ->
                this[value + bonus] = probability
            }
        }
    }

    override fun toString() = if (bonus > 0) "$diceSet + $bonus" else "$diceSet"
}

fun Stats.countPassThresholdProbability(threshold: Int) = either {
    ensure(distribution.isCorrect) { IncorrectDistribution }
    distribution
        .allPossibleOutcomes()
        .asSequence()
        .filter { (value, _) -> value > threshold }
        .sumOf { (_, probability) -> probability }
}

data object IncorrectDistribution