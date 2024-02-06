package laiss.dicer.android.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import laiss.dicer.android.model.Dice

@Composable
@Preview(showBackground = true, showSystemUi = true)
fun SelectDicesScreenPreview() {
    SelectDicesScreen()
}

@Composable
fun SelectDicesScreen(selectDicesViewModel: SelectDicesViewModel = viewModel()) {
    SelectDicesLayout(
        bonus = selectDicesViewModel.bonus.collectAsState().value,
        threshold = selectDicesViewModel.threshold.collectAsState().value,
        countByDice = selectDicesViewModel.countByDice.collectAsState().value,
        onBonusChanged = { selectDicesViewModel.updateBonus(it) },
        onThresholdChanged = { selectDicesViewModel.updateThreshold(it) },
        onOkClicked = { TODO("Show calculation result") },
        onDiceCountMinusClicked = { selectDicesViewModel.increaseDiceCount(it) },
        onDiceCountChanged = { dice, count -> selectDicesViewModel.updateDiceCount(dice, count) },
        onDiceCountPlusClicked = { selectDicesViewModel.decreaseDiceCount(it) },
        modifier = Modifier
            .statusBarsPadding()
            .safeDrawingPadding()
            .padding(3.dp)
    )
}

@Composable
fun SelectDicesLayout(
    bonus: Int,
    threshold: Int,
    countByDice: HashMap<Dice, Int>,
    onBonusChanged: (String) -> Unit,
    onThresholdChanged: (String) -> Unit,
    onOkClicked: () -> Unit,
    onDiceCountMinusClicked: (Dice) -> Unit,
    onDiceCountChanged: (Dice, String) -> Unit,
    onDiceCountPlusClicked: (Dice) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxHeight(),
        verticalArrangement = Arrangement.SpaceAround,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(modifier = modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            NumberParameterSetter(
                name = "Bonus",
                value = bonus,
                onChanged = onBonusChanged,
                modifier = modifier
            )

            NumberParameterSetter(
                name = "Threshold",
                value = threshold,
                onChanged = onThresholdChanged,
                modifier = modifier
            )
        }

        LazyColumn(
            modifier = modifier.padding(horizontal = 15.dp),
            verticalArrangement = Arrangement.SpaceAround
        ) {
            items(Dice.entries)
            {
                DiceCountSetter(
                    dice = it,
                    count = countByDice[it] ?: 0,
                    onMinusClicked = { onDiceCountMinusClicked(it) },
                    onCountChanged = { count -> onDiceCountChanged(it, count) },
                    onPlusClicked = { onDiceCountPlusClicked(it) },
                    modifier = modifier
                )
            }
        }

        Button(onClick = onOkClicked, modifier = modifier.width(150.dp)) { Text(text = "OK") }
    }
}

@Composable
fun NumberParameterSetter(
    name: String,
    value: Int,
    onChanged: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = 5.dp)
    ) {
        Row(
            modifier = modifier,
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(text = "$name: ")

            OutlinedTextField(
                modifier = modifier.width(70.dp),
                value = "$value",
                onValueChange = onChanged
            )
        }
    }
}

@Composable
fun DiceCountSetter(
    dice: Dice,
    count: Int,
    onMinusClicked: () -> Unit,
    onCountChanged: (String) -> Unit,
    onPlusClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = 5.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically,
            modifier = modifier.fillMaxWidth()
        ) {
            Button(onClick = onMinusClicked) { Text(text = "-") }

            OutlinedTextField(
                value = "$count",
                modifier = modifier.width(70.dp),
                onValueChange = onCountChanged
            )

            Text(modifier = modifier, text = dice.name)

            Button(onClick = onPlusClicked, modifier = modifier) { Text(text = "+") }
        }
    }
}