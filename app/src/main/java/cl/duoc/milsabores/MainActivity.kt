package cl.duoc.milsabores

import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import androidx.core.content.getSystemService
import cl.duoc.milsabores.core.AppLogger
import cl.duoc.milsabores.data.local.Prefs
import cl.duoc.milsabores.ui.app.AppNavHost
import cl.duoc.milsabores.ui.theme.MilsaboresTheme
import cl.duoc.milsabores.utils.PermissionHelper
import com.google.firebase.FirebaseApp   // ✅ IMPORTANTE

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

            // ✅ INICIALIZAR FIREBASE ANTES DE USAR FirebaseAuth EN CUALQUIER VM
            FirebaseApp.initializeApp(this)

            // ✅ Crear canal(es) de notificación una vez
            createNotificationChannel()

            // Solicitar permiso de notificaciones en Android 13+
            if (!PermissionHelper.hasNotificationPermission(this)) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    requestPermissionLauncher.launch(
                        android.Manifest.permission.POST_NOTIFICATIONS
                    )
                }
            }

            setContent {
                val prefs = remember { Prefs(this) }
                var dark by remember { mutableStateOf(prefs.darkMode) }

                // Guardar preferencia de modo oscuro
                LaunchedEffect(dark) {
                    prefs.darkMode = dark
                }

                MilsaboresTheme(darkTheme = dark) {
                    AppNavHost(onDarkModeChanged = { newValue -> dark = newValue })
                }
            }

            AppLogger.info("Aplicación iniciada correctamente")
        } catch (e: Exception) {
            AppLogger.error("Error crítico al iniciar aplicación", e)
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = "orders"
            val name = "Pedidos"
            val desc = "Notificaciones de confirmación y estado de pedidos"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(channelId, name, importance).apply {
                description = desc
                enableVibration(true)
            }
            val nm: NotificationManager? = getSystemService()
            nm?.createNotificationChannel(channel)
        }
    }
}
