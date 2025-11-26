package cl.duoc.milsabores.ui.principal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.milsabores.data.repository.ProductRepository
import cl.duoc.milsabores.model.CarritoItem
import cl.duoc.milsabores.repository.CarritoRepository
import cl.duoc.milsabores.ui.model.Producto
import cl.duoc.milsabores.ui.model.productosDemo
import cl.duoc.milsabores.ui.model.toUiModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

data class PrincipalUiState(
    val email: String? = null,
    val loading: Boolean = false,
    val error: String? = null,
    val loggedOut: Boolean = false
)

class PrincipalViewModel(
    private val carritoRepo: CarritoRepository = CarritoRepository.getInstance(),
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val productRepo: ProductRepository = ProductRepository()
) : ViewModel() {

    private val _ui = MutableStateFlow(PrincipalUiState(email = auth.currentUser?.email))
    val ui: StateFlow<PrincipalUiState> = _ui.asStateFlow()

    // Productos y filtros
    private val _productos = MutableStateFlow<List<Producto>>(emptyList())
    val productos: StateFlow<List<Producto>> = _productos.asStateFlow()

    private val _categoriaSel = MutableStateFlow<String?>(null)
    val categoriaSel: StateFlow<String?> = _categoriaSel.asStateFlow()

    val productosFiltrados: StateFlow<List<Producto>> =
        combine(_productos, _categoriaSel) { lista, cat ->
            if (cat.isNullOrBlank()) lista
            else lista.filter { it.categoria.equals(cat, ignoreCase = true) }
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
            try {
                // Intentamos primero desde el microservicio
                val dtos = productRepo.obtenerProductosDisponibles()
                val listaUi = dtos.map { it.toUiModel() }
                _productos.value = listaUi
                _ui.value = _ui.value.copy(loading = false)
            } catch (io: IOException) {
                // Problemas de red → usamos respaldo local
                _productos.value = productosDemo
                _ui.value = _ui.value.copy(
                    loading = false,
                    error = "No se pudo conectar al catálogo en línea. Se muestran productos de ejemplo."
                )
            } catch (he: HttpException) {
                // Error del backend → usamos respaldo local
                _productos.value = productosDemo
                _ui.value = _ui.value.copy(
                    loading = false,
                    error = "Error del servidor al cargar productos. Se muestran productos de ejemplo."
                )
            }
        }
    }

    fun setCategoria(cat: String?) {
        _categoriaSel.value = cat
    }

    fun refreshHome() {
        cargarProductos()
    }

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