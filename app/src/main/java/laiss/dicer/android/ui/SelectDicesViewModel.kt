package laiss.dicer.android.ui

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import laiss.dicer.android.model.Dice
import laiss.dicer.android.model.DiceSet
import laiss.dicer.android.model.Stats
import laiss.dicer.android.model.countPassThresholdProbability
import kotlin.math.sqrt

class SelectDicesViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(SelectDicesScreenState())
    val uiState: StateFlow<SelectDicesScreenState> = _uiState.asStateFlow()

    fun updateBonus(bonusStr: String) {
        _uiState.update { it.copy(bonus = bonusStr.toIntOrNull() ?: 0) }
    }

    fun updateThreshold(thresholdStr: String) {
        _uiState.update { it.copy(threshold = thresholdStr.toIntOrNull() ?: 0) }
    }

    fun updateDiceCount(dice: Dice, count: String) {
        _uiState.update {
            it.copy(countByDice = it.countByDice.plus(dice to (count.toIntOrNull() ?: 0)))
        }
    }

    fun increaseDiceCount(dice: Dice) {
        val oldCount = uiState.value.countByDice[dice] ?: 0
        updateDiceCount(dice, (oldCount + 1).toString())
    }

    fun decreaseDiceCount(dice: Dice) {
        val oldCount = uiState.value.countByDice[dice].takeIf { it != null && it > 0 } ?: 0
        updateDiceCount(dice, (oldCount - 1).toString())
    }

    fun getResults() = with(uiState.value.toStats()) {
        Results(
            checkDescription = "$this | ${uiState.value.threshold}",
            expectation = expectation,
            deviation = sqrt(dispersion),
            probability = countPassThresholdProbability(uiState.value.threshold)
        )
    }
}

data class SelectDicesScreenState(
    val bonus: Int = 0,
    val threshold: Int = 3,
    val countByDice: Map<Dice, Int> = mapOf(Dice.D6 to 1)
)

fun SelectDicesScreenState.toStats(): Stats {
    val dicesAndCounts = countByDice.filter { it.value > 0 }.toList()
    return Stats(DiceSet(dicesAndCounts), bonus)
}

data class Results(
    val checkDescription: String,
    val expectation: Double,
    val deviation: Double,
    val probability: Double
)