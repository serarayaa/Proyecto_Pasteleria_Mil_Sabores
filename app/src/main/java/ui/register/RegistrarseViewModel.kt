package cl.duoc.milsabores.ui.register

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.milsabores.repository.auth.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class RegisterUiState(
    val email: String = "",
    val pass: String = "",
    val confirm: String = "",
    val loading: Boolean = false,
    val error: String? = null,
    val registered: Boolean = false,
    // Validaciones por campo
    val emailError: String? = null,
    val passError: String? = null,
    val confirmError: String? = null
)

class RegistrarseViewModel(
    private val repo: AuthRepository = AuthRepository()
) : ViewModel() {

    private val _ui = MutableStateFlow(RegisterUiState())
    val ui: StateFlow<RegisterUiState> = _ui

    fun onEmail(v: String) {
        val emailError = if (v.isNotEmpty() && !Patterns.EMAIL_ADDRESS.matcher(v).matches()) {
            "Email inválido"
        } else null
        _ui.update { it.copy(email = v, emailError = emailError, error = null, registered = false) }
    }

    fun onPass(v: String) {
        val passError = if (v.isNotEmpty() && v.length < 6) {
            "Clave de 6+ caracteres"
        } else null
        _ui.update { it.copy(pass = v, passError = passError, error = null, registered = false) }
    }

    fun onConfirm(v: String) {
        val confirmError = if (v.isNotEmpty() && v != _ui.value.pass) {
            "Las claves no coinciden"
        } else null
        _ui.update { it.copy(confirm = v, confirmError = confirmError, error = null, registered = false) }
    }

    fun submit() {
        val s = _ui.value
        when {
            !Patterns.EMAIL_ADDRESS.matcher(s.email).matches() ->
                _ui.update { it.copy(error = "Email inválido") }
            s.pass.length < 6 ->
                _ui.update { it.copy(error = "Clave de 6+ caracteres") }
            s.pass != s.confirm ->
                _ui.update { it.copy(error = "Las claves no coinciden") }
            else -> viewModelScope.launch {
                _ui.update { it.copy(loading = true, error = null, registered = false) }
                try {
                    repo.register(s.email, s.pass)
                    _ui.update { it.copy(loading = false, registered = true) }
                } catch (e: Exception) {
                    _ui.update { it.copy(loading = false, error = (e.message ?: "Error al registrar")) }
                }
            }
        }
    }
}
