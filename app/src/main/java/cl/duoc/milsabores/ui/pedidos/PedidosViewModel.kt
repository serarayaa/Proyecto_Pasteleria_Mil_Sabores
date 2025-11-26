package cl.duoc.milsabores.ui.pedidos

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.milsabores.data.local.PedidosLocalStorageSQLite
import cl.duoc.milsabores.model.Pedido
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class PedidosUiState(
    val loading: Boolean = true,
    val pedidos: List<Pedido> = emptyList(),
    val pedidoSeleccionado: Pedido? = null,
    val message: String? = null
)

class PedidosViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val storage = PedidosLocalStorageSQLite(application)

    private val _ui = MutableStateFlow(PedidosUiState())
    val ui: StateFlow<PedidosUiState> = _ui.asStateFlow()

    // Usuario "local" (como en CarritoViewModel)
    private val uidActual = "usuario_local"

    init {
        cargarPedidos()
    }

    private fun cargarPedidos() {
        viewModelScope.launch {
            try {
                // Flow desde SQLite
                storage.cargarPedidosByUser(uidActual).collect { lista ->
                    _ui.value = _ui.value.copy(
                        loading = false,
                        pedidos = lista
                    )
                }
            } catch (e: Exception) {
                _ui.value = _ui.value.copy(
                    loading = false,
                    message = e.message ?: "Error al cargar pedidos"
                )
            }
        }
    }

    fun seleccionarPedido(pedido: Pedido) {
        _ui.value = _ui.value.copy(pedidoSeleccionado = pedido)
    }

    fun cerrarDetalle() {
        _ui.value = _ui.value.copy(pedidoSeleccionado = null)
    }

    fun cancelarPedido(pedidoId: String) {
        // Para simplificar: eliminar pedido del historial local
        viewModelScope.launch {
            try {
                storage.eliminarPedido(pedidoId)
                _ui.value = _ui.value.copy(
                    pedidos = _ui.value.pedidos.filterNot { it.id == pedidoId },
                    pedidoSeleccionado = null,
                    message = "Pedido cancelado"
                )
            } catch (e: Exception) {
                _ui.value = _ui.value.copy(
                    message = e.message ?: "Error al cancelar pedido"
                )
            }
        }
    }

    fun consumeMessage() {
        _ui.value = _ui.value.copy(message = null)
    }
}
