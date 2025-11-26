package cl.duoc.milsabores.ui.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.milsabores.data.remote.dto.CrearUsuarioRequest
import cl.duoc.milsabores.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class RegistrarseUiState(
    val rut: String = "",
    val nombre: String = "",
    val fechaNacimiento: String = "",
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val loading: Boolean = false,
    val error: String? = null,
    val message: String? = null,
    val registered: Boolean = false
)

class RegistrarseViewModel : ViewModel() {

    private val authRepository = AuthRepository()

    private val _ui = MutableStateFlow(RegistrarseUiState())
    val ui: StateFlow<RegistrarseUiState> = _ui.asStateFlow()

    // -------- handlers de campos --------

    fun onRutChange(value: String) {
        _ui.update { it.copy(rut = value, error = null, message = null) }
    }

    fun onNombreChange(value: String) {
        _ui.update { it.copy(nombre = value, error = null, message = null) }
    }

    fun onFechaNacimientoChange(value: String) {
        _ui.update { it.copy(fechaNacimiento = value, error = null, message = null) }
    }

    fun onEmailChange(value: String) {
        _ui.update { it.copy(email = value, error = null, message = null) }
    }

    fun onPasswordChange(value: String) {
        _ui.update { it.copy(password = value, error = null, message = null) }
    }

    fun onConfirmPasswordChange(value: String) {
        _ui.update { it.copy(confirmPassword = value, error = null, message = null) }
    }

    private fun isEmailValid(email: String): Boolean {
        val regex = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")
        return email.isNotBlank() && regex.matches(email)
    }

    private fun isFechaValida(fecha: String): Boolean {
        // Solo validamos formato dd-MM-yyyy (ej: 10-05-2000)
        val regex = Regex("^\\d{2}-\\d{2}-\\d{4}\$")
        return fecha.isNotBlank() && regex.matches(fecha)
    }

    fun submit() {
        val s = _ui.value

        // Validaciones básicas
        if (s.nombre.isBlank() ||
            s.email.isBlank() ||
            s.password.isBlank() ||
            s.confirmPassword.isBlank() ||
            s.fechaNacimiento.isBlank()
        ) {
            _ui.update { it.copy(error = "Completa todos los campos obligatorios.") }
            return
        }

        if (!isEmailValid(s.email)) {
            _ui.update { it.copy(error = "El correo electrónico no es válido.") }
            return
        }

        if (s.password.length < 6) {
            _ui.update { it.copy(error = "La contraseña debe tener al menos 6 caracteres.") }
            return
        }

        if (s.password != s.confirmPassword) {
            _ui.update { it.copy(error = "Las contraseñas no coinciden.") }
            return
        }

        if (!isFechaValida(s.fechaNacimiento)) {
            _ui.update { it.copy(error = "La fecha debe tener formato dd-MM-aaaa (ej: 10-05-2000).") }
            return
        }

        // Armamos request EXACTO como lo espera el backend
        val req = CrearUsuarioRequest(
            rut = if (s.rut.isBlank()) "SIN_RUT" else s.rut,
            nombre = s.nombre,
            mail = s.email,
            password = s.password,
            fechaNac = s.fechaNacimiento
            // idrol e idfirebase usan los valores por defecto (1, "app-mobile")
        )

        viewModelScope.launch {
            _ui.update { it.copy(loading = true, error = null, message = null) }

            try {
                authRepository.registrar(req)

                _ui.update {
                    it.copy(
                        loading = false,
                        registered = true,
                        message = "Cuenta creada correctamente."
                    )
                }
            } catch (e: Exception) {
                _ui.update {
                    it.copy(
                        loading = false,
                        error = "No se pudo registrar: ${e.message}"
                    )
                }
            }
        }
    }

    fun clearMessage() {
        _ui.update { it.copy(message = null, error = null) }
    }
}
