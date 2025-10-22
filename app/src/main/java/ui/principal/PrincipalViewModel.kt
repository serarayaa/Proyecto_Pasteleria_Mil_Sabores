package cl.duoc.milsabores.ui.principal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.milsabores.model.CarritoItem
import cl.duoc.milsabores.repository.CarritoRepository
import cl.duoc.milsabores.repository.auth.AuthRepository
import cl.duoc.milsabores.ui.model.Producto
import cl.duoc.milsabores.ui.model.productosDemo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class PrincipalUiState(
    val email: String? = null,
    val loading: Boolean = false,
    val error: String? = null,
    val loggedOut: Boolean = false
)

class PrincipalViewModel(
    private val repo: AuthRepository = AuthRepository(),
    private val carritoRepo: CarritoRepository = CarritoRepository.getInstance()
) : ViewModel() {

    // Estado general
    private val _ui = MutableStateFlow(
        PrincipalUiState(email = repo.currentUser()?.email)
    )
    val ui: StateFlow<PrincipalUiState> = _ui.asStateFlow()

    // Carrito
    val cantidadCarrito: StateFlow<Int> = carritoRepo.cantidadTotal.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        0
    )

    // Fuente y filtros
    private val fuente: List<Producto> = productosDemo

    val categorias: List<String> = listOf("Todos") + fuente.map { it.categoria }.distinct()

    private val _categoriaSel = MutableStateFlow("Todos")
    val categoriaSel: StateFlow<String> = _categoriaSel.asStateFlow()

    private val _productosFiltrados = MutableStateFlow<List<Producto>>(emptyList())
    val productosFiltrados: StateFlow<List<Producto>> = _productosFiltrados.asStateFlow()

    init {
        cargarProductos()
    }

    fun setCategoria(cat: String) {
        _categoriaSel.value = cat
        aplicarFiltro()
    }

    fun cargarProductos() {
        viewModelScope.launch {
            _ui.value = _ui.value.copy(loading = true, error = null)
            try {
                aplicarFiltro()
            } catch (e: Exception) {
                _ui.value = _ui.value.copy(error = e.message ?: "Error al cargar productos")
            } finally {
                _ui.value = _ui.value.copy(loading = false)
            }
        }
    }

    fun refreshHome() {
        _categoriaSel.value = "Todos"
        cargarProductos()
    }

    fun logout() {
        _ui.value = _ui.value.copy(loading = true)
        viewModelScope.launch {
            try {
                repo.signOut()
                _ui.value = _ui.value.copy(loading = false, loggedOut = true)
            } catch (e: Exception) {
                _ui.value = _ui.value.copy(loading = false, error = e.message ?: "Error al cerrar sesión")
            }
        }
    }

    fun agregarAlCarrito(producto: Producto) {
        val precioDouble = producto.precio
            .replace("$", "")
            .replace(".", "")
            .replace(",", "")
            .toDoubleOrNull() ?: 0.0

        val item = CarritoItem(
            productoId = producto.id.toString(),
            nombre = producto.titulo,
            precio = precioDouble,
            imagen = ""
        )
        carritoRepo.agregarProducto(item)
    }

    private fun aplicarFiltro() {
        val cat = _categoriaSel.value
        _productosFiltrados.value = if (cat == "Todos") fuente else fuente.filter { it.categoria == cat }
    }
}

