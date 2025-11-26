package cl.duoc.milsabores.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.milsabores.data.remote.dto.LoginRequest
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

    // ========================================================
    //  HANDLERS DE FORMULARIO
    // ========================================================

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

    // ========================================================
    //  VALIDACIONES (sin dependencia de Android SDK)
    // ========================================================

    private fun isEmailValid(email: String): Boolean {
        val emailRegex = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")
        return email.isNotBlank() && emailRegex.matches(email)
    }

    private fun isPasswordValid(password: String): Boolean {
        return password.length >= 6
    }

    // ========================================================
    //  LOGIN BACKEND
    // ========================================================

    fun login() {
        val current = ui.value

        var emailError: String? = null
        var passwordError: String? = null

        if (!isEmailValid(current.email)) {
            emailError = "Formato de correo inválido"
        }

        if (!isPasswordValid(current.password)) {
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

        // LOGIN REAL CONTRA EL BACKEND
        viewModelScope.launch {
            _ui.update { it.copy(loading = true, error = null, message = null) }

            try {
                val request = LoginRequest(
                    mail = current.email,
                    password = current.password
                )

                // El backend SIEMPRE devuelve un UsuarioResponseDto
                val backendUser = repository.login(request)

                _ui.update {
                    it.copy(
                        loading = false,
                        loggedIn = true,
                        isGuest = false,
                        backendUser = backendUser,
                        error = null,
                        message = "Bienvenido ${backendUser.email}"
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

    // ========================================================
    //  LOGIN INVITADO
    // ========================================================

    fun guestLogin() {
        _ui.update {
            it.copy(
                loading = false,
                loggedIn = true,
                isGuest = true,
                backendUser = UsuarioResponseDto(
                    rut = "GUEST",
                    email = "invitado@milsabores.cl",
                    token = "" // sin token real para invitado
                ),
                error = null,
                message = "Sesión de invitado"
            )
        }
    }

    // ========================================================
    //  UTILERÍA
    // ========================================================

    fun messageConsumed() {
        _ui.update { it.copy(message = null, error = null) }
    }
}
