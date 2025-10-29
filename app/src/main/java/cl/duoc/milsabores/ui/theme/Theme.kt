package cl.duoc.milsabores.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = PastelPink,
    secondary = CreamyBeige,
    tertiary = StrawberryRed,
    background = DarkChocolate,
    surface = DeepBrown,
    onPrimary = DarkChocolate,     // buen contraste con PastelPink en dark
    onSecondary = DarkChocolate,
    onTertiary = VanillaWhite,
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
    onPrimary = VanillaWhite,      // texto blanco sobre rojo frutilla
    onSecondary = ChocolateBrown,
    onTertiary = ChocolateBrown,
    onBackground = ChocolateBrown,
    onSurface = ChocolateBrown
)

@Composable
fun MilsaboresTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
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
        // shapes = Shapes // <- Si quieres bordes redondeados globales, define Shapes.kt y habilita esto
        content = content
    )
}
