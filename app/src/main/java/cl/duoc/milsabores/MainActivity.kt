package cl.duoc.milsabores

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import cl.duoc.milsabores.core.AppLogger
import cl.duoc.milsabores.ui.app.AppNavHost
import cl.duoc.milsabores.ui.theme.MilsaboresTheme
import cl.duoc.milsabores.utils.PermissionHelper

class MainActivity : ComponentActivity() {

    // Launcher para permisos de notificaciones
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            AppLogger.info("Permiso de notificaciones concedido")
        } else {
            AppLogger.warning("Permiso de notificaciones denegado")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        try {
            AppLogger.info("Iniciando aplicación Mil Sabores...")
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

            AppLogger.info("Aplicación iniciada correctamente")
        } catch (e: Exception) {
            AppLogger.error("Error crítico al iniciar aplicación", e)
        }
    }
}
