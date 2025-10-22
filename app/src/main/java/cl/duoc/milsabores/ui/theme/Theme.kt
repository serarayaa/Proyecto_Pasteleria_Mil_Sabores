package cl.duoc.milsabores.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = PastelPink,
    secondary = CreamyBeige,
    tertiary = StrawberryRed,
    background = DarkChocolate,
    surface = DeepBrown,
    onPrimary = DarkChocolate,
    onSecondary = DarkChocolate,
    onBackground = VanillaWhite,
    onSurface = VanillaWhite
)

private val LightColorScheme = lightColorScheme(
    primary = StrawberryRed,
    secondary = PastelPeach,
    tertiary = CaramelGold,
    background = VanillaWhite,
    surface = PastelCream,
    primaryContainer = GradientPink,
    secondaryContainer = GradientOrange,
    onPrimary = androidx.compose.ui.graphics.Color.White,
    onSecondary = ChocolateBrown,
    onBackground = ChocolateBrown,
    onSurface = ChocolateBrown
)

@Composable
fun MilsaboresTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
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

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}