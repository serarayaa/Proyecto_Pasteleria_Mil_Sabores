package cl.duoc.milsabores

import android.app.Application
import cl.duoc.milsabores.service.PedidosObserverService
import com.google.firebase.FirebaseApp

class MilSaboresApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // Inicializar Firebase explícitamente
        try {
            FirebaseApp.initializeApp(this)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        // Iniciar servicio de observación de pedidos
        try {
            PedidosObserverService.getInstance(this).iniciarObservacion()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

