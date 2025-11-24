package cl.duoc.milsabores.ui.register

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

data class RegisterUiState(
    val email: String = "",
    val pass: String = "",
    val confirm: String = "",
    val loading: Boolean = false,
    val error: String? = null,          // para compatibilidad con la UI
    val registered: Boolean = false,
    // Validaciones por campo
    val emailError: String? = null,
    val passError: String? = null,
    val confirmError: String? = null,
    val message: String? = null         // por si quieres mostrar snackbar
)

class RegistrarseViewModel(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) : ViewModel() {

    private val _ui = MutableStateFlow(RegisterUiState())
    val ui: StateFlow<RegisterUiState> = _ui

    fun onEmail(v: String) {
        val emailError = if (v.isNotEmpty() && !Patterns.EMAIL_ADDRESS.matcher(v).matches()) {
            "Email inválido"
        } else null
        _ui.update {
            it.copy(
                email = v,
                emailError = emailError,
                error = null,
                message = null,
                registered = false
            )
        }
    }

    fun onPass(v: String) {
        val passError = if (v.isNotEmpty() && v.length < 6) {
            "Clave de 6+ caracteres"
        } else null
        _ui.update {
            it.copy(
                pass = v,
                passError = passError,
                error = null,
                message = null,
                registered = false
            )
        }
    }

    fun onConfirm(v: String) {
        val confirmError = if (v.isNotEmpty() && v != _ui.value.pass) {
            "Las claves no coinciden"
        } else null
        _ui.update {
            it.copy(
                confirm = v,
                confirmError = confirmError,
                error = null,
                message = null,
                registered = false
            )
        }
    }

    fun submit() {
        val s = _ui.value

        // Validaciones finales
        when {
            !Patterns.EMAIL_ADDRESS.matcher(s.email).matches() ->
                _ui.update { it.copy(error = "Email inválido", message = "Email inválido") }

            s.pass.length < 6 ->
                _ui.update { it.copy(error = "Clave de 6+ caracteres", message = "Clave de 6+ caracteres") }

            s.pass != s.confirm ->
                _ui.update { it.copy(error = "Las claves no coinciden", message = "Las claves no coinciden") }

            else -> viewModelScope.launch {
                _ui.update { it.copy(loading = true, error = null, message = null, registered = false) }

                try {
                    // Registro REAL en FirebaseAuth
                    createUserWithFirebase(s.email, s.pass)

                    // Pequeña pausa solo para efecto visual si quieres mantenerlo
                    delay(300)

                    _ui.update {
                        it.copy(
                            loading = false,
                            registered = true,
                            error = null,
                            message = "Cuenta creada correctamente"
                        )
                    }
                } catch (e: Exception) {
                    val readable = when (e) {
                        is FirebaseAuthUserCollisionException ->
                            "Este correo ya está registrado"
                        else ->
                            "Error al registrar: ${e.message ?: "intente nuevamente"}"
                    }

                    _ui.update {
                        it.copy(
                            loading = false,
                            registered = false,
                            error = readable,
                            message = readable
                        )
                    }
                }
            }
        }
    }

    fun messageConsumed() {
        _ui.update { it.copy(message = null, error = null) }
    }

    private suspend fun createUserWithFirebase(
        email: String,
        password: String
    ) = suspendCancellableCoroutine { cont ->
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (!cont.isActive) return@addOnCompleteListener

                if (task.isSuccessful) {
                    cont.resume(Unit)
                } else {
                    cont.resumeWithException(
                        task.exception ?: Exception("Error desconocido al registrar")
                    )
                }
            }
    }
}
