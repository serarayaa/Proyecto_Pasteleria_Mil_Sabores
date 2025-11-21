package cl.duoc.milsabores.ui.login

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.milsabores.data.remote.dto.UsuarioResponseDto
import cl.duoc.milsabores.repository.auth.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.IOException

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val loading: Boolean = false,
    val error: String? = null,
    val loggedIn: Boolean = false,
    val usuario: UsuarioResponseDto? = null,
    val message: String? = null,
    // Validaciones de campos
    val emailError: String? = null,
    val passwordError: String? = null
)

class LoginViewModel(
    private val repo: AuthRepository = AuthRepository()
) : ViewModel() {

    private val _ui = MutableStateFlow(LoginUiState())
    val ui: StateFlow<LoginUiState> = _ui

    // ---------------------------------------
    // ACTUALIZADORES DE CAMPOS
    // ---------------------------------------

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

    // ---------------------------------------
    // SUBMIT → LOGIN VIA RETROFIT
    // ---------------------------------------

    fun submit() {
        val err = validar()
        if (err != null) {
            _ui.update { it.copy(error = err) }
            return
        }

        viewModelScope.launch {
            _ui.update { it.copy(loading = true, error = null, message = null) }

            try {
                val response = repo.login(
                    _ui.value.email.trim(),
                    _ui.value.password.trim()
                )

                if (response != null) {
                    // Login exitoso
                    _ui.update {
                        it.copy(
                            loading = false,
                            loggedIn = true,
                            usuario = response,
                            message = "Ingreso exitoso"
                        )
                    }
                } else {
                    _ui.update {
                        it.copy(
                            loading = false,
                            error = "Credenciales incorrectas"
                        )
                    }
                }

            } catch (io: IOException) {
                _ui.update {
                    it.copy(
                        loading = false,
                        error = "Error de conexión con el servidor"
                    )
                }
            } catch (e: Exception) {
                _ui.update {
                    it.copy(
                        loading = false,
                        error = "Error inesperado: ${e.message}"
                    )
                }
            }
        }
    }

    // ---------------------------------------
    // LOGIN COMO INVITADO (SIN CAMBIOS)
    // ---------------------------------------

    fun guestLogin() {
        viewModelScope.launch {
            _ui.update {
                it.copy(
                    loading = false,
                    loggedIn = true,
                    usuario = UsuarioResponseDto(
                        rut = "0-0",
                        nombre = "Invitado",
                        mail = "invitado@milsabores.cl",
                        idrol = 0,
                        idfirebase = null
                    ),
                    message = "Sesión de invitado"
                )
            }
        }
    }

    fun messageConsumed() {
        _ui.update { it.copy(message = null) }
    }
}
