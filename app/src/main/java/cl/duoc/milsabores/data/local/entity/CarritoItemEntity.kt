package cl.duoc.milsabores.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entidad Room para items del carrito almacenados localmente
 */
@Entity(tableName = "carrito")
data class CarritoItemEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val productoId: String,
    val nombre: String,
    val precio: Double,
    val cantidad: Int,
    val imagen: String = ""
)

