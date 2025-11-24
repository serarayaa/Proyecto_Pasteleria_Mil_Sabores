package cl.duoc.milsabores.ui.recover

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class RecoverUiState(
    val email: String = "",
    val loading: Boolean = false,
    val error: String? = null,
    val sent: Boolean = false
)

class RecuperarPasswordViewModel : ViewModel() {

    private val _ui = MutableStateFlow(RecoverUiState())
    val ui: StateFlow<RecoverUiState> = _ui

    fun onEmailChange(v: String) {
        _ui.update { it.copy(email = v, error = null, sent = false) }
    }

    fun submit() {
        val s = _ui.value

        // Validación de email
        if (!Patterns.EMAIL_ADDRESS.matcher(s.email).matches()) {
            _ui.update { it.copy(error = "Email inválido") }
            return
        }

        viewModelScope.launch {
            _ui.update { it.copy(loading = true, error = null, sent = false) }

            try {
                // 🚨 Aquí antes llamaba a Firebase:
                // repo.sendPasswordReset(s.email)

                // Ahora NO tenemos backend para esto, así que simulamos éxito:
                kotlinx.coroutines.delay(1000)

                _ui.update {
                    it.copy(
                        loading = false,
                        sent = true
                    )
                }

            } catch (e: Exception) {
                _ui.update {
                    it.copy(
                        loading = false,
                        error = e.message ?: "Error al enviar enlace"
                    )
                }
            }
        }
    }
}
