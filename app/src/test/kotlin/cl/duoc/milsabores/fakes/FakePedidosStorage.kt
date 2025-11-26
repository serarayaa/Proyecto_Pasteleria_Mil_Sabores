package cl.duoc.milsabores.fakes

import cl.duoc.milsabores.data.repository.pedidos.IPedidosStorage
import cl.duoc.milsabores.model.Pedido
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class FakePedidosStorage(
    initialData: List<Pedido> = emptyList()
) : IPedidosStorage {

    private val data = MutableStateFlow(initialData)

    override fun cargarPedidosByUser(uid: String): Flow<List<Pedido>> {
        // En SQLite real se filtra por UID, pero aquí lo omitimos para tests
        return data
    }

    override suspend fun eliminarPedido(pedidoId: String) {
        data.value = data.value.filterNot { it.id == pedidoId }
    }
}
