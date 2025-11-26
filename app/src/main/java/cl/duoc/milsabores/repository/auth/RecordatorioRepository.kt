package cl.duoc.milsabores.data.repository

import cl.duoc.milsabores.data.local.ReminderDao
import cl.duoc.milsabores.model.Recordatorio
import cl.duoc.milsabores.model.mappers.toDto
import cl.duoc.milsabores.model.mappers.toEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class RecordatorioRepository(
    private val dao: ReminderDao
) {
    fun observe(uid: String): Flow<List<Recordatorio>> =
        dao.observeByUid(uid).map { list -> list.map { it.toDto() } }

    suspend fun insert(recordatorio: Recordatorio): Long =
        dao.insert(recordatorio.toEntity())

    suspend fun update(recordatorio: Recordatorio) =
        dao.update(recordatorio.toEntity())

    suspend fun delete(recordatorio: Recordatorio) =
        dao.delete(recordatorio.toEntity())

    suspend fun findById(id: Long): Recordatorio? =
        dao.findById(id)?.toDto()
}