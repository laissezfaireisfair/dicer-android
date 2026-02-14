package laiss.dicer.android.model

class Distribution {
    private val probabilityByValue = mutableMapOf<Double, Double>()
    private val allowedDeviation = 1e-10

    val isCorrect
        get() = probabilityByValue.values.sum() in
                0.0 - allowedDeviation..1.0 + allowedDeviation

    operator fun get(value: Double) = probabilityByValue[value] ?: 0.0

    operator fun set(value: Double, probability: Double) {
        probabilityByValue[value] = probability
    }

    operator fun plus(other: Distribution) = run {
        val values = allPossibleOutcomes()
        val otherValues = other.allPossibleOutcomes()

        Distribution().apply {
            values.forEach { (value, probability) ->
                otherValues.forEach { this[value + it.first] += probability * it.second }
            }
        }
    }

    operator fun plusAssign(other: Distribution) {
        val sum = this + other
        probabilityByValue.clear()
        sum.probabilityByValue.forEach { (value, probability) ->
            this[value] = probability
        }
    }

    fun allPossibleOutcomes() = probabilityByValue.toList()

    override fun toString() = allPossibleOutcomes()
        .joinToString("\n") { (value, probability) -> "$value: $probability" }
}

