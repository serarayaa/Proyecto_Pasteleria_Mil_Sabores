// app/src/main/java/cl/duoc/milsabores/ui/mapper/EstadoPedidoUi.kt
package cl.duoc.milsabores.ui.mapper

import androidx.compose.ui.graphics.Color
import cl.duoc.milsabores.model.EstadoPedido

fun EstadoPedido.color(): Color = when (this) {
    EstadoPedido.PENDIENTE      -> Color(0xFFFFA726) // naranja
    EstadoPedido.EN_PREPARACION -> Color(0xFF42A5F5) // azul
    EstadoPedido.LISTO          -> Color(0xFF66BB6A) // verde
    EstadoPedido.ENTREGADO      -> Color(0xFF9E9E9E) // gris
}
