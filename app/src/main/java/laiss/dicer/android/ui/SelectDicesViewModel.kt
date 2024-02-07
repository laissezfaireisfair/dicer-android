package laiss.dicer.android.ui

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import laiss.dicer.android.model.Dice

class SelectDicesViewModel : ViewModel() {
    var bonus = MutableStateFlow(0)
        private set
    var threshold = MutableStateFlow(0)
        private set
    var countByDice = MutableStateFlow(hashMapOf(Dice.D6 to 1))
        private set

    fun updateBonus(bonusStr: String) {
        bonus.value = bonusStr.toInt()  // TODO: Validate input
    }

    fun updateThreshold(thresholdStr: String) {
        threshold.value = thresholdStr.toInt()  // TODO: Validate input
    }

    fun updateDiceCount(dice: Dice, count: String) {
        countByDice.value[dice] = count.toInt()  // TODO: Validate input
    }

    fun increaseDiceCount(dice: Dice) {
        countByDice.value[dice] = (countByDice.value[dice] ?: 0) + 1  // TODO: Validate input
    }

    fun decreaseDiceCount(dice: Dice) {
        countByDice.value[dice] = countByDice.value[dice]
            ?.takeIf { it > 0 }
            ?.let { it - 1 } ?: 0  // TODO: Validate input
    }

    fun calculate() {
        TODO("Not yet implemented")
    }
}