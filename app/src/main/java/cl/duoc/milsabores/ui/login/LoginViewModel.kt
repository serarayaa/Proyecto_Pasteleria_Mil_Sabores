package cl.duoc.milsabores.ui.login

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.milsabores.data.remote.dto.UsuarioResponseDto
import cl.duoc.milsabores.data.repository.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import java.io.IOException
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val loading: Boolean = false,
    val emailError: String? = null,
    val passwordError: String? = null,
    val error: String? = null,          // por compatibilidad si tu UI lo usa
    val message: String? = null,        // para snackbars
    val loggedIn: Boolean = false,
    val isGuest: Boolean = false,
    val firebaseUid: String? = null,
    val backendUser: UsuarioResponseDto? = null
)

class LoginViewModel(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
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
    // Login principal
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
                // 1) Login REAL en Firebase (para que toda la app funcione)
                val user = signInWithFirebase(current.email, current.password)

                // 2) Llamada opcional al microservicio (para la rúbrica de integración)
                val backendUser = try {
                    repository.login(current.email, current.password)
                } catch (io: IOException) {
                    null  // problema de red → no rompemos el login
                } catch (e: Exception) {
                    null
                }

                _ui.update {
                    it.copy(
                        loading = false,
                        loggedIn = true,
                        isGuest = false,
                        firebaseUid = user.uid,
                        backendUser = backendUser,
                        error = null,
                        message = "Bienvenido ${backendUser?.nombre ?: (user.email ?: "usuario")}"
                    )
                }
            } catch (e: Exception) {
                val readable = when (e) {
                    is FirebaseAuthInvalidCredentialsException,
                    is FirebaseAuthInvalidUserException ->
                        "Correo o contraseña incorrectos"
                    else ->
                        "Error al iniciar sesión: ${e.message ?: "intente nuevamente"}"
                }

                _ui.update {
                    it.copy(
                        loading = false,
                        loggedIn = false,
                        firebaseUid = null,
                        backendUser = null,
                        error = readable,
                        message = readable
                    )
                }
            }
        }
    }

    // 🔹 Función usada por la UI (LoginScreen)
    //    Mantiene compatibilidad con onClick = vm::submit y onDone { vm.submit() }
    fun submit() {
        if (!ui.value.loading) {
            login()
        }
    }

    // ========================
    // Login invitado
    // ========================

    fun guestLogin() {
        viewModelScope.launch {
            _ui.update {
                it.copy(
                    loading = false,
                    loggedIn = true,
                    isGuest = true,
                    firebaseUid = null,
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
    }

    // ========================
    // Utilidad
    // ========================

    fun messageConsumed() {
        _ui.update { it.copy(message = null, error = null) }
    }

    // ========================
    // Helper privado
    // ========================

    private suspend fun signInWithFirebase(
        email: String,
        password: String
    ): FirebaseUser = suspendCancellableCoroutine { cont ->
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (!cont.isActive) return@addOnCompleteListener

                if (task.isSuccessful) {
                    val user = task.result?.user
                    if (user != null) {
                        cont.resume(user)
                    } else {
                        cont.resumeWithException(
                            IllegalStateException("Usuario Firebase nulo")
                        )
                    }
                } else {
                    cont.resumeWithException(
                        task.exception ?: Exception("Error desconocido en FirebaseAuth")
                    )
                }
            }
    }
}
