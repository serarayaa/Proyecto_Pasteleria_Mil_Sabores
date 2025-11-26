package cl.duoc.milsabores.ui.time

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.milsabores.data.repository.TimeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TimeViewModel(
    private val repo: TimeRepository = TimeRepository()
) : ViewModel() {

    private val _horaChile = MutableStateFlow<String?>(null)
    val horaChile: StateFlow<String?> = _horaChile.asStateFlow()

    init {
        cargarHoraChile()
    }

    fun cargarHoraChile() {
        viewModelScope.launch {
            try {
                _horaChile.value = repo.obtenerHoraChile()
            } catch (e: Exception) {
                // Si falla la API externa, simplemente no mostramos la hora
                _horaChile.value = null
            }
        }
    }
}
