package cl.duoc.milsabores.repository

import cl.duoc.milsabores.model.CarritoItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CarritoRepository {
    private val _items = MutableStateFlow<List<CarritoItem>>(emptyList())
    val items: StateFlow<List<CarritoItem>> = _items.asStateFlow()

    private val _cantidadTotal = MutableStateFlow(0)
    val cantidadTotal: StateFlow<Int> = _cantidadTotal.asStateFlow()

    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    init {
        scope.launch {
            _items.collect { itemsList ->
                _cantidadTotal.value = itemsList.sumOf { it.cantidad }
            }
        }
    }

    fun agregarProducto(item: CarritoItem) {
        _items.update { currentList ->
            val existing = currentList.find { it.productoId == item.productoId }
            if (existing != null) {
                currentList.map {
                    if (it.productoId == item.productoId)
                        it.copy(cantidad = it.cantidad + 1)
                    else it
                }
            } else {
                currentList + item
            }
        }
    }

    fun removerProducto(productoId: String) {
        _items.update { it.filter { item -> item.productoId != productoId } }
    }

    fun actualizarCantidad(productoId: String, nuevaCantidad: Int) {
        if (nuevaCantidad <= 0) {
            removerProducto(productoId)
            return
        }
        _items.update { currentList ->
            currentList.map {
                if (it.productoId == productoId) it.copy(cantidad = nuevaCantidad)
                else it
            }
        }
    }

    fun limpiarCarrito() {
        _items.value = emptyList()
    }

    fun calcularTotal(): Double {
        return _items.value.sumOf { it.subtotal }
    }

    companion object {
        @Volatile
        private var INSTANCE: CarritoRepository? = null

        fun getInstance(): CarritoRepository {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: CarritoRepository().also { INSTANCE = it }
            }
        }
    }
}

