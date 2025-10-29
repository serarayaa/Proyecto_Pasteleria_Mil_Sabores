package cl.duoc.milsabores.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

class NotificationHelper(private val context: Context) {
    private val channelId = "orders"

    private fun ensureChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val channel = NotificationChannel(channelId, "Pedidos", NotificationManager.IMPORTANCE_DEFAULT)
            channel.description = "Notificaciones de pedidos"
            nm.createNotificationChannel(channel)
        }
    }

    fun notificarPedidoCreado(pedidoId: String, total: Double) {
        ensureChannel()
        val notif = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("Pedido creado")
            .setContentText("ID: $pedidoId • Total: $total")
            .setAutoCancel(true)
            .build()
        NotificationManagerCompat.from(context).notify(pedidoId.hashCode(), notif)
    }

    fun notificarCambioEstado(pedidoId: String, estado: Any) {
        ensureChannel()
        val notif = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("Estado actualizado")
            .setContentText("Pedido $pedidoId → $estado")
            .setAutoCancel(true)
            .build()
        NotificationManagerCompat.from(context).notify((pedidoId + "_estado").hashCode(), notif)
    }
}
