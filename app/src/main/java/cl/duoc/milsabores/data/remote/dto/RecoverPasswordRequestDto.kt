package cl.duoc.milsabores.data.remote.dto

/**
 * DTO para consumir POST /auth/recover-password en el backend.
 * El backend espera un JSON con el campo "mail".
 */
data class RecoverPasswordRequestDto(
    val mail: String
)
