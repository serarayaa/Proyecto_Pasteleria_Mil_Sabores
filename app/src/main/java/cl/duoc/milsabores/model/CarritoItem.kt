// app/src/main/java/cl/duoc/milsabores/cl.duoc.milsabores.model/CarritoItem.kt
package cl.duoc.milsabores.model

data class CarritoItem(
    val productoId: String,
    val nombre: String,
    val precio: Double,
    val imagen: String,
    var cantidad: Int = 1
) {
    val subtotal: Double get() = precio * cantidad
}

data class Carrito(
    val items: List<CarritoItem> = emptyList()
) {
    val total: Double get() = items.sumOf { it.subtotal }
    val cantidadTotal: Int get() = items.sumOf { it.cantidad }
}
