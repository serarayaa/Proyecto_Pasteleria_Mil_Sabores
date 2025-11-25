package cl.duoc.milsabores

import android.app.Application
import cl.duoc.milsabores.core.AppLogger
import cl.duoc.milsabores.service.PedidosObserverService
import com.google.firebase.FirebaseApp
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MilSaboresApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        // Inicializar Firebase
        try {
            FirebaseApp.initializeApp(this)
            AppLogger.i("Firebase inicializado correctamente")
        } catch (e: Exception) {
            AppLogger.e("Error inicializando Firebase", e)
        }

        // Iniciar servicio de observación de pedidos (si existe)
        try {
            PedidosObserverService.getInstance(this).iniciarObservacion()
            AppLogger.i("PedidosObserverService iniciado")
        } catch (e: Exception) {
            AppLogger.e("No se pudo iniciar PedidosObserverService", e)
        }
    }
}
