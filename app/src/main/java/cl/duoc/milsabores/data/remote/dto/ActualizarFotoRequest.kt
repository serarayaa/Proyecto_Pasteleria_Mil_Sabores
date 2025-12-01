package cl.duoc.milsabores.data.remote.dto

/**
 * Request para actualizar la foto de perfil del usuario.
 * La imagen va en Base64.
 */
data class ActualizarFotoRequest(
    val imagenBase64: String
)
