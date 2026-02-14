package laiss.dicer.android.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import arrow.core.raise.context.bind
import arrow.core.raise.context.either
import arrow.core.toNonEmptyListOrThrow
import arrow.optics.copy
import arrow.optics.dsl.index
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import laiss.dicer.android.model.Dice
import laiss.dicer.android.model.DiceSet
import laiss.dicer.android.model.IncorrectDistribution
import laiss.dicer.android.model.NegativeDiceCount
import laiss.dicer.android.model.Stats
import laiss.dicer.android.model.countPassThresholdProbability
import laiss.dicer.android.uiStates.Estimate
import laiss.dicer.android.uiStates.EstimateError
import laiss.dicer.android.uiStates.SelectDicesLayoutState
import laiss.dicer.android.uiStates.SelectDicesScreenState
import laiss.dicer.android.uiStates.activeTabIndex
import laiss.dicer.android.uiStates.bonus
import laiss.dicer.android.uiStates.countByDice
import laiss.dicer.android.uiStates.layoutStates
import laiss.dicer.android.uiStates.threshold
import kotlin.math.sqrt

private const val TABS_LIMIT = 4

class SelectDicesViewModel(
    private val defaultDispatcher: CoroutineDispatcher,
) : ViewModel() {
    private val _uiState = MutableStateFlow(SelectDicesScreenState())
    val uiState: StateFlow<SelectDicesScreenState> = _uiState.asStateFlow()

    fun updateBonus(bonusStr: String) = viewModelScope.launch {
        _uiState.update { it.setBonusOnActiveTab(bonusStr.toIntOrNull()) }
    }

    fun updateThreshold(thresholdStr: String) = viewModelScope.launch {
        _uiState.update { it.setThresholdOnActiveTab(thresholdStr.toIntOrNull()) }
    }

    fun updateDiceCount(dice: Dice, count: String) = viewModelScope.launch {
        _uiState.update { it.setDiceCount(dice, count.toIntOrNull() ?: 0) }
    }

    fun increaseDiceCount(dice: Dice) = viewModelScope.launch {
        _uiState.update { it.incrementDiceCount(dice) }
    }

    fun decreaseDiceCount(dice: Dice) = viewModelScope.launch {
        _uiState.update { it.decrementDiceCount(dice) }
    }

    fun selectTab(tabIndex: Int) = viewModelScope.launch {
        _uiState.update { it.setActiveTab(tabIndex) }
    }

    fun openNewTab() = viewModelScope.launch {
        _uiState.update { it.openNewTab() }
    }

    fun closeActiveTab() = viewModelScope.launch {
        _uiState.update { it.closeActiveTab() }
    }

    fun calculateEstimates() = viewModelScope.launch(defaultDispatcher) {
        val estimates = uiState.value.layoutStates.map { layoutState ->
            either {
                val stats = layoutState.toStats()
                    .mapLeft { _: NegativeDiceCount -> EstimateError.NegativeDiceCount }
                    .bind()
                stats.run {
                    val threshold = layoutState.threshold ?: 0
                    Estimate(
                        expectation = expectation,
                        deviation = sqrt(dispersion),
                        probability = countPassThresholdProbability(threshold)
                            .mapLeft { _: IncorrectDistribution ->
                                EstimateError.IncorrectDistribution
                            }
                            .bind(),
                        checkDescription = "$this | $threshold"
                    )
                }
            }
        }
        _uiState.update { it.copy(estimates = estimates) }
    }

    fun closeEstimates() = viewModelScope.launch {
        _uiState.update { it.copy(estimates = null) }
    }
}

val SelectDicesScreenState.isTabsLimitReached get() = layoutStates.size >= TABS_LIMIT

private fun SelectDicesLayoutState.toStats() = either {
    val dicesAndCounts = countByDice
        .filter { (it.value) > 0 }
        .map { it.key to it.value }
        .toMap()
    Stats(DiceSet.create(dicesAndCounts).bind(), bonus ?: 0)
}

private fun SelectDicesScreenState.setActiveTab(tabIndex: Int) = this.copy {
    require(tabIndex in layoutStates.indices) { "Switching to non-existent tab" }
    SelectDicesScreenState.activeTabIndex set tabIndex
}

private fun SelectDicesScreenState.setBonusOnActiveTab(newBonus: Int?) = this.copy {
    SelectDicesScreenState.layoutStates.index(activeTabIndex).bonus set newBonus
}

private fun SelectDicesScreenState.setThresholdOnActiveTab(newThreshold: Int?) = this.copy {
    SelectDicesScreenState.layoutStates.index(activeTabIndex).threshold set newThreshold
}

private fun SelectDicesScreenState.setDiceCount(dice: Dice, count: Int) = this.copy {
    SelectDicesScreenState.layoutStates.index(activeTabIndex)
        .countByDice transform { it + (dice to count) }
}

private fun SelectDicesScreenState.incrementDiceCount(dice: Dice) = this.copy {
    SelectDicesScreenState.layoutStates.index(activeTabIndex)
        .countByDice transform { it + (dice to (it[dice] ?: 0) + 1) }
}

private fun SelectDicesScreenState.decrementDiceCount(dice: Dice) = this.copy {
    SelectDicesScreenState.layoutStates.index(activeTabIndex)
        .countByDice.index(dice).transform { if (it < 1) 0 else it - 1 }
}

private fun SelectDicesScreenState.openNewTab() = this.copy {
    SelectDicesScreenState.layoutStates transform {
        if (it.size < TABS_LIMIT) it + SelectDicesLayoutState() else it
    }
    SelectDicesScreenState.activeTabIndex set layoutStates.lastIndex
}

private fun SelectDicesScreenState.closeActiveTab() = this.copy {
    check(layoutStates.size > 1) { "Closing last tab" }
    SelectDicesScreenState.activeTabIndex set 1
    SelectDicesScreenState.layoutStates.transform {
        it.filterIndexed { i, _ -> i != activeTabIndex }.toNonEmptyListOrThrow()
    }
}