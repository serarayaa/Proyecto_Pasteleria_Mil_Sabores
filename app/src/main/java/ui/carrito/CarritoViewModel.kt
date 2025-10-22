package cl.duoc.milsabores.ui.carrito

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.milsabores.model.CarritoItem
import cl.duoc.milsabores.model.EstadoPedido
import cl.duoc.milsabores.model.Pedido
import cl.duoc.milsabores.model.ProductoPedido
import cl.duoc.milsabores.repository.CarritoRepository
import cl.duoc.milsabores.repository.PedidosRepository
import cl.duoc.milsabores.service.PedidosObserverService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
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
    private val repo: CarritoRepository = CarritoRepository.getInstance(),
    private val pedidosRepo: PedidosRepository = PedidosRepository()
) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(CarritoUiState())
    val uiState: StateFlow<CarritoUiState> = _uiState.asStateFlow()

    private val pedidosObserver = PedidosObserverService.getInstance(application)

    val items: StateFlow<List<CarritoItem>> = repo.items.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    val total: StateFlow<Double> = MutableStateFlow(0.0).apply {
        viewModelScope.launch {
            repo.items.collect { items ->
                value = items.sumOf { it.subtotal }
            }
        }
    }

    fun actualizarCantidad(productoId: String, cantidad: Int) {
        repo.actualizarCantidad(productoId, cantidad)
    }

    fun agregarProducto(productoId: String) {
        val item = items.value.find { it.productoId == productoId }
        item?.let {
            actualizarCantidad(productoId, it.cantidad + 1)
        }
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

    fun removerProducto(productoId: String) {
        repo.removerProducto(productoId)
    }

    fun limpiarCarrito() {
        repo.limpiarCarrito()
        _uiState.value = CarritoUiState()
    }

    fun vaciarCarrito() {
        limpiarCarrito()
    }

    fun setObservaciones(obs: String) {
        _uiState.value = _uiState.value.copy(observaciones = obs)
    }

    fun finalizarCompra() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(procesandoPedido = true, error = null)

            try {
                val itemsCarrito = items.value
                if (itemsCarrito.isEmpty()) {
                    _uiState.value = _uiState.value.copy(
                        procesandoPedido = false,
                        error = "El carrito está vacío"
                    )
                    return@launch
                }

                // Convertir items del carrito a productos del pedido
                val productos = itemsCarrito.map { item ->
                    ProductoPedido(
                        nombre = item.nombre,
                        cantidad = item.cantidad,
                        precio = item.precio
                    )
                }

                // Crear el pedido
                val pedido = Pedido(
                    fecha = System.currentTimeMillis(),
                    productos = productos,
                    total = total.value,
                    estado = EstadoPedido.PENDIENTE,
                    observaciones = _uiState.value.observaciones
                )

                // Guardar en Firebase
                pedidosRepo.crearPedido(pedido)
                    .onSuccess { pedidoId ->
                        // Enviar notificación
                        pedidosObserver.notificarPedidoCreado(pedidoId, pedido.total)

                        // Limpiar carrito
                        repo.limpiarCarrito()
                        _uiState.value = _uiState.value.copy(
                            procesandoPedido = false,
                            pedidoCreado = true,
                            observaciones = ""
                        )
                    }
                    .onFailure { error ->
                        _uiState.value = _uiState.value.copy(
                            procesandoPedido = false,
                            error = error.message ?: "Error al crear el pedido"
                        )
                    }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    procesandoPedido = false,
                    error = e.message ?: "Error desconocido"
                )
            }
        }
    }

    fun resetPedidoCreado() {
        _uiState.value = _uiState.value.copy(pedidoCreado = false)
    }
}

