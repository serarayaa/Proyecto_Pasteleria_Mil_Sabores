package cl.duoc.milsabores.data.remote.dto

data class UsuarioResponseDto(
    val rut: String,
    val nombre: String,
    val mail: String,
    val idrol: Int,
    val idfirebase: String?,
    val imagen: String? = null   // Podemos mapearla a base64 o URL más adelante
)
