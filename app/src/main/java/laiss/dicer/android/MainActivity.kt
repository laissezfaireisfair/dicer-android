package laiss.dicer.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import laiss.dicer.android.screens.SelectDicesScreen
import laiss.dicer.android.ui.theme.DicerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            DicerTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    SelectDicesScreen()
                }
            }
        }
    }
}
