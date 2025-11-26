package cl.duoc.milsabores.ui.pedidos

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.milsabores.data.repository.auth.IAuthProvider
import cl.duoc.milsabores.data.repository.pedidos.IPedidosStorage
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

/**
 * ViewModel preparado tanto para ejecución real en Android como
 * para pruebas unitarias en JVM (a través de inyección de dependencias).
 */
class PedidosViewModel(
    application: Application,
    private val storage: IPedidosStorage = PedidosLocalStorageSQLite(application),
    private val authProvider: IAuthProvider = object : IAuthProvider {
        override val currentUid: String? =
            FirebaseAuth.getInstance().currentUser?.uid
    }
) : AndroidViewModel(application) {

    private val _ui = MutableStateFlow(PedidosUiState())
    val ui: StateFlow<PedidosUiState> = _ui.asStateFlow()

    init {
        observarPedidos()
    }

    private fun observarPedidos() {
        viewModelScope.launch {
            _ui.value = _ui.value.copy(loading = true, error = null)

            try {
                val uid = authProvider.currentUid

                if (uid == null) {
                    _ui.value = _ui.value.copy(
                        pedidos = emptyList(),
                        loading = false
                    )
                    return@launch
                }

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

                observarPedidos()

            } catch (e: Exception) {
                _ui.value = _ui.value.copy(
                    loading = false,
                    error = e.message ?: "Error al cancelar pedido"
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
