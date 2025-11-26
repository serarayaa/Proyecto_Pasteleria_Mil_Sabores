package cl.duoc.milsabores.ui.carrito

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.milsabores.data.local.PedidosLocalStorageSQLite
import cl.duoc.milsabores.model.CarritoItem
import cl.duoc.milsabores.model.EstadoPedido
import cl.duoc.milsabores.model.Pedido
import cl.duoc.milsabores.model.ProductoPedido
import cl.duoc.milsabores.repository.CarritoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class CarritoUiState(
    val procesandoPedido: Boolean = false,
    val pedidoCreado: Boolean = false,
    val error: String? = null,
    val observaciones: String = ""
)

class CarritoViewModel(
    application: Application,
    private val repo: CarritoRepository = CarritoRepository.getInstance()
) : AndroidViewModel(application) {

    private val pedidosLocalStorage = PedidosLocalStorageSQLite(application)

    // Items del carrito expuestos a la UI
    val items: StateFlow<List<CarritoItem>> = repo.items
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Total del carrito
    val total: StateFlow<Double> = repo.items
        .map { list -> list.sumOf { it.subtotal } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    private val _uiState = MutableStateFlow(CarritoUiState())
    val uiState: StateFlow<CarritoUiState> = _uiState.asStateFlow()

    // ======================
    // Acciones sobre items
    // ======================

    fun actualizarCantidad(productoId: String, cantidad: Int) {
        repo.actualizarCantidad(productoId, cantidad)
    }

    fun agregarProducto(productoId: String) {
        val item = items.value.find { it.productoId == productoId }
        item?.let { actualizarCantidad(productoId, it.cantidad + 1) }
    }

    fun decrementarProducto(productoId: String) {
        val item = items.value.find { it.productoId == productoId }
        item?.let {
            if (it.cantidad > 1) {
                actualizarCantidad(productoId, it.cantidad - 1)
            } else {
                removerProducto(productoId)
            }
        }
    }

    fun removerProducto(productoId: String) = repo.removerProducto(productoId)

    fun limpiarCarrito() {
        repo.limpiarCarrito()
        _uiState.value = CarritoUiState()
    }

    fun vaciarCarrito() = limpiarCarrito()

    fun setObservaciones(obs: String) {
        _uiState.value = _uiState.value.copy(observaciones = obs)
    }

    // ======================
    // Finalizar compra
    // ======================

    fun finalizarCompra() {
        if (_uiState.value.procesandoPedido) return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                procesandoPedido = true,
                error = null
            )

            try {
                val itemsCarrito = items.value
                if (itemsCarrito.isEmpty()) {
                    _uiState.value = _uiState.value.copy(
                        procesandoPedido = false,
                        error = "El carrito está vacío"
                    )
                    return@launch
                }

                val productos = itemsCarrito.map { it ->
                    ProductoPedido(
                        nombre = it.nombre,
                        cantidad = it.cantidad,
                        precio = it.precio
                    )
                }

                // Usuario local (ya NO usamos FirebaseAuth aquí)
                val uid = "usuario_local"

                val pedido = Pedido(
                    id = "",
                    uid = uid,
                    fecha = System.currentTimeMillis(),
                    productos = productos,
                    total = total.value,
                    estado = EstadoPedido.PENDIENTE,
                    observaciones = _uiState.value.observaciones
                )

                // Guardar pedido en SQLite
                pedidosLocalStorage.guardarPedido(pedido)

                // Limpiar carrito
                repo.limpiarCarrito()

                // Actualizar estado de UI
                _uiState.value = _uiState.value.copy(
                    procesandoPedido = false,
                    pedidoCreado = true,
                    observaciones = ""
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    procesandoPedido = false,
                    error = e.message ?: "Error al procesar el pedido"
                )
            }
        }
    }

    fun resetPedidoCreado() {
        _uiState.value = _uiState.value.copy(pedidoCreado = false)
    }
}
