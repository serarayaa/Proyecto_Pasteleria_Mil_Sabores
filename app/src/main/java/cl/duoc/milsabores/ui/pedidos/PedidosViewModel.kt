package cl.duoc.milsabores.ui.pedidos

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.milsabores.model.Pedido
import cl.duoc.milsabores.data.local.PedidosLocalStorageSQLite
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class PedidosUiState(
    val pedidos: List<Pedido> = emptyList(),
    val loading: Boolean = false,
    val error: String? = null,
    val pedidoSeleccionado: Pedido? = null,
    val message: String? = null
)

class PedidosViewModel(
    application: Application? = null,
    private val pedidosLocalStorage: PedidosLocalStorageSQLite? = null
) : AndroidViewModel(application ?: throw IllegalArgumentException("Application required")) {

    private val _ui = MutableStateFlow(PedidosUiState())
    val ui: StateFlow<PedidosUiState> = _ui.asStateFlow()

    private val storage = pedidosLocalStorage ?: PedidosLocalStorageSQLite(getApplication())
    private val auth = FirebaseAuth.getInstance()

    init {
        observarPedidos()
    }

    private fun observarPedidos() {
        viewModelScope.launch {
            _ui.value = _ui.value.copy(loading = true, error = null)
            try {
                val uid = auth.currentUser?.uid
                if (uid == null) {
                    // Usuario no autenticado: deja estado consistente
                    _ui.value = _ui.value.copy(pedidos = emptyList(), loading = false)
                    return@launch
                }

                // Usar Flow para observar cambios en tiempo real
                storage.cargarPedidosByUser(uid).collect { pedidos ->
                    _ui.value = _ui.value.copy(
                        pedidos = pedidos.sortedByDescending { it.fecha },
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
            _ui.value = _ui.value.copy(loading = true, message = null)
            try {
                storage.eliminarPedido(pedidoId)
                _ui.value = _ui.value.copy(
                    loading = false,
                    pedidoSeleccionado = null,
                    message = "Pedido cancelado"
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

    fun consumeMessage() {
        _ui.value = _ui.value.copy(message = null)
    }
}
