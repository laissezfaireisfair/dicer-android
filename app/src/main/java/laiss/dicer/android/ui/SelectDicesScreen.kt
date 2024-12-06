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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import laiss.dicer.android.model.Dice
import java.util.*

@Composable
@Preview(showBackground = true, showSystemUi = true)
fun SelectDicesScreenPreview() {
    SelectDicesScreen()
}

@Composable
fun SelectDicesScreen(selectDicesViewModel: SelectDicesViewModel = viewModel()) {
    val results = remember { mutableStateOf<Results?>(null) }
    val modifier = Modifier
        .statusBarsPadding()
        .safeDrawingPadding()
        .padding(3.dp)

    val state = selectDicesViewModel.uiState.collectAsState().value

    Column {
        TabRow(selectedTabIndex = state.activeTabIndex) {
            state.layoutStates.forEachIndexed { index, _ ->
                Tab(
                    text = { Text(text = "Option $index") },
                    selected = index == state.activeTabIndex,
                    onClick = { selectDicesViewModel.selectTab(index) }
                )
            }
            if (state.layoutStates.size < SelectDicesViewModel.TABS_LIMIT) {
                Tab(
                    icon = { Icon(imageVector = Icons.Default.Add, contentDescription = "New tab") },
                    selected = false,
                    onClick = { selectDicesViewModel.createTab() }
                )
            }
        }

        val activeLayoutState = with(state) { layoutStates[activeTabIndex] }
        SelectDicesLayout(
            bonus = activeLayoutState.bonus,
            threshold = activeLayoutState.threshold,
            countByDice = activeLayoutState.countByDice,
            onBonusChanged = { selectDicesViewModel.updateBonus(it) },
            onThresholdChanged = { selectDicesViewModel.updateThreshold(it) },
            onOkClicked = { results.value = selectDicesViewModel.getResults() },
            onDiceCountMinusClicked = { selectDicesViewModel.decreaseDiceCount(it) },
            onDiceCountChanged = { dice, count -> selectDicesViewModel.updateDiceCount(dice, count) },
            onDiceCountPlusClicked = { selectDicesViewModel.increaseDiceCount(it) },
            isClosable = state.layoutStates.size > 1,
            onCloseButtonClicked = { selectDicesViewModel.closeActiveTab() },
            modifier = modifier
        )
    }

    val resultsValue = results.value
    if (resultsValue != null) {
        ResultDialog(
            onDismissRequest = { results.value = null },
            results = resultsValue,
            modifier = modifier
        )
    }
}

@Composable
fun SelectDicesLayout(
    bonus: Int?,
    threshold: Int?,
    countByDice: Map<Dice, Int?>,
    onBonusChanged: (String) -> Unit,
    onThresholdChanged: (String) -> Unit,
    onOkClicked: () -> Unit,
    onDiceCountMinusClicked: (Dice) -> Unit,
    onDiceCountChanged: (Dice, String) -> Unit,
    onDiceCountPlusClicked: (Dice) -> Unit,
    isClosable: Boolean,
    onCloseButtonClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxHeight(),
        verticalArrangement = Arrangement.SpaceAround,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(modifier = modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            NumberParameterSetter(name = "Bonus", value = bonus, onChanged = onBonusChanged, modifier = modifier)

            NumberParameterSetter(
                name = "Threshold", value = threshold, onChanged = onThresholdChanged, modifier = modifier
            )
        }

        LazyColumn(
            modifier = modifier.padding(horizontal = 15.dp), verticalArrangement = Arrangement.SpaceAround
        ) {
            items(Dice.entries) {
                DiceCountSetter(
                    dice = it,
                    count = if (!countByDice.containsKey(it)) 0 else countByDice[it],
                    onMinusClicked = { onDiceCountMinusClicked(it) },
                    onCountChanged = { count -> onDiceCountChanged(it, count) },
                    onPlusClicked = { onDiceCountPlusClicked(it) },
                    modifier = modifier
                )
            }
        }

        Row(horizontalArrangement = Arrangement.SpaceEvenly, modifier = modifier.fillMaxWidth()) {
            if (isClosable)
                Button(onClick = onCloseButtonClicked, modifier = modifier.width(150.dp)) { Text(text = "Close") }

            Button(onClick = onOkClicked, modifier = modifier.width(150.dp)) { Text(text = "OK") }
        }
    }
}

@Composable
fun NumberParameterSetter(
    name: String, value: Int?, onChanged: (String) -> Unit, modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier, elevation = CardDefaults.cardElevation(defaultElevation = 5.dp)
    ) {
        Row(
            modifier = modifier,
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(text = "$name: ")

            OutlinedTextField(
                modifier = modifier.width(70.dp),
                value = value?.toString() ?: "",
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                onValueChange = onChanged,
            )
        }
    }
}

@Composable
fun DiceCountSetter(
    dice: Dice,
    count: Int?,
    onMinusClicked: () -> Unit,
    onCountChanged: (String) -> Unit,
    onPlusClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier, elevation = CardDefaults.cardElevation(defaultElevation = 5.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically,
            modifier = modifier.fillMaxWidth()
        ) {
            Button(onClick = onMinusClicked) { Text(text = "-") }

            OutlinedTextField(
                value = count?.toString() ?: "",
                modifier = modifier.width(70.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                onValueChange = onCountChanged
            )

            Text(modifier = modifier.width(50.dp), text = dice.name)

            Button(onClick = onPlusClicked, modifier = modifier) { Text(text = "+") }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ResultDialogPreview() {
    val exampleResults = Results(
        layoutResults = listOf(
            Result(
                checkDescription = "2d4 + d6 + d8 + d12 + 5 | 20",
                expectation = 24.5,
                deviation = 7.3,
                probability = 0.79
            )
        )
    )

    ResultDialog(
        onDismissRequest = {},
        results = exampleResults,
        modifier = Modifier.padding(3.dp)
    )
}

@Composable
fun ResultDialog(
    onDismissRequest: () -> Unit,
    results: Results,
    modifier: Modifier = Modifier,
) {
    Dialog(onDismissRequest = onDismissRequest) {
        LazyColumn {
            items(results.layoutResults) {
                Card(modifier = modifier, elevation = CardDefaults.cardElevation(defaultElevation = 5.dp)) {
                    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
                        OutlinedCard(modifier = modifier) {
                            Text(text = it.checkDescription, modifier = modifier.padding(5.dp))
                        }
                        ResultEntry(name = "Expectation", value = it.expectation, modifier = modifier)
                        ResultEntry(name = "Deviation", value = it.deviation, modifier = modifier)
                        ResultEntry(name = "Probability", value = it.probability, modifier = modifier)
                    }
                }
            }
        }

    }
}

@Composable
fun ResultEntry(name: String, value: Double, modifier: Modifier) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        Text(text = "$name:", modifier = modifier)
        OutlinedCard(modifier = modifier) {
            Text(text = "%.2f".format(Locale.ROOT, value), modifier = modifier.padding(5.dp))
        }
    }
}