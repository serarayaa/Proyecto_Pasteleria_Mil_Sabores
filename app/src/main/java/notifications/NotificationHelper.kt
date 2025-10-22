package cl.duoc.milsabores.notifications

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import cl.duoc.milsabores.MainActivity
import cl.duoc.milsabores.model.EstadoPedido

class NotificationHelper(private val context: Context) {

    companion object {
        const val CHANNEL_ID = "pedidos_channel"
        const val CHANNEL_NAME = "Pedidos"
        const val CHANNEL_DESCRIPTION = "Notificaciones sobre el estado de tus pedidos"
        const val NOTIFICATION_ID_BASE = 1000
    }

    init {
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance).apply {
                description = CHANNEL_DESCRIPTION
                enableLights(true)
                enableVibration(true)
            }

            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    @SuppressLint("MissingPermission")
    fun notificarCambioEstado(pedidoId: String, nuevoEstado: EstadoPedido) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("pedidoId", pedidoId)
            putExtra("abrirPedidos", true)
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            pedidoId.hashCode(),
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val (titulo, mensaje, icono) = when (nuevoEstado) {
            EstadoPedido.PENDIENTE -> Triple(
                "Pedido Recibido 📝",
                "Tu pedido $pedidoId ha sido recibido y está siendo procesado.",
                android.R.drawable.ic_dialog_info
            )
            EstadoPedido.EN_PREPARACION -> Triple(
                "¡Preparando tu pedido! 👨‍🍳",
                "Tu pedido $pedidoId está siendo preparado con mucho cariño.",
                android.R.drawable.ic_menu_today
            )
            EstadoPedido.LISTO -> Triple(
                "¡Pedido Listo! ✅",
                "Tu pedido $pedidoId está listo para ser recogido.",
                android.R.drawable.ic_menu_agenda
            )
            EstadoPedido.ENTREGADO -> Triple(
                "Pedido Entregado 🎉",
                "Tu pedido $pedidoId ha sido entregado. ¡Que lo disfrutes!",
                android.R.drawable.ic_menu_compass
            )
        }

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(icono)
            .setContentTitle(titulo)
            .setContentText(mensaje)
            .setStyle(NotificationCompat.BigTextStyle().bigText(mensaje))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setVibrate(longArrayOf(0, 500, 250, 500))
            .build()

        NotificationManagerCompat.from(context).notify(
            NOTIFICATION_ID_BASE + pedidoId.hashCode(),
            notification
        )
    }

    @SuppressLint("MissingPermission")
    fun notificarPedidoCreado(pedidoId: String, total: Double) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("pedidoId", pedidoId)
            putExtra("abrirPedidos", true)
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            pedidoId.hashCode(),
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("¡Pedido Creado! 🎂")
            .setContentText("Tu pedido $pedidoId por $${"%.0f".format(total)} ha sido creado exitosamente.")
            .setStyle(NotificationCompat.BigTextStyle()
                .bigText("Tu pedido $pedidoId por $${"%.0f".format(total)} ha sido creado exitosamente. Te notificaremos cuando cambie su estado."))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setVibrate(longArrayOf(0, 300, 200, 300))
            .build()

        NotificationManagerCompat.from(context).notify(
            NOTIFICATION_ID_BASE + pedidoId.hashCode(),
            notification
        )
    }

    fun cancelarTodasLasNotificaciones() {
        NotificationManagerCompat.from(context).cancelAll()
    }

    fun cancelarNotificacion(pedidoId: String) {
        NotificationManagerCompat.from(context).cancel(NOTIFICATION_ID_BASE + pedidoId.hashCode())
    }
}

