package cl.duoc.milsabores.ui.carrito

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.milsabores.data.local.PedidosLocalStorage
import cl.duoc.milsabores.model.CarritoItem
import cl.duoc.milsabores.model.EstadoPedido
import cl.duoc.milsabores.model.Pedido
import cl.duoc.milsabores.model.ProductoPedido
import cl.duoc.milsabores.repository.CarritoRepository
import cl.duoc.milsabores.repository.PedidosRepository
import cl.duoc.milsabores.service.PedidosObserverService
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull

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

    // Storage local para pedidos
    private val pedidosLocalStorage = PedidosLocalStorage(application)
    private val auth = FirebaseAuth.getInstance()

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
        Log.d("CarritoVM", "finalizarCompra() llamado")

        // Prevenir múltiples clics
        if (_uiState.value.procesandoPedido) {
            Log.d("CarritoVM", "Ya está procesando, ignorando clic")
            return
        }

        viewModelScope.launch {
            Log.d("CarritoVM", "Iniciando procesamiento de compra...")
            _uiState.value = _uiState.value.copy(procesandoPedido = true, error = null)

            try {
                val itemsCarrito = items.value
                Log.d("CarritoVM", "Items en carrito: ${itemsCarrito.size}")

                if (itemsCarrito.isEmpty()) {
                    Log.w("CarritoVM", "Carrito vacío")
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

                // Obtener UID del usuario autenticado
                val uid = auth.currentUser?.uid ?: "usuario_local_${System.currentTimeMillis()}"
                Log.d("CarritoVM", "UID del usuario: $uid")

                // Crear el pedido
                val pedido = Pedido(
                    id = "",
                    uid = uid,
                    fecha = System.currentTimeMillis(),
                    productos = productos,
                    total = total.value,
                    estado = EstadoPedido.PENDIENTE,
                    observaciones = _uiState.value.observaciones
                )

                Log.d("CarritoVM", "Pedido creado localmente. Total: ${pedido.total}")

                // Guardar en almacenamiento local (MUCHO MÁS RÁPIDO)
                Log.d("CarritoVM", "Guardando pedido en almacenamiento local...")

                val resultado = pedidosLocalStorage.guardarPedido(pedido)

                resultado
                    .onSuccess { pedidoId ->
                        Log.d("CarritoVM", "✅ Pedido guardado exitosamente en local: $pedidoId")
                        try {
                            // Enviar notificación
                            pedidosObserver.notificarPedidoCreado(pedidoId, pedido.total)
                        } catch (e: Exception) {
                            Log.w("CarritoVM", "Error al notificar pedido creado: ${e.message}")
                        }

                        // Limpiar carrito
                        repo.limpiarCarrito()
                        _uiState.value = _uiState.value.copy(
                            procesandoPedido = false,
                            pedidoCreado = true,
                            observaciones = ""
                        )
                    }
                    .onFailure { error ->
                        Log.e("CarritoVM", "❌ Error al guardar pedido: ${error.message}", error)
                        _uiState.value = _uiState.value.copy(
                            procesandoPedido = false,
                            error = error.message ?: "Error al crear el pedido. Intenta de nuevo."
                        )
                    }
            } catch (e: Exception) {
                Log.e("CarritoVM", "❌ Excepción: ${e.message}", e)
                _uiState.value = _uiState.value.copy(
                    procesandoPedido = false,
                    error = "Error: ${e.localizedMessage ?: e.message ?: "Error desconocido"}"
                )
            }
        }
    }

    fun resetPedidoCreado() {
        _uiState.value = _uiState.value.copy(pedidoCreado = false)
    }
}

