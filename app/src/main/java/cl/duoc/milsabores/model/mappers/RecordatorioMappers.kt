// app/src/main/java/cl/duoc/milsabores/cl.duoc.milsabores.model/mappers/RecordatorioMappers.kt
package cl.duoc.milsabores.model.mappers

import cl.duoc.milsabores.data.local.RecordatorioEntity
import cl.duoc.milsabores.model.Recordatorio

fun RecordatorioEntity.toDto() = Recordatorio(
    id = id,
    uid = uid,
    createdAt = createdAt,
    message = message
)

fun Recordatorio.toEntity() = RecordatorioEntity(
    id = id,
    uid = uid,
    createdAt = createdAt,
    message = message
)
