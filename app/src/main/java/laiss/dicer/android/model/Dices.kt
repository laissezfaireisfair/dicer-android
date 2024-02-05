package laiss.dicer.android.model

import kotlin.math.pow

enum class Dice(val faces: Int) {
    D2(2),
    D4(4),
    D6(6),
    D8(8),
    D10(10),
    D12(12),
    D20(20),
    D100(100);

    companion object {
        fun fromFaces(faces: Int): Dice? = Dice.entries.firstOrNull { it.faces == faces }
    }

    val expectation: Double by lazy { (1.0 + faces) / 2 }

    val dispersion: Double by lazy { (1.0 + faces * faces) / 2 - expectation.pow(2) }

    val distribution: Distribution by lazy {
        Distribution().apply {
            (1..faces).forEach { this[it.toDouble()] = 1.0 / faces }
        }
    }

    override fun toString(): String = "d$faces"
}

class DiceSet(private val dicesAndCounts: List<Pair<Dice, Int>>) {
    val expectation: Double by lazy { dicesAndCounts.sumOf { (dice, count) -> dice.expectation * count } }

    val dispersion: Double by lazy { dicesAndCounts.sumOf { (dice, count) -> dice.dispersion * count } }

    val distribution: Distribution by lazy {
        Distribution()
            .apply { set(0.0, 1.0) }
            .apply {
                dicesAndCounts
                    .asSequence()
                    .map { (dice, count) -> generateSequence(dice) { it }.take(count) }
                    .flatten()
                    .forEach { plusAssign(it.distribution) }
            }
    }

    override fun toString(): String = dicesAndCounts
        .map { (dice, count) -> "$count$dice" }
        .joinToString(" + ") { it }
}

class Stats(private val diceSet: DiceSet, private val bonus: Int) {
    companion object {
        fun emptyStats() = Stats(DiceSet(emptyList()), 0)
    }

    val expectation: Double by lazy { diceSet.expectation + bonus }

    val dispersion: Double by lazy { diceSet.dispersion }

    val distribution: Distribution by lazy {
        Distribution().apply {
            diceSet.distribution.allPossibleOutcomes().forEach { (value, probability) ->
                this[value + bonus] = probability
            }
        }
    }

    override fun toString() = "$diceSet + $bonus"
}

fun Stats.countPassThresholdProbability(threshold: Int): Double =
    this.distribution
        .takeIf { it.isCorrect }
        ?.allPossibleOutcomes()
        ?.asSequence()
        ?.filter { (value, _) -> value > threshold }
        ?.sumOf { (_, probability) -> probability }
        ?: throw RuntimeException("Dices.Distribution is incorrect:\n${this.distribution.allPossibleOutcomes()}")