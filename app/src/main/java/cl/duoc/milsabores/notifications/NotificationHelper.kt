package cl.duoc.milsabores.notifications

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

class NotificationHelper(private val context: Context) {

    private val channelId = "orders"

    // ================================
    // Canal (igual que antes)
    // ================================
    private fun ensureChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val channel = NotificationChannel(
                channelId,
                "Pedidos",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            channel.description = "Notificaciones de pedidos"
            nm.createNotificationChannel(channel)
        }
    }

    // ================================
    // Safe notify para evitar errores
    // ================================
    private fun notifySafe(id: Int, notification: android.app.Notification) {
        // Android 13+ → verificar POST_NOTIFICATIONS
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val granted = ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED

            if (!granted) {
                // Si el permiso NO está otorgado, no notificamos
                return
            }
        }

        NotificationManagerCompat.from(context).notify(id, notification)
    }

    // ================================
    // Notificación: pedido creado
    // ================================
    fun notificarPedidoCreado(pedidoId: String, total: Double) {
        ensureChannel()

        val notif = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("Pedido creado")
            .setContentText("ID: $pedidoId • Total: $total")
            .setAutoCancel(true)
            .build()

        notifySafe(pedidoId.hashCode(), notif)
    }

    // ================================
    // Notificación: cambio de estado
    // ================================
    fun notificarCambioEstado(pedidoId: String, estado: Any) {
        ensureChannel()

        val notif = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("Estado actualizado")
            .setContentText("Pedido $pedidoId → $estado")
            .setAutoCancel(true)
            .build()

        notifySafe((pedidoId + "_estado").hashCode(), notif)
    }
}
