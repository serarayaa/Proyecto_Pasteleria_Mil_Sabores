package cl.duoc.milsabores

import android.app.Application
import com.google.firebase.FirebaseApp

class MilSaboresApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        // Inicialización de Firebase (si la usas)
        FirebaseApp.initializeApp(this)
    }
}
