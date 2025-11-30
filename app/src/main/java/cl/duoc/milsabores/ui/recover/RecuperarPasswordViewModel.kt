package cl.duoc.milsabores.ui.recover

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.milsabores.data.remote.AuthApiClient
import cl.duoc.milsabores.data.remote.dto.RecoverPasswordRequestDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class RecoverUiState(
    val email: String = "",
    val loading: Boolean = false,
    val error: String? = null,
    val sent: Boolean = false,
    val successMessage: String? = null
)

class RecuperarPasswordViewModel : ViewModel() {

    private val _ui = MutableStateFlow(RecoverUiState())
    val ui: StateFlow<RecoverUiState> = _ui

    fun onEmailChange(v: String) {
        _ui.update {
            it.copy(
                email = v,
                error = null,
                sent = false,
                successMessage = null
            )
        }
    }

    fun submit() {
        val s = _ui.value
        val email = s.email.trim()

        // Validación de email
        if (email.isBlank()) {
            _ui.update { it.copy(error = "Ingresa tu correo electrónico") }
            return
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _ui.update { it.copy(error = "Email inválido") }
            return
        }

        viewModelScope.launch {
            _ui.update {
                it.copy(
                    loading = true,
                    error = null,
                    sent = false,
                    successMessage = null
                )
            }

            try {
                // 🔹 Llamada REAL al backend
                val response = AuthApiClient.api.recoverPassword(
                    RecoverPasswordRequestDto(mail = email)
                )

                if (response.isSuccessful) {
                    _ui.update {
                        it.copy(
                            loading = false,
                            sent = true,
                            successMessage = "Si el correo está registrado, enviaremos instrucciones para recuperar tu contraseña."
                        )
                    }
                } else {
                    _ui.update {
                        it.copy(
                            loading = false,
                            error = "No se pudo procesar la solicitud. Inténtalo nuevamente."
                        )
                    }
                }

            } catch (e: Exception) {
                _ui.update {
                    it.copy(
                        loading = false,
                        error = e.message ?: "Error al conectar con el servidor"
                    )
                }
            }
        }
    }
}
