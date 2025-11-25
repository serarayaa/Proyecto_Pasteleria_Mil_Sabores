package cl.duoc.milsabores.data.remote.dto

/**
 * DTO que se envía al auth-service para registrar un usuario nuevo.
 * Debe coincidir EXACTAMENTE con el record CrearUsuarioRequest del backend.
 *
 * Backend record esperado:
 * (rut, nombre, mail, password, idrol, idfirebase, fechaNacimiento, edad)
 */
data class CrearUsuarioRequest(
    val rut: String,
    val nombre: String,
    val mail: String,
    val password: String,
    val idrol: Int,
    val idfirebase: String?,      // puede ser null
    val fechaNacimiento: String,
    val edad: Int
)
