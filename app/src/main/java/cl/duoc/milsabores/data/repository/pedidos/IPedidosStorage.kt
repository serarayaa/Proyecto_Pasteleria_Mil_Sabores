package cl.duoc.milsabores.data.repository.pedidos

import cl.duoc.milsabores.model.Pedido
import kotlinx.coroutines.flow.Flow

interface IPedidosStorage {
    fun cargarPedidosByUser(uid: String): Flow<List<Pedido>>
    suspend fun eliminarPedido(pedidoId: String)
}
