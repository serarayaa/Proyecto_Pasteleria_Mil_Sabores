package cl.duoc.milsabores.repository.carrito

import cl.duoc.milsabores.model.CarritoItem
import cl.duoc.milsabores.repository.CarritoRepository as CoreRepo
import cl.duoc.milsabores.ui.model.Producto
import kotlinx.coroutines.flow.StateFlow
import java.math.BigDecimal

class CarritoRepository(
    private val delegate: CoreRepo = CoreRepo.getInstance()
) {
    val items: StateFlow<List<CarritoItem>> = delegate.items

    fun agregarProducto(prod: Producto, cantidad: Int = 1) {
        val precioDouble = prod.precio
            .replace("$", "")
            .replace(".", "")
            .replace(",", ".")
            .trim()
            .toDoubleOrNull() ?: 0.0
        val item = CarritoItem(
            productoId = prod.id.toString(),
            nombre = prod.titulo,
            precio = precioDouble,
            imagen = prod.imagenRes.toString(),
            cantidad = cantidad
        )
        // Si ya existe, actualizar cantidad; si no, agregar
        val existente = items.value.find { it.productoId == item.productoId }
        if (existente != null) {
            actualizarCantidad(prod.id.toLong(), existente.cantidad + cantidad)
        } else {
            delegate.agregarProducto(item)
        }
    }

    fun actualizarCantidad(prodId: Long, nuevaCantidad: Int) {
        delegate.actualizarCantidad(prodId.toString(), nuevaCantidad)
    }

    fun removerProducto(prodId: Long) {
        delegate.removerProducto(prodId.toString())
    }

    fun limpiarCarrito() { delegate.limpiarCarrito() }

    fun cantidadTotal(): Int = delegate.cantidadTotal.value

    fun subtotal(): BigDecimal = BigDecimal.valueOf(delegate.calcularTotal())
}
