package laiss.dicer.android.ui

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import laiss.dicer.android.model.Dice

class SelectDicesViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(SelectDicesScreenState())
    val uiState: StateFlow<SelectDicesScreenState> = _uiState.asStateFlow()

    fun updateBonus(bonusStr: String) {
        _uiState.update { it.copy(bonus = bonusStr.toInt()) }  // TODO: Validate input
    }

    fun updateThreshold(thresholdStr: String) {
        _uiState.update { it.copy(threshold = thresholdStr.toInt()) }  // TODO: Validate input
    }

    fun updateDiceCount(dice: Dice, count: String) {
        _uiState.update {
            it.copy(countByDice = it.countByDice.plus(dice to count.toInt()))  // TODO: Validate input
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

    fun calculate() {
        TODO("Not yet implemented")
    }
}

data class SelectDicesScreenState(
    val bonus: Int = 0,
    val threshold: Int = 20,
    val countByDice: Map<Dice, Int> = mapOf(Dice.D6 to 1)
)