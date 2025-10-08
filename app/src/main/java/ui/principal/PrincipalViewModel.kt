package cl.duoc.milsabores.ui.principal

import androidx.lifecycle.ViewModel
import cl.duoc.milsabores.repository.auth.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

data class PrincipalUiState(
    val userEmail: String = "usuario"
)

class PrincipalViewModel(
    private val repo: AuthRepository = AuthRepository()
) : ViewModel() {

    private val _ui = MutableStateFlow(PrincipalUiState())
    val ui: StateFlow<PrincipalUiState> = _ui

    init {
        val email = repo.currentUser()?.email ?: "usuario"
        _ui.update { it.copy(userEmail = email) }
    }

    fun logout() = repo.signOut()
}
