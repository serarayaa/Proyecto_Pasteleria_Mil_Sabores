package cl.duoc.milsabores.data.repository

import cl.duoc.milsabores.data.remote.time.WorldTimeApi

class TimeRepository(
    // 👉 Usamos el factory correcto del propio WorldTimeApi
    private val api: WorldTimeApi = WorldTimeApi.create()
) {

    /**
     * Llama a la API y devuelve solo la hora en formato HH:mm (hora chilena).
     */
    suspend fun obtenerHoraChile(): String {
        val resp = api.getChileTime()
        // Ej: "2025-11-26T16:43:12.123456-03:00"
        val timePart = resp.datetime.substringAfter("T")  // "16:43:12.123456-03:00"
        val hhmm = timePart.take(5)                        // "16:43"
        return hhmm
    }
}
