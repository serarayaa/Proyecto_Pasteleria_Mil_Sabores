package cl.duoc.milsabores

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import cl.duoc.milsabores.ui.app.AppNavHost
import cl.duoc.milsabores.ui.theme.MilsaboresTheme
import cl.duoc.milsabores.utils.PermissionHelper

class MainActivity : ComponentActivity() {

    // Launcher para permisos de notificaciones
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            Log.d("MainActivity", "Permiso de notificaciones concedido")
        } else {
            Log.d("MainActivity", "Permiso de notificaciones denegado")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        try {
            Log.d("MainActivity", "Iniciando aplicación...")
            enableEdgeToEdge()

            // Solicitar permisos de notificaciones
            if (!PermissionHelper.hasNotificationPermission(this)) {
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                    requestPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
                }
            }

            setContent {
                MilsaboresTheme {
                    AppNavHost()
                }
            }

            Log.d("MainActivity", "Aplicación iniciada correctamente")
        } catch (e: Exception) {
            Log.e("MainActivity", "Error al iniciar aplicación", e)
            e.printStackTrace()
        }
    }
}
