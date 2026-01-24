package laiss.dicer.android.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import arrow.core.Either
import laiss.dicer.android.model.Dice
import laiss.dicer.android.model.NegativeDiceCount
import laiss.dicer.android.ui.theme.DicerTheme
import laiss.dicer.android.viewModels.Result
import laiss.dicer.android.viewModels.Results
import laiss.dicer.android.viewModels.SelectDicesViewModel
import org.koin.androidx.compose.koinViewModel
import java.util.Locale

@Composable
@Preview(backgroundColor = 0xff24273a, showBackground = true, showSystemUi = false)
fun SelectDicesScreenPreview() = DicerTheme { SelectDicesScreen() }

@Composable
fun SelectDicesScreen(selectDicesViewModel: SelectDicesViewModel = koinViewModel()) {
    val results = remember { mutableStateOf<Either<NegativeDiceCount, Results>?>(null) }
    val modifier = Modifier
        .statusBarsPadding()
        .safeDrawingPadding()
        .padding(3.dp)

    val state = selectDicesViewModel.uiState.collectAsState().value

    Surface(color = MaterialTheme.colorScheme.background) {
        Column {
            TabRow(selectedTabIndex = state.activeTabIndex) {
                state.layoutStates.forEachIndexed { index, _ ->
                    Tab(
                        text = { Text(text = "Option $index") },
                        selected = index == state.activeTabIndex,
                        onClick = { selectDicesViewModel.selectTab(index) })
                }
                if (state.layoutStates.size < SelectDicesViewModel.TABS_LIMIT) {
                    Tab(icon = {
                        Icon(
                            imageVector = Icons.Default.Add, contentDescription = "New tab"
                        )
                    }, selected = false, onClick = { selectDicesViewModel.createTab() })
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
                onDiceCountChanged = { dice, count ->
                    selectDicesViewModel.updateDiceCount(
                        dice, count
                    )
                },
                onDiceCountPlusClicked = { selectDicesViewModel.increaseDiceCount(it) },
                isClosable = state.layoutStates.size > 1,
                onCloseButtonClicked = { selectDicesViewModel.closeActiveTab() },
                modifier = modifier
            )
        }
    }

    val resultsValue = results.value?.getOrNull()
    if (resultsValue != null) {
        ResultDialog(
            onDismissRequest = { results.value = null }, results = resultsValue, modifier = modifier
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
            NumberParameterSetter(
                name = "Bonus", value = bonus, onChanged = onBonusChanged, modifier = modifier
            )

            NumberParameterSetter(
                name = "Threshold",
                value = threshold,
                onChanged = onThresholdChanged,
                modifier = modifier
            )
        }

        LazyColumn(
            modifier = modifier.padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.SpaceBetween
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
            if (isClosable) OutlinedButton(
                onClick = onCloseButtonClicked, modifier = modifier.width(150.dp)
            ) { Text(text = "Close") }

            Button(onClick = onOkClicked, modifier = modifier.width(150.dp)) { Text(text = "OK") }
        }
    }
}

@Composable
fun NumberParameterSetter(
    name: String, value: Int?, onChanged: (String) -> Unit, modifier: Modifier = Modifier
) {
    ElevatedCard(modifier = Modifier) {
        Row(
            modifier = modifier.padding(5.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
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
    ElevatedCard(
        modifier = modifier
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically,
            modifier = modifier.fillMaxWidth()
        ) {
            IconButton(onClick = onMinusClicked, colors = with(MaterialTheme.colorScheme) {
                IconButtonDefaults.iconButtonColors(
                    containerColor = surface,
                    contentColor = primary,
                    disabledContainerColor = secondary,
                    disabledContentColor = onSecondary
                )
            }) { Icon(Icons.Filled.KeyboardArrowDown, "remove") }

            OutlinedTextField(
                value = count?.toString() ?: "",
                modifier = modifier.width(70.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                onValueChange = onCountChanged
            )

            Text(modifier = modifier.width(50.dp), text = dice.name)

            IconButton(onClick = onPlusClicked, colors = with(MaterialTheme.colorScheme) {
                IconButtonDefaults.iconButtonColors(
                    containerColor = surface,
                    contentColor = primary,
                    disabledContainerColor = secondary,
                    disabledContentColor = onSecondary
                )
            }) { Icon(Icons.Filled.KeyboardArrowUp, "add") }
        }
    }
}

@Preview(backgroundColor = 0xff24273a, showBackground = true)
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

    DicerTheme {
        ResultDialog(
            onDismissRequest = {},
            results = exampleResults,
            modifier = Modifier.padding(3.dp)
        )
    }
}

@Composable
fun ResultDialog(
    onDismissRequest: () -> Unit, results: Results, modifier: Modifier = Modifier
) {
    Dialog(onDismissRequest = onDismissRequest) {
        LazyColumn(
            modifier = modifier,
            verticalArrangement = Arrangement.spacedBy(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            items(results.layoutResults) {
                ElevatedCard {
                    Column(
                        modifier = Modifier.padding(7.dp),
                        horizontalAlignment = Alignment.Start
                    ) {
                        Row(
                            modifier = modifier,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "Probability:", fontSize = 20.sp)
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "%.2f".format(Locale.ROOT, it.probability),
                                fontSize = 20.sp,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "${"Expectation"}:",
                                fontSize = 15.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "%.2f".format(Locale.ROOT, it.expectation),
                                fontSize = 15.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "${"Deviation"}:",
                                fontSize = 15.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "%.2f".format(Locale.ROOT, it.deviation),
                                fontSize = 15.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = it.checkDescription,
                            fontSize = 15.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }

    }
}