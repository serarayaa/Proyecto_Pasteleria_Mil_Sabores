package cl.duoc.milsabores.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Tabla: recordatorios
 * Campos mínimos: uid del usuario, fecha creación (dd/MM/yyyy), mensaje
 */
@Entity(tableName = "recordatorios")
data class RecordatorioEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val uid: String,
    val createdAt: String, // dd/MM/yyyy (según tu implementación actual)
    val message: String
)
