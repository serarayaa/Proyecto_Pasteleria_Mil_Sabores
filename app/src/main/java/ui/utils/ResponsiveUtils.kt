package cl.duoc.milsabores.ui.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Utilidades responsive. Mantengo tus funciones para compatibilidad
 * y agrego variantes @Composable que responden al ancho de pantalla.
 */
object ResponsiveUtils {

    // ======== Compatibilidad (tu API actual) ========
    fun getColumnCount(): Int = 2
    fun getHorizontalPadding(): Dp = 16.dp
    fun getVerticalSpacing(): Dp = 12.dp
    fun getTitleFontSize(): Float = 18f
    fun getProductCardMinHeight(): Dp = 320.dp

    // ======== Variantes responsivas (opcional usar) ========
    @Composable
    fun columnCount(): Int {
        val widthDp = LocalConfiguration.current.screenWidthDp
        return when {
            widthDp >= 840 -> 4     // tablets grandes / desktop
            widthDp >= 600 -> 3     // tablets
            widthDp >= 400 -> 2     // teléfonos grandes
            else -> 1               // teléfonos muy compactos
        }
    }

    @Composable
    fun horizontalPadding(): Dp {
        val widthDp = LocalConfiguration.current.screenWidthDp
        return when {
            widthDp >= 840 -> 24.dp
            widthDp >= 600 -> 20.dp
            else -> 16.dp
        }
    }

    @Composable
    fun verticalSpacing(): Dp = 12.dp

    @Composable
    fun titleFontSizeSp(): TextUnit {
        val widthDp = LocalConfiguration.current.screenWidthDp
        return when {
            widthDp >= 840 -> 22.sp
            widthDp >= 600 -> 20.sp
            else -> 18.sp
        }
    }

    @Composable
    fun productCardMinHeight(): Dp {
        val widthDp = LocalConfiguration.current.screenWidthDp
        return when {
            widthDp >= 600 -> 360.dp
            else -> 320.dp
        }
    }
}
