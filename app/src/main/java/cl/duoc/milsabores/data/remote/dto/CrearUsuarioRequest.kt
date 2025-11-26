package cl.duoc.milsabores.data.remote.dto

data class CrearUsuarioRequest(
    val rut: String,
    val nombre: String,
    val mail: String,
    val password: String,
    val idrol: Int = 1,                 // el backend lo fuerza igual a 1
    val idfirebase: String = "app-mobile",
    val fechaNac: String               // formato requerido: "dd-MM-yyyy"
)
