package cl.duoc.milsabores.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import cl.duoc.milsabores.data.local.entity.CarritoItemEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO (Data Access Object) para operaciones del Carrito en SQLite
 */
@Dao
interface CarritoDao {

    /**
     * Obtener todos los items del carrito
     * Flow = actualización automática en la UI
     */
    @Query("SELECT * FROM carrito")
    fun getAllItems(): Flow<List<CarritoItemEntity>>

    /**
     * Obtener un item específico por productoId
     */
    @Query("SELECT * FROM carrito WHERE productoId = :productoId LIMIT 1")
    suspend fun getItemByProductoId(productoId: String): CarritoItemEntity?

    /**
     * Insertar un item al carrito
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(item: CarritoItemEntity)

    /**
     * Actualizar un item existente
     */
    @Update
    suspend fun updateItem(item: CarritoItemEntity)

    /**
     * Actualizar solo la cantidad de un item
     */
    @Query("UPDATE carrito SET cantidad = :cantidad WHERE productoId = :productoId")
    suspend fun updateCantidad(productoId: String, cantidad: Int)

    /**
     * Eliminar un item específico
     */
    @Delete
    suspend fun deleteItem(item: CarritoItemEntity)

    /**
     * Eliminar un item por productoId
     */
    @Query("DELETE FROM carrito WHERE productoId = :productoId")
    suspend fun deleteByProductoId(productoId: String)

    /**
     * Vaciar todo el carrito
     */
    @Query("DELETE FROM carrito")
    suspend fun deleteAll()

    /**
     * Obtener el total de items en el carrito
     */
    @Query("SELECT COUNT(*) FROM carrito")
    fun getItemCount(): Flow<Int>

    /**
     * Calcular el total del carrito
     */
    @Query("SELECT SUM(precio * cantidad) FROM carrito")
    fun getTotal(): Flow<Double?>
}

