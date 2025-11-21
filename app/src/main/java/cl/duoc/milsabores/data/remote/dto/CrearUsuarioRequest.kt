data class CrearUsuarioRequest(
    val rut: String,
    val nombre: String,
    val mail: String,
    val password: String,
    val idrol: Int,
    val idfirebase: String?
)
