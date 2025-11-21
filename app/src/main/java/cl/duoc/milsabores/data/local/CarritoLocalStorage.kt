package cl.duoc.milsabores.data.local

import android.content.Context
import cl.duoc.milsabores.core.AppLogger
import cl.duoc.milsabores.data.local.entity.CarritoItemEntity
import cl.duoc.milsabores.model.CarritoItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * 🆕 NUEVO: Gestión del Carrito usando SQLite (Room)
 * Reemplaza el sistema de Singleton en memoria
 *
 * VENTAJAS:
 * - El carrito persiste aunque cierres la app
 * - No se pierde si la app se fuerza a cerrar
 * - Actualización automática en la UI con Flow
 */
class CarritoLocalStorage(context: Context) {

    private val database = AppDatabase.getDatabase(context)
    private val carritoDao = database.carritoDao()

    /**
     * Obtener todos los items del carrito (Flow para actualización automática)
     */
    fun obtenerItems(): Flow<List<CarritoItem>> {
        return carritoDao.getAllItems().map { entities ->
            entities.map { it.toCarritoItem() }
        }
    }

    /**
     * Agregar un producto al carrito
     */
    suspend fun agregarProducto(producto: CarritoItem) {
        try {
            // Verificar si ya existe en el carrito
            val existente = carritoDao.getItemByProductoId(producto.productoId)

            if (existente != null) {
                // Si ya existe, aumentar cantidad
                val nuevaCantidad = existente.cantidad + producto.cantidad
                carritoDao.updateCantidad(producto.productoId, nuevaCantidad)
                AppLogger.i("Cantidad actualizada: ${producto.nombre} x$nuevaCantidad", "CarritoSQLite")
            } else {
                // Si no existe, insertar nuevo
                carritoDao.insertItem(producto.toEntity())
                AppLogger.i("Producto agregado: ${producto.nombre}", "CarritoSQLite")
            }
        } catch (e: Exception) {
            AppLogger.e("Error al agregar producto al carrito", e, "CarritoSQLite")
        }
    }

    /**
     * Actualizar la cantidad de un producto
     */
    suspend fun actualizarCantidad(productoId: String, cantidad: Int) {
        try {
            if (cantidad <= 0) {
                eliminarProducto(productoId)
            } else {
                carritoDao.updateCantidad(productoId, cantidad)
                AppLogger.i("Cantidad actualizada: $productoId → $cantidad", "CarritoSQLite")
            }
        } catch (e: Exception) {
            AppLogger.e("Error al actualizar cantidad", e, "CarritoSQLite")
        }
    }

    /**
     * Eliminar un producto del carrito
     */
    suspend fun eliminarProducto(productoId: String) {
        try {
            carritoDao.deleteByProductoId(productoId)
            AppLogger.i("Producto eliminado: $productoId", "CarritoSQLite")
        } catch (e: Exception) {
            AppLogger.e("Error al eliminar producto", e, "CarritoSQLite")
        }
    }

    /**
     * Vaciar todo el carrito
     */
    suspend fun vaciarCarrito() {
        try {
            carritoDao.deleteAll()
            AppLogger.i("🗑️ Carrito vaciado", "CarritoSQLite")
        } catch (e: Exception) {
            AppLogger.e("Error al vaciar carrito", e, "CarritoSQLite")
        }
    }

    /**
     * Obtener el total del carrito (Flow)
     */
    fun obtenerTotal(): Flow<Double> {
        return carritoDao.getTotal().map { it ?: 0.0 }
    }

    /**
     * Obtener cantidad de items en el carrito (Flow)
     */
    fun obtenerCantidadItems(): Flow<Int> {
        return carritoDao.getItemCount()
    }

    // ========== MAPPERS ==========

    private fun CarritoItemEntity.toCarritoItem(): CarritoItem {
        return CarritoItem(
            productoId = this.productoId,
            nombre = this.nombre,
            precio = this.precio,
            cantidad = this.cantidad,
            imagen = this.imagen
        )
    }

    private fun CarritoItem.toEntity(): CarritoItemEntity {
        return CarritoItemEntity(
            // id se autogenera en la entidad
            productoId = this.productoId,
            nombre = this.nombre,
            precio = this.precio,
            cantidad = this.cantidad,
            imagen = this.imagen
        )
    }
}

