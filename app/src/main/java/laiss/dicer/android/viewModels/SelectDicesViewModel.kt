package laiss.dicer.android.viewModels

import androidx.lifecycle.ViewModel
import arrow.core.raise.context.bind
import arrow.core.raise.context.either
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import laiss.dicer.android.model.Dice
import laiss.dicer.android.model.DiceSet
import laiss.dicer.android.model.Stats
import kotlin.math.sqrt

class SelectDicesViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(SelectDicesScreenState())
    val uiState: StateFlow<SelectDicesScreenState> = _uiState.asStateFlow()

    companion object {
        const val TABS_LIMIT = 4
    }

    fun updateBonus(bonusStr: String) {
        _uiState.update { it.copyWithBonus(bonusStr.toIntOrNull()) }
    }

    fun updateThreshold(thresholdStr: String) {
        _uiState.update { it.copyWithThreshold(thresholdStr.toIntOrNull()) }
    }

    fun updateDiceCount(dice: Dice, count: String) {
        _uiState.update { it.copyWithNewDiceCount(dice, count.toIntOrNull()) }
    }

    fun increaseDiceCount(dice: Dice) {
        val oldCount = with(uiState.value) { layoutStates[activeTabIndex].countByDice[dice] ?: 0 }
        updateDiceCount(dice, (oldCount + 1).toString())
    }

    fun decreaseDiceCount(dice: Dice) {
        val oldCount = with(uiState.value) {
            layoutStates[activeTabIndex].countByDice[dice].takeIf { it != null && it > 0 } ?: 1
        }
        updateDiceCount(dice, (oldCount - 1).toString())
    }

    fun selectTab(index: Int) {
        if (index !in 0..<uiState.value.layoutStates.size) throw IndexOutOfBoundsException("$index")
        _uiState.update { it.copy(activeTabIndex = index) }
    }

    fun createTab() {
        if (uiState.value.layoutStates.size < TABS_LIMIT) _uiState.update { it.copyWithNewTab() }
    }

    fun closeActiveTab() {
        _uiState.update { it.copyWithClosedActiveTab() }
    }

    fun getResults() = either {
        uiState.value.layoutStates
            .map { layoutState ->
                with(layoutState.toStats().bind()) {
                    Result(
                        expectation = expectation,
                        deviation = sqrt(dispersion),
                        probability = distribution.allPossibleOutcomes()
                            .filter { (outcome, _) -> (layoutState.threshold ?: 0) <= outcome }
                            .sumOf { (_, probability) -> probability },
                        checkDescription = "$this | ${layoutState.threshold ?: 0}"
                    )
                }
            }
            .let { results -> Results(results) }
    }
}

data class SelectDicesScreenState(
    val activeTabIndex: Int = 0,
    val layoutStates: List<SelectDicesLayoutState> = listOf(SelectDicesLayoutState())
) {
    fun copyWithBonus(bonus: Int?): SelectDicesScreenState =
        copy(layoutStates = layoutStates.mapIndexed { index, entry ->
            if (index == activeTabIndex) entry.copy(bonus = bonus) else entry.copy()
        })

    fun copyWithThreshold(threshold: Int?) =
        copy(layoutStates = layoutStates.mapIndexed { index, entry ->
            if (index == activeTabIndex) entry.copy(threshold = threshold) else entry.copy()
        })

    fun copyWithNewDiceCount(dice: Dice, count: Int?) =
        copy(layoutStates = layoutStates.mapIndexed { index, entry ->
            if (index == activeTabIndex) entry.copy(countByDice = entry.countByDice.plus(dice to count))
            else entry.copy()
        })

    fun copyWithNewTab() =
        copy(layoutStates = layoutStates.plus(layoutStates[activeTabIndex].copy()))

    fun copyWithClosedActiveTab(): SelectDicesScreenState = when (layoutStates.size) {
        1 -> copy()
        else -> copy(activeTabIndex = (activeTabIndex - 1).takeIf { it > 0 }
            ?: (layoutStates.size - 2),
            layoutStates = layoutStates - layoutStates[activeTabIndex])
    }
}

data class SelectDicesLayoutState(
    val bonus: Int? = 0,
    val threshold: Int? = 3,
    val countByDice: Map<Dice, Int?> = mapOf(Dice.D6 to 1)
)

fun SelectDicesLayoutState.toStats() = either {
    val dicesAndCounts = countByDice
        .filter { (it.value ?: 0) > 0 }
        .map { it.key to it.value!! }
        .toMap()
    Stats(DiceSet.create(dicesAndCounts).bind(), bonus ?: 0)
}

data class Results(
    val layoutResults: List<Result>
)

data class Result(
    val checkDescription: String,
    val expectation: Double,
    val deviation: Double,
    val probability: Double
)