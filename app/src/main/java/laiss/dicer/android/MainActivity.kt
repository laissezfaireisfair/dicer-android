package laiss.dicer.android

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import laiss.dicer.android.ui.SelectDicesScreen
import laiss.dicer.android.ui.theme.DicerTheme
import laiss.dicer.android.ui.theme.Rosewater

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
