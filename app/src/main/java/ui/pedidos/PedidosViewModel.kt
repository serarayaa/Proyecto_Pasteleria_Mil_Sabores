package cl.duoc.milsabores.ui.pedidos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.milsabores.model.EstadoPedido
import cl.duoc.milsabores.model.Pedido
import cl.duoc.milsabores.repository.PedidosRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class PedidosUiState(
    val pedidos: List<Pedido> = emptyList(),
    val loading: Boolean = false,
    val error: String? = null,
    val pedidoSeleccionado: Pedido? = null
)

class PedidosViewModel(
    private val repository: PedidosRepository = PedidosRepository()
) : ViewModel() {
    private val _ui = MutableStateFlow(PedidosUiState())
    val ui: StateFlow<PedidosUiState> = _ui.asStateFlow()

    init {
        observarPedidos()
    }

    private fun observarPedidos() {
        viewModelScope.launch {
            _ui.value = _ui.value.copy(loading = true)
            try {
                repository.observarPedidosUsuario().collect { pedidos ->
                    _ui.value = _ui.value.copy(
                        pedidos = pedidos,
                        loading = false,
                        error = null
                    )
                }
            } catch (e: Exception) {
                _ui.value = _ui.value.copy(
                    loading = false,
                    error = e.message ?: "Error al cargar pedidos"
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
        viewModelScope.launch {
            _ui.value = _ui.value.copy(loading = true)
            repository.cancelarPedido(pedidoId)
                .onSuccess {
                    _ui.value = _ui.value.copy(
                        loading = false,
                        pedidoSeleccionado = null
                    )
                }
                .onFailure { error ->
                    _ui.value = _ui.value.copy(
                        loading = false,
                        error = error.message ?: "Error al cancelar pedido"
                    )
                }
        }
    }

    fun recargarPedidos() {
        observarPedidos()
    }
}

