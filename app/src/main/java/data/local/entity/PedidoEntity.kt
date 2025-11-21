package cl.duoc.milsabores.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entidad Room para Pedidos almacenados localmente
 */
@Entity(tableName = "pedidos")
data class PedidoEntity(
    @PrimaryKey
    val id: String,
    val userId: String,
    val total: Double,
    val estado: String, // "PENDIENTE", "EN_PREPARACION", "LISTO", "ENTREGADO"
    val fecha: Long, // Timestamp
    val observaciones: String = "",
    val productosJson: String // JSON serializado de List<ProductoPedido>
)

/**
 * Clase auxiliar para productos dentro de un pedido
 * Se serializa a JSON para guardar en productosJson
 */
data class ProductoPedido(
    val id: String,
    val nombre: String,
    val precio: Double,
    val cantidad: Int,
    val imagen: String = ""
)

