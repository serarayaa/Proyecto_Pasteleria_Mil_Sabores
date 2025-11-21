package cl.duoc.milsabores.data.local

import android.content.Context
import cl.duoc.milsabores.core.AppLogger
import cl.duoc.milsabores.data.local.entity.PedidoEntity
import cl.duoc.milsabores.data.local.entity.ProductoPedido
import cl.duoc.milsabores.model.Pedido
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * 🆕 NUEVO: Gestión de almacenamiento local de pedidos usando SQLite (Room)
 * Reemplaza el sistema anterior de SharedPreferences + JSON
 *
 * UBICACIÓN DE LA BASE DE DATOS:
 * /data/data/cl.duoc.milsabores/databases/mil_sabores_database.db
 */
class PedidosLocalStorageSQLite(context: Context) {

    private val database = AppDatabase.getDatabase(context)
    private val pedidoDao = database.pedidoDao()
    private val gson = Gson()

    /**
     * Guardar un pedido en SQLite
     */
    suspend fun guardarPedido(pedido: Pedido) {
        try {
            val entity = pedido.toEntity()
            pedidoDao.insertPedido(entity)
            AppLogger.i("✅ Pedido guardado en SQLite: ${pedido.id}", "SQLite")
        } catch (e: Exception) {
            AppLogger.e("❌ Error al guardar pedido en SQLite", e, "SQLite")
        }
    }

    /**
     * Guardar múltiples pedidos en SQLite
     */
    suspend fun guardarPedidos(pedidos: List<Pedido>) {
        try {
            val entities = pedidos.map { it.toEntity() }
            pedidoDao.insertAll(entities)
            AppLogger.i("✅ ${pedidos.size} pedidos guardados en SQLite", "SQLite")
        } catch (e: Exception) {
            AppLogger.e("❌ Error al guardar pedidos en SQLite", e, "SQLite")
        }
    }

    /**
     * Cargar todos los pedidos desde SQLite
     * Flow = actualización automática en la UI cuando cambian los datos
     */
    fun cargarPedidos(): Flow<List<Pedido>> {
        return pedidoDao.getAllPedidos().map { entities ->
            entities.map { it.toPedido() }
        }
    }

    /**
     * Cargar pedidos de un usuario específico
     */
    fun cargarPedidosByUser(userId: String): Flow<List<Pedido>> {
        return pedidoDao.getPedidosByUser(userId).map { entities ->
            entities.map { it.toPedido() }
        }
    }

    /**
     * Obtener un pedido específico por ID
     */
    suspend fun obtenerPedido(pedidoId: String): Pedido? {
        return try {
            pedidoDao.getPedidoById(pedidoId)?.toPedido()
        } catch (e: Exception) {
            AppLogger.e("Error al obtener pedido $pedidoId", e, "SQLite")
            null
        }
    }

    /**
     * Actualizar el estado de un pedido
     */
    suspend fun actualizarEstado(pedidoId: String, nuevoEstado: String) {
        try {
            pedidoDao.updateEstado(pedidoId, nuevoEstado)
            AppLogger.i("Estado de pedido $pedidoId → $nuevoEstado", "SQLite")
        } catch (e: Exception) {
            AppLogger.e("Error al actualizar estado de pedido", e, "SQLite")
        }
    }

    /**
     * Eliminar un pedido específico por ID
     */
    suspend fun eliminarPedido(pedidoId: String) {
        try {
            val entity = pedidoDao.getPedidoById(pedidoId)
            if (entity != null) {
                pedidoDao.deletePedido(entity)
                AppLogger.i("🗑️ Pedido $pedidoId eliminado de SQLite", "SQLite")
            }
        } catch (e: Exception) {
            AppLogger.e("Error al eliminar pedido", e, "SQLite")
        }
    }

    /**
     * Limpiar todos los pedidos de la base de datos
     */
    suspend fun limpiar() {
        try {
            pedidoDao.deleteAll()
            AppLogger.i("🗑️ Todos los pedidos eliminados de SQLite", "SQLite")
        } catch (e: Exception) {
            AppLogger.e("Error al limpiar pedidos", e, "SQLite")
        }
    }

    /**
     * Limpiar pedidos de un usuario específico
     */
    suspend fun limpiarPorUsuario(userId: String) {
        try {
            pedidoDao.deleteAllByUser(userId)
            AppLogger.i("Pedidos del usuario $userId eliminados", "SQLite")
        } catch (e: Exception) {
            AppLogger.e("Error al limpiar pedidos del usuario", e, "SQLite")
        }
    }

    // ========== MAPPERS (Conversión entre Pedido y PedidoEntity) ==========

    /**
     * Convertir Pedido (modelo) a PedidoEntity (tabla SQLite)
     */
    private fun Pedido.toEntity(): PedidoEntity {
        val productosJson = gson.toJson(this.productos.map { producto ->
            ProductoPedido(
                id = "", // No se usa en el modelo real
                nombre = producto.nombre,
                precio = producto.precio,
                cantidad = producto.cantidad,
                imagen = "" // No se usa en el modelo real
            )
        })

        return PedidoEntity(
            id = this.id,
            userId = this.uid,
            total = this.total,
            estado = this.estado.name, // Enum a String
            fecha = this.fecha,
            observaciones = this.observaciones,
            productosJson = productosJson
        )
    }

    /**
     * Convertir PedidoEntity (tabla SQLite) a Pedido (modelo)
     */
    private fun PedidoEntity.toPedido(): Pedido {
        val type = object : TypeToken<List<ProductoPedido>>() {}.type
        val productos: List<ProductoPedido> = try {
            gson.fromJson(this.productosJson, type)
        } catch (e: Exception) {
            AppLogger.e("Error al parsear productos del pedido ${this.id}", e, "SQLite")
            emptyList()
        }

        return Pedido(
            id = this.id,
            uid = this.userId,
            productos = productos.map { prod ->
                cl.duoc.milsabores.model.ProductoPedido(
                    nombre = prod.nombre,
                    precio = prod.precio,
                    cantidad = prod.cantidad
                )
            },
            total = this.total,
            estado = try {
                cl.duoc.milsabores.model.EstadoPedido.valueOf(this.estado)
            } catch (e: Exception) {
                cl.duoc.milsabores.model.EstadoPedido.PENDIENTE
            },
            fecha = this.fecha,
            observaciones = this.observaciones
        )
    }
}

