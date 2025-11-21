// app/src/main/java/cl/duoc/milsabores/service/PedidosObserverService.kt
package cl.duoc.milsabores.service

import android.content.Context
import cl.duoc.milsabores.core.AppLogger
import cl.duoc.milsabores.model.EstadoPedido
import cl.duoc.milsabores.model.Pedido
import cl.duoc.milsabores.notifications.NotificationHelper
import cl.duoc.milsabores.repository.PedidosRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class PedidosObserverService(
    private val context: Context,
    private val repository: PedidosRepository = PedidosRepository()
) {
    private val job = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.IO + job)
    private val notificationHelper = NotificationHelper(context)
    private val estadosAnteriores = mutableMapOf<String, EstadoPedido>()

    companion object {
        @Volatile private var instance: PedidosObserverService? = null
        fun getInstance(context: Context): PedidosObserverService =
            instance ?: synchronized(this) {
                instance ?: PedidosObserverService(context.applicationContext).also { instance = it }
            }
    }

    fun iniciarObservacion() {
        scope.launch {
            try {
                AppLogger.i("Iniciando observación de pedidos...", "PedidosObserver")
                repository.observarPedidosUsuario().collect { pedidos ->
                    pedidos.forEach { verificarCambioEstado(it) }
                }
            } catch (e: Exception) {
                AppLogger.e("Error observando pedidos", e, "PedidosObserver")
            }
        }
    }

    private fun verificarCambioEstado(pedido: Pedido) {
        val anterior = estadosAnteriores[pedido.id]
        if (anterior == null) {
            estadosAnteriores[pedido.id] = pedido.estado
            return
        }
        if (anterior != pedido.estado) {
            AppLogger.d("Estado cambió ${pedido.id}: $anterior -> ${pedido.estado}", "PedidosObserver")
            notificationHelper.notificarCambioEstado(pedido.id, pedido.estado)
            estadosAnteriores[pedido.id] = pedido.estado
        }
    }

    fun notificarPedidoCreado(pedidoId: String, total: Double) {
        notificationHelper.notificarPedidoCreado(pedidoId, total)
    }

    fun detener() {
        estadosAnteriores.clear()
        job.cancel() // ✅ cancela el scope
        AppLogger.i("Observación detenida y recursos liberados", "PedidosObserver")
    }
}
