package cl.duoc.milsabores.ui.vmfactory

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import cl.duoc.milsabores.data.local.AppDatabase
import cl.duoc.milsabores.data.repository.RecordatorioRepository
import cl.duoc.milsabores.ui.recordatorio.RecordatorioViewModel

class RecordatorioVMFactory(
    context: Context,
    private val uid: String
) : ViewModelProvider.Factory {

    private val repo by lazy {
        val db = AppDatabase.getInstance(context)
        RecordatorioRepository(db.reminderDao())
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return RecordatorioViewModel(repo, uid) as T
    }
}
