// app/src/main/java/cl/duoc/milsabores/model/Recordatorio.kt
package cl.duoc.milsabores.model

/** Modelo de dominio para recordatorios (UI/ViewModel). */
data class Recordatorio(
    val id: Long = 0L,   // id autogenerado por Room
    val uid: String,     // usuario dueño del recordatorio
    val createdAt: String, // dd/MM/yyyy
    val message: String
)
