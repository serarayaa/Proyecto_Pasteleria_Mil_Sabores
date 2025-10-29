package cl.duoc.milsabores.ui.principal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.milsabores.model.CarritoItem
import cl.duoc.milsabores.repository.CarritoRepository
import cl.duoc.milsabores.ui.model.Producto
import cl.duoc.milsabores.ui.model.productosDemo
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class PrincipalUiState(
    val email: String? = null,
    val loading: Boolean = false,
    val error: String? = null,
    val loggedOut: Boolean = false
)

class PrincipalViewModel(
    private val carritoRepo: CarritoRepository = CarritoRepository.getInstance(),
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) : ViewModel() {

    private val _ui = MutableStateFlow(PrincipalUiState(email = auth.currentUser?.email))
    val ui: StateFlow<PrincipalUiState> = _ui.asStateFlow()

    // Productos y filtros
    private val _productos = MutableStateFlow<List<Producto>>(emptyList())
    val productos: StateFlow<List<Producto>> = _productos.asStateFlow()

    private val _categoriaSel = MutableStateFlow<String?>(null)
    val categoriaSel: StateFlow<String?> = _categoriaSel.asStateFlow()

    val productosFiltrados: StateFlow<List<Producto>> = combine(_productos, _categoriaSel) { lista, cat ->
        if (cat.isNullOrBlank()) lista else lista.filter { it.categoria.equals(cat, ignoreCase = true) }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val categorias: List<String>
        get() = _productos.value.map { it.categoria }.distinct()

    // Carrito
    val cantidadCarrito: StateFlow<Int> = carritoRepo.cantidadTotal
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    init {
        cargarProductos()
    }

    fun cargarProductos() {
        _ui.value = _ui.value.copy(loading = true, error = null)
        viewModelScope.launch {
            // En este proyecto usamos productosDemo locales
            _productos.value = productosDemo
            _ui.value = _ui.value.copy(loading = false)
        }
    }

    fun setCategoria(cat: String?) { _categoriaSel.value = cat }

    fun refreshHome() { cargarProductos() }

    fun agregarAlCarrito(p: Producto) {
        val precioDouble = p.precio
            .replace("$", "")
            .replace(".", "")
            .replace(",", ".")
            .trim()
            .toDoubleOrNull() ?: 0.0
        val item = CarritoItem(
            productoId = p.id.toString(),
            nombre = p.titulo,
            precio = precioDouble,
            imagen = p.imagenRes.toString(),
            cantidad = 1
        )
        carritoRepo.agregarProducto(item)
    }

    fun logout() {
        viewModelScope.launch {
            runCatching { auth.signOut() }
            _ui.value = _ui.value.copy(loggedOut = true)
        }
    }
}
