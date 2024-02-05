package laiss.dicer.android.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
        Modifier.padding(3.dp)
    )
}

@Composable
fun SelectDicesLayout(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxHeight(),
        verticalArrangement = Arrangement.SpaceAround,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
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
                    Text(text = "Bonus: ")

                    OutlinedTextField(
                        modifier = modifier.width(70.dp),
                        value = "3",
                        onValueChange = {/*TODO*/ }
                    )
                }
            }

            Card(
                modifier = modifier,
                elevation = CardDefaults.cardElevation(defaultElevation = 5.dp)
            ) {
                Row(
                    modifier = modifier,
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(text = "Threshold: ")

                    OutlinedTextField(
                        modifier = modifier.width(70.dp),
                        value = "20",
                        onValueChange = {/*TODO*/ }
                    )
                }
            }
        }


        LazyColumn(
            modifier = modifier.padding(horizontal = 15.dp,),
            verticalArrangement = Arrangement.SpaceAround
        ) {
            items(Dice.entries)
            {
                Card(
                    modifier = modifier,
                    elevation = CardDefaults.cardElevation(defaultElevation = 5.dp)
                ) {
                    DiceCountSetter(dice = it, modifier = modifier)
                }
            }
        }

        Button(onClick = { /*TODO*/ }, modifier = modifier.width(150.dp)) {
            Text(text = "OK")
        }
    }
}

@Composable
fun DiceCountSetter(dice: Dice, modifier: Modifier = Modifier) {
    Row(
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.fillMaxWidth()
    ) {
        Button(onClick = { /*TODO*/ }) {
            Text(text = "-")
        }

        OutlinedTextField(value = "0", modifier = modifier.width(70.dp), onValueChange = {})

        Text(modifier = modifier, text = dice.name)

        Button(onClick = { /*TODO*/ }, modifier = modifier) {
            Text(text = "+")
        }
    }
}