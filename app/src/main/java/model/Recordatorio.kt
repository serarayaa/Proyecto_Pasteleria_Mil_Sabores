package cl.duoc.milsabores.model

/**
 * Modelo de dominio para mostrar o manipular recordatorios
 * en la capa de presentación (UI o ViewModel).
 */
data class Recordatorio(
    val id: Long = 0L, // id autogenerado por Room
    val uid: String, // uid del usuario dueño del recordatorio
    val createdAt: String,  // formato dd/MM/yyyy
    val message: String // mensaje del recordatorio
)
