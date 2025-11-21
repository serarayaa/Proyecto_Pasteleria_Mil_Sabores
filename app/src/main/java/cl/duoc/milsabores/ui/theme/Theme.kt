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
    // Colores vibrantes y modernos para modo oscuro
    primary = androidx.compose.ui.graphics.Color(0xFFFF4E88),        // Rosa-rojo vibrante
    secondary = androidx.compose.ui.graphics.Color(0xFFFFB84D),      // Naranja dorado brillante
    tertiary = androidx.compose.ui.graphics.Color(0xFFFF6B9D),       // Rosa coral

    // Fondos con mejor profundidad
    background = androidx.compose.ui.graphics.Color(0xFF0D0D0D),     // Negro más profundo
    surface = androidx.compose.ui.graphics.Color(0xFF1A1A1A),        // Superficie oscura con tinte
    surfaceVariant = androidx.compose.ui.graphics.Color(0xFF2D2D2D), // Cards elevadas

    // Colores de contenedores
    primaryContainer = androidx.compose.ui.graphics.Color(0xFF3D1F2E),   // Rosa oscuro
    secondaryContainer = androidx.compose.ui.graphics.Color(0xFF3D2F1F), // Naranja oscuro

    // Textos con excelente contraste
    onPrimary = androidx.compose.ui.graphics.Color(0xFFFFFFFF),      // Blanco puro
    onSecondary = androidx.compose.ui.graphics.Color(0xFFFFFFFF),    // Blanco puro
    onTertiary = androidx.compose.ui.graphics.Color(0xFFFFFFFF),     // Blanco puro
    onBackground = androidx.compose.ui.graphics.Color(0xFFF5F5F5),   // Blanco suave
    onSurface = androidx.compose.ui.graphics.Color(0xFFF5F5F5),      // Blanco suave
    onSurfaceVariant = androidx.compose.ui.graphics.Color(0xFFB8B8B8), // Gris claro

    // Bordes y líneas
    outline = androidx.compose.ui.graphics.Color(0xFF4A4A4A),        // Bordes visibles
    outlineVariant = androidx.compose.ui.graphics.Color(0xFF353535), // Bordes sutiles

    // Estados de error más vibrantes
    error = androidx.compose.ui.graphics.Color(0xFFFF5252),          // Rojo error brillante
    onError = androidx.compose.ui.graphics.Color(0xFFFFFFFF)
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
