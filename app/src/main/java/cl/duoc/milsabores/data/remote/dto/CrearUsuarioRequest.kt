package cl.duoc.milsabores.data.remote.dto

data class CrearUsuarioRequest(
    val rut: String,
    val nombre: String,
    val mail: String,
    val password: String,
    val idrol: Int = 1,
    val idfirebase: String? = null
)
