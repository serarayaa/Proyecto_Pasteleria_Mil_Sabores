package cl.duoc.milsabores.ui.login

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.milsabores.repository.auth.AuthRepository
import cl.duoc.milsabores.ui.model.User
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val loading: Boolean = false,
    val error: String? = null,
    val loggedIn: Boolean = false,
    val user: User? = null,
    val message: String? = null,
    // Validaciones por campo
    val emailError: String? = null,
    val passwordError: String? = null
)

class LoginViewModel(
    private val repo: AuthRepository = AuthRepository()
) : ViewModel() {

    private val _ui = MutableStateFlow(LoginUiState())
    val ui: StateFlow<LoginUiState> = _ui

    fun onEmailChange(v: String) {
        val emailError = if (v.isNotEmpty() && !Patterns.EMAIL_ADDRESS.matcher(v).matches()) {
            "Email inválido"
        } else null
        _ui.update { it.copy(email = v, emailError = emailError, error = null, message = null) }
    }

    fun onPasswordChange(v: String) {
        val passError = if (v.isNotEmpty() && v.length < 6) {
            "La clave debe tener al menos 6 caracteres"
        } else null
        _ui.update { it.copy(password = v, passwordError = passError, error = null, message = null) }
    }

    private fun validar(): String? {
        val s = _ui.value
        if (!Patterns.EMAIL_ADDRESS.matcher(s.email).matches()) return "Email inválido"
        if (s.password.length < 6) return "La clave debe tener al menos 6 caracteres"
        return null
    }

    fun submit() {
        val err = validar()
        if (err != null) {
            _ui.update { it.copy(error = err) }
            return
        }
        viewModelScope.launch {
            _ui.update { it.copy(loading = true, error = null, message = null) }
            try {
                android.util.Log.d("LoginViewModel", "Iniciando login para: ${_ui.value.email}")
                val user = repo.login(_ui.value.email.trim(), _ui.value.password)
                _ui.update {
                    if (user != null) {
                        android.util.Log.d("LoginViewModel", "Login exitoso para: ${user.email}")
                        it.copy(loading = false, loggedIn = true, user = user, message = "Ingreso exitoso")
                    } else {
                        android.util.Log.e("LoginViewModel", "Login falló: usuario null")
                        it.copy(loading = false, error = "Error al iniciar sesión")
                    }
                }
            } catch (e: FirebaseAuthInvalidUserException) {
                android.util.Log.e("LoginViewModel", "Usuario no existe: ${e.message}")
                _ui.update { it.copy(loading = false, error = "Usuario no registrado. Verifica el correo: ${_ui.value.email.trim()}") }
            } catch (e: FirebaseAuthInvalidCredentialsException) {
                android.util.Log.e("LoginViewModel", "Credenciales incorrectas: ${e.message}")
                _ui.update { it.copy(loading = false, error = "Contraseña incorrecta. Verifica tus datos.") }
            } catch (e: Exception) {
                android.util.Log.e("LoginViewModel", "Error desconocido: ${e.javaClass.simpleName} - ${e.message}", e)
                _ui.update { it.copy(loading = false, error = "Error: ${e.message ?: "Conexión fallida"}") }
            }
        }
    }

    fun messageConsumed() { _ui.update { it.copy(message = null) } }
}
