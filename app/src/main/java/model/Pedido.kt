// app/src/main/java/cl/duoc/milsabores/model/Pedido.kt
package cl.duoc.milsabores.model

data class Pedido(
    val id: String = "",
    val uid: String = "", // UID del usuario propietario del pedido
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

enum class EstadoPedido(val displayName: String) {
    PENDIENTE("Pendiente"),
    EN_PREPARACION("En Preparación"),
    LISTO("Listo para Recoger"),
    ENTREGADO("Entregado")
}
