package cl.duoc.milsabores.ui.login

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.milsabores.data.remote.dto.UsuarioResponseDto
import cl.duoc.milsabores.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val loading: Boolean = false,
    val emailError: String? = null,
    val passwordError: String? = null,
    val error: String? = null,
    val message: String? = null,
    val loggedIn: Boolean = false,
    val isGuest: Boolean = false,
    val firebaseUid: String? = null,
    val backendUser: UsuarioResponseDto? = null
)

class LoginViewModel(
    private val repository: AuthRepository = AuthRepository()
) : ViewModel() {

    private val _ui = MutableStateFlow(LoginUiState())
    val ui: StateFlow<LoginUiState> = _ui

    // ========================
    // Handlers de formulario
    // ========================

    fun onEmailChange(value: String) {
        _ui.update {
            it.copy(
                email = value,
                emailError = null,
                error = null,
                message = null
            )
        }
    }

    fun onPasswordChange(value: String) {
        _ui.update {
            it.copy(
                password = value,
                passwordError = null,
                error = null,
                message = null
            )
        }
    }

    // ========================
    // Login SOLO con backend
    // ========================

    fun login() {
        val current = ui.value

        var emailError: String? = null
        var passwordError: String? = null

        if (!Patterns.EMAIL_ADDRESS.matcher(current.email).matches()) {
            emailError = "Formato de correo inválido"
        }
        if (current.password.length < 6) {
            passwordError = "La contraseña debe tener al menos 6 caracteres"
        }

        if (emailError != null || passwordError != null) {
            _ui.update {
                it.copy(
                    emailError = emailError,
                    passwordError = passwordError
                )
            }
            return
        }

        viewModelScope.launch {
            _ui.update { it.copy(loading = true, error = null, message = null) }

            try {
                // Login en tu microservicio auth-service
                val backendUser = repository.login(current.email, current.password)
                    ?: throw Exception("Correo o contraseña incorrectos")

                _ui.update {
                    it.copy(
                        loading = false,
                        loggedIn = true,
                        isGuest = false,
                        backendUser = backendUser,
                        error = null,
                        // tu DTO tiene 'nombre' y 'mail', no 'email'
                        message = "Bienvenido ${backendUser.nombre ?: backendUser.mail}"
                    )
                }

            } catch (e: Exception) {
                val readable = e.message ?: "Error al iniciar sesión. Intenta nuevamente."

                _ui.update {
                    it.copy(
                        loading = false,
                        loggedIn = false,
                        backendUser = null,
                        error = readable,
                        message = readable
                    )
                }
            }
        }
    }

    fun submit() {
        if (!ui.value.loading) login()
    }

    // ========================
    // Login invitado
    // ========================

    fun guestLogin() {
        _ui.update {
            it.copy(
                loading = false,
                loggedIn = true,
                isGuest = true,
                backendUser = UsuarioResponseDto(
                    rut = "GUEST",
                    nombre = "Invitado",
                    mail = "invitado@milsabores.cl",
                    idrol = 0,
                    idfirebase = null
                ),
                error = null,
                message = "Sesión de invitado"
            )
        }
    }

    // ========================
    // Utilidad
    // ========================

    fun messageConsumed() {
        _ui.update { it.copy(message = null, error = null) }
    }
}
