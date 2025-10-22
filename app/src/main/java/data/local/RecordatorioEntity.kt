package cl.duoc.milsabores.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Tabla: recordatorios
 * Requisitos: uid, fecha de creación (dd/MM/yyyy), mensaje
 */
@Entity(tableName = "recordatorios")
data class RecordatorioEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val uid: String,
    val createdAt: String,   // formato: dd/MM/yyyy (manteniendo tu implementación original)
    val message: String
)
