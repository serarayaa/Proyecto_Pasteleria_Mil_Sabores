package cl.duoc.milsabores.ui.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.milsabores.data.remote.dto.CrearUsuarioRequest
import cl.duoc.milsabores.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter
import javax.inject.Inject

data class RegistrarseUiState(
    val rut: String = "",
    val nombre: String = "",
    val fechaNacimiento: String = "",   // formato: dd-MM-aaaa
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val loading: Boolean = false,
    val error: String? = null,
    val message: String? = null,
    val registered: Boolean = false
)

@HiltViewModel
class RegistrarseViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _ui = MutableStateFlow(RegistrarseUiState())
    val ui: StateFlow<RegistrarseUiState> = _ui.asStateFlow()

    fun onRutChange(value: String) {
        _ui.update { it.copy(rut = value, error = null) }
    }

    fun onNombreChange(value: String) {
        _ui.update { it.copy(nombre = value, error = null) }
    }

    fun onFechaNacimientoChange(value: String) {
        _ui.update { it.copy(fechaNacimiento = value, error = null) }
    }

    fun onEmailChange(value: String) {
        _ui.update { it.copy(email = value, error = null) }
    }

    fun onPasswordChange(value: String) {
        _ui.update { it.copy(password = value, error = null) }
    }

    fun onConfirmPasswordChange(value: String) {
        _ui.update { it.copy(confirmPassword = value, error = null) }
    }

    private fun calcularEdad(fechaNacimiento: String): Int? {
        if (fechaNacimiento.isBlank()) return null
        return try {
            val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
            val fecha = LocalDate.parse(fechaNacimiento, formatter)
            Period.between(fecha, LocalDate.now()).years
        } catch (e: Exception) {
            null
        }
    }

    fun submit() {
        val state = _ui.value

        // Validaciones básicas
        if (state.nombre.isBlank() ||
            state.email.isBlank() ||
            state.password.isBlank() ||
            state.confirmPassword.isBlank() ||
            state.fechaNacimiento.isBlank()
        ) {
            _ui.update { it.copy(error = "Completa todos los campos obligatorios.") }
            return
        }

        if (!state.email.contains("@")) {
            _ui.update { it.copy(error = "El correo electrónico no es válido.") }
            return
        }

        if (state.password.length < 6) {
            _ui.update { it.copy(error = "La contraseña debe tener al menos 6 caracteres.") }
            return
        }

        if (state.password != state.confirmPassword) {
            _ui.update { it.copy(error = "Las contraseñas no coinciden.") }
            return
        }

        val edad = calcularEdad(state.fechaNacimiento)
        if (edad == null || edad < 0) {
            _ui.update {
                it.copy(
                    error = "Fecha de nacimiento inválida. Usa el formato dd-MM-aaaa."
                )
            }
            return
        }

        viewModelScope.launch {
            _ui.update { it.copy(loading = true, error = null, message = null, registered = false) }
            try {
                val request = CrearUsuarioRequest(
                    rut = if (state.rut.isBlank()) "SIN_RUT" else state.rut,
                    nombre = state.nombre,
                    mail = state.email,
                    password = state.password,
                    idrol = 2,                  // 2 = cliente / usuario normal
                    idfirebase = null,
                    fechaNacimiento = state.fechaNacimiento,
                    edad = edad
                )

                // Llamada al backend (auth-service)
                authRepository.registrar(request)

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
                        error = e.message ?: "No se pudo registrar el usuario."
                    )
                }
            }
        }
    }

    fun clearMessage() {
        _ui.update { it.copy(message = null, error = null) }
    }
}
