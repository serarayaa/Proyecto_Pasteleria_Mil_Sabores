package cl.duoc.milsabores.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import cl.duoc.milsabores.data.local.entity.PedidoEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO (Data Access Object) para operaciones de Pedidos en SQLite
 */
@Dao
interface PedidoDao {

    /**
     * Obtener todos los pedidos (ordenados por fecha descendente)
     * Flow = actualización automática en la UI cuando cambian los datos
     */
    @Query("SELECT * FROM pedidos ORDER BY fecha DESC")
    fun getAllPedidos(): Flow<List<PedidoEntity>>

    /**
     * Obtener pedidos de un usuario específico
     */
    @Query("SELECT * FROM pedidos WHERE userId = :userId ORDER BY fecha DESC")
    fun getPedidosByUser(userId: String): Flow<List<PedidoEntity>>

    /**
     * Obtener un pedido por ID
     */
    @Query("SELECT * FROM pedidos WHERE id = :pedidoId LIMIT 1")
    suspend fun getPedidoById(pedidoId: String): PedidoEntity?

    /**
     * Insertar un nuevo pedido
     * OnConflictStrategy.REPLACE = si ya existe, lo reemplaza
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPedido(pedido: PedidoEntity)

    /**
     * Insertar múltiples pedidos
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(pedidos: List<PedidoEntity>)

    /**
     * Actualizar un pedido existente
     */
    @Update
    suspend fun updatePedido(pedido: PedidoEntity)

    /**
     * Actualizar solo el estado de un pedido
     */
    @Query("UPDATE pedidos SET estado = :nuevoEstado WHERE id = :pedidoId")
    suspend fun updateEstado(pedidoId: String, nuevoEstado: String)

    /**
     * Eliminar un pedido específico
     */
    @Delete
    suspend fun deletePedido(pedido: PedidoEntity)

    /**
     * Eliminar todos los pedidos de un usuario
     */
    @Query("DELETE FROM pedidos WHERE userId = :userId")
    suspend fun deleteAllByUser(userId: String)

    /**
     * Eliminar TODOS los pedidos (útil para logout)
     */
    @Query("DELETE FROM pedidos")
    suspend fun deleteAll()
}

