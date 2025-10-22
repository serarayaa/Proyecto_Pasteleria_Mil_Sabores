package cl.duoc.milsabores.model

data class Pedido(
    val id: String = "",
    val fecha: Long = System.currentTimeMillis(),
    val productos: List<ProductoPedido> = emptyList(),
    val total: Double = 0.0,
    val estado: EstadoPedido = EstadoPedido.PENDIENTE,
    val observaciones: String = ""
)

data class ProductoPedido(
    val nombre: String,
    val cantidad: Int,
    val precio: Double
)

enum class EstadoPedido(val displayName: String, val color: androidx.compose.ui.graphics.Color) {
    PENDIENTE("Pendiente", androidx.compose.ui.graphics.Color(0xFFFFA726)),
    EN_PREPARACION("En Preparación", androidx.compose.ui.graphics.Color(0xFF42A5F5)),
    LISTO("Listo para Recoger", androidx.compose.ui.graphics.Color(0xFF66BB6A)),
    ENTREGADO("Entregado", androidx.compose.ui.graphics.Color(0xFF9E9E9E))
}

