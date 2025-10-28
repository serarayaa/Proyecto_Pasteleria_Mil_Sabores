package cl.duoc.milsabores.ui.pedidos

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.milsabores.data.local.PedidosLocalStorage
import cl.duoc.milsabores.model.EstadoPedido
import cl.duoc.milsabores.model.Pedido
import com.google.firebase.auth.FirebaseAuth
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
    application: Application? = null,
    private val pedidosLocalStorage: PedidosLocalStorage? = null
) : AndroidViewModel(application ?: throw IllegalArgumentException("Application required")) {

    private val _ui = MutableStateFlow(PedidosUiState())
    val ui: StateFlow<PedidosUiState> = _ui.asStateFlow()

    private val storage = pedidosLocalStorage ?: PedidosLocalStorage(getApplication())
    private val auth = FirebaseAuth.getInstance()

    init {
        observarPedidos()
    }

    private fun observarPedidos() {
        viewModelScope.launch {
            _ui.value = _ui.value.copy(loading = true)
            try {
                // Obtener UID del usuario actual
                val uid = auth.currentUser?.uid ?: return@launch

                // Obtener pedidos locales del usuario
                val pedidos = storage.obtenerPedidosUsuario(uid)

                _ui.value = _ui.value.copy(
                    pedidos = pedidos.sortedByDescending { it.fecha },
                    loading = false,
                    error = null
                )
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
            try {
                storage.eliminarPedido(pedidoId)
                _ui.value = _ui.value.copy(
                    loading = false,
                    pedidoSeleccionado = null
                )
                // Recargar pedidos
                observarPedidos()
            } catch (error: Exception) {
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

