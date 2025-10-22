package cl.duoc.milsabores.service

import android.content.Context
import android.util.Log
import cl.duoc.milsabores.model.EstadoPedido
import cl.duoc.milsabores.model.Pedido
import cl.duoc.milsabores.notifications.NotificationHelper
import cl.duoc.milsabores.repository.PedidosRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

/**
 * Servicio que observa cambios en pedidos y envía notificaciones
 */
class PedidosObserverService(
    private val context: Context,
    private val repository: PedidosRepository = PedidosRepository()
) {
    private val scope = CoroutineScope(Dispatchers.IO + Job())
    private val notificationHelper = NotificationHelper(context)
    private val estadosAnteriores = mutableMapOf<String, EstadoPedido>()

    companion object {
        private const val TAG = "PedidosObserver"

        @Volatile
        private var instance: PedidosObserverService? = null

        fun getInstance(context: Context): PedidosObserverService {
            return instance ?: synchronized(this) {
                instance ?: PedidosObserverService(context.applicationContext).also {
                    instance = it
                }
            }
        }
    }

    /**
     * Iniciar observación de pedidos
     */
    fun iniciarObservacion() {
        scope.launch {
            try {
                repository.observarPedidosUsuario().collect { pedidos ->
                    pedidos.forEach { pedido ->
                        verificarCambioEstado(pedido)
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error al observar pedidos", e)
            }
        }
    }

    /**
     * Verificar si cambió el estado de un pedido y notificar
     */
    private fun verificarCambioEstado(pedido: Pedido) {
        val estadoAnterior = estadosAnteriores[pedido.id]

        if (estadoAnterior == null) {
            // Primera vez que vemos este pedido
            estadosAnteriores[pedido.id] = pedido.estado
            return
        }

        if (estadoAnterior != pedido.estado) {
            // El estado cambió, enviar notificación
            Log.d(TAG, "Estado cambió de $estadoAnterior a ${pedido.estado} para pedido ${pedido.id}")
            notificationHelper.notificarCambioEstado(pedido.id, pedido.estado)
            estadosAnteriores[pedido.id] = pedido.estado
        }
    }

    /**
     * Notificar creación de pedido
     */
    fun notificarPedidoCreado(pedidoId: String, total: Double) {
        notificationHelper.notificarPedidoCreado(pedidoId, total)
    }

    /**
     * Detener observación
     */
    fun detener() {
        estadosAnteriores.clear()
    }
}

