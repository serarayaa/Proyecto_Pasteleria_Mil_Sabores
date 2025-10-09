package cl.duoc.milsabores.ui.principal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.milsabores.repository.auth.AuthRepository
import cl.duoc.milsabores.ui.model.Producto
import cl.duoc.milsabores.ui.model.productosDemo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class PrincipalUiState(
    val email: String? = null,
    val loading: Boolean = false,
    val error: String? = null,
    val loggedOut: Boolean = false
)

class PrincipalViewModel(
    private val repo: AuthRepository = AuthRepository()
) : ViewModel() {

    private val _ui = MutableStateFlow(PrincipalUiState())
    val ui: StateFlow<PrincipalUiState> = _ui

    init {
        val user = repo.currentUser()
        _ui.update { it.copy(email = user?.email) }
    }

    fun logout() {
        viewModelScope.launch {
            _ui.update { it.copy(loading = true, error = null) }
            try {
                repo.signOut()  // <- usa tu método del repositorio
                _ui.update { it.copy(loading = false, loggedOut = true) }
            } catch (e: Exception) {
                _ui.update { it.copy(loading = false, error = e.message ?: "Error al cerrar sesión") }
            }
        }
    }

    private val _categoriaSel = MutableStateFlow("Todos")
    val categoriaSel: StateFlow<String> = _categoriaSel

    val categorias: List<String> = listOf("Todos") + productosDemo.map { it.categoria }.distinct()

    val productosFiltrados: StateFlow<List<Producto>> =
        _categoriaSel
            .map { cat -> if (cat == "Todos") productosDemo else productosDemo.filter { it.categoria == cat } }
            .stateIn(viewModelScope, SharingStarted.Eagerly, productosDemo)

    fun setCategoria(cat: String) {
        _categoriaSel.value = cat
    }
}
