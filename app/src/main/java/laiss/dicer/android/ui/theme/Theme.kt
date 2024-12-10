package laiss.dicer.android.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    background = Base,
    onBackground = Text,

    surface = Surface1,
    surfaceVariant = Surface0,
    surfaceTint = Surface0,
    inverseSurface = Crust,
    onSurface = Text,
    onSurfaceVariant = Subtext0,
    inverseOnSurface = Text,

    primary = Peach,
    primaryContainer = Rosewater,
    inversePrimary = Rosewater,
    onPrimary = Mantle,
    onPrimaryContainer = Mantle,

    secondary = Surface2,
    secondaryContainer = Overlay2,
    onSecondary = Text,
    onSecondaryContainer = Crust,

    tertiary = Yellow,
    tertiaryContainer = Lavender,
    onTertiary = Text,
    onTertiaryContainer = Crust,

    outline = Lavender,
    outlineVariant = Overlay0,

    scrim = MantleHalfOpacity,

    error = Red,
    errorContainer = Pink,
    onError = Text,
    onErrorContainer = Mantle
)

//private val LightColorScheme = lightColorScheme()

private val LightColorScheme = DarkColorScheme

@Composable
fun DicerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}