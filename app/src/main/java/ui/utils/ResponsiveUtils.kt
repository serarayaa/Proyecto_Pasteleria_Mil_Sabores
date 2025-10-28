package cl.duoc.milsabores.ui.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Objeto con utilidades para adaptar diseños a diferentes resoluciones
 */
object ResponsiveUtils {

    /**
     * Obtiene el número de columnas basado en el ancho disponible
     * Por defecto retorna 2 columnas para mejor compatibilidad
     */
    fun getColumnCount(): Int {
        return 2 // 2 columnas para teléfonos, se puede mejorar en futuro
    }

    /**
     * Obtiene el padding horizontal
     */
    fun getHorizontalPadding(): Dp {
        return 16.dp
    }

    /**
     * Obtiene el espaciado vertical
     */
    fun getVerticalSpacing(): Dp {
        return 12.dp
    }

    /**
     * Obtiene el tamaño de fuente base para el título
     */
    fun getTitleFontSize(): Float {
        return 18f
    }

    /**
     * Calcula el tamaño mínimo de la tarjeta de producto
     */
    fun getProductCardMinHeight(): Dp {
        return 320.dp
    }
}

