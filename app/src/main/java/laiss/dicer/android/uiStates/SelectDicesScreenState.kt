package laiss.dicer.android.uiStates

import arrow.core.Either
import arrow.core.NonEmptyList
import arrow.core.nonEmptyListOf
import arrow.optics.optics
import laiss.dicer.android.model.Dice

@optics
data class SelectDicesScreenState(
    val activeTabIndex: Int = 0,
    val layoutStates: NonEmptyList<SelectDicesLayoutState> =
        nonEmptyListOf(SelectDicesLayoutState()),
    val estimates: NonEmptyList<Either<EstimateError, Estimate>>? = null,
) {
    companion object
}

@optics
data class SelectDicesLayoutState(
    val bonus: Int? = 0,
    val threshold: Int? = 3,
    val countByDice: Map<Dice, Int> = mapOf(Dice.D6 to 1)
) {
    companion object
}

@optics
data class Estimate(
    val checkDescription: String,
    val expectation: Double,
    val deviation: Double,
    val probability: Double
) {
    companion object
}

sealed interface EstimateError {
    data object NegativeDiceCount : EstimateError
    data object IncorrectDistribution : EstimateError
}