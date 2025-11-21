data class UsuarioResponseDto(
    val rut: String,
    val nombre: String,
    val mail: String,
    val idrol: Int,
    val idfirebase: String?,
    val imagen: String? = null
)
