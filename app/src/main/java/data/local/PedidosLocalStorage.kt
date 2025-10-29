package cl.duoc.milsabores.data.local

import android.content.Context
import android.content.SharedPreferences
import cl.duoc.milsabores.core.AppLogger
import cl.duoc.milsabores.core.Result
import cl.duoc.milsabores.model.Pedido
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

/**
 * Almacenamiento local de pedidos con SharedPreferences + JSON.
 * NOTA: Se considera "legacy". Próximo paso: migrar pedidos a Room.
 */
class PedidosLocalStorage(context: Context) {

    private val sharedPrefs: SharedPreferences = context.getSharedPreferences(
        "MilSaboresPedidos",
        Context.MODE_PRIVATE
    )
    private val gson = Gson()
    private val key = "pedidos_list"

    fun guardarPedido(pedido: Pedido): Result<String> = try {
        val pedidos = obtenerTodosPedidos().toMutableList()
        val nuevoId = "PEDIDO_${System.currentTimeMillis()}"
        val pedidoGuardado = pedido.copy(id = nuevoId)

        pedidos.add(pedidoGuardado)
        sharedPrefs.edit().putString(key, gson.toJson(pedidos)).apply()
        AppLogger.i("Pedido guardado localmente: $nuevoId", "PedidosLocal")

        Result.Success(nuevoId)
    } catch (e: Exception) {
        AppLogger.e("Error guardando pedido", e, "PedidosLocal")
        Result.Error("Error guardando pedido", e)
    }

    fun obtenerTodosPedidos(): List<Pedido> = try {
        val json = sharedPrefs.getString(key, null) ?: return emptyList()
        val type = object : TypeToken<List<Pedido>>() {}.type
        gson.fromJson<List<Pedido>>(json, type) ?: emptyList()
    } catch (e: Exception) {
        AppLogger.e("Error leyendo pedidos", e, "PedidosLocal")
        emptyList()
    }

    fun obtenerPedidosUsuario(uid: String): List<Pedido> =
        obtenerTodosPedidos().filter { it.uid == uid }

    fun actualizarPedido(pedido: Pedido): Result<String> = try {
        val pedidos = obtenerTodosPedidos().toMutableList()
        val idx = pedidos.indexOfFirst { it.id == pedido.id }
        if (idx != -1) {
            pedidos[idx] = pedido
            sharedPrefs.edit().putString(key, gson.toJson(pedidos)).apply()
            AppLogger.i("Pedido actualizado: ${pedido.id}", "PedidosLocal")
            Result.Success(pedido.id)
        } else {
            Result.Error("Pedido no encontrado")
        }
    } catch (e: Exception) {
        AppLogger.e("Error actualizando pedido", e, "PedidosLocal")
        Result.Error("Error actualizando pedido", e)
    }

    fun obtenerPedidoPorId(pedidoId: String): Pedido? =
        try { obtenerTodosPedidos().firstOrNull { it.id == pedidoId } }
        catch (_: Exception) { null }

    fun eliminarPedido(pedidoId: String): Result<Boolean> = try {
        val pedidos = obtenerTodosPedidos().toMutableList()
        val removido = pedidos.removeAll { it.id == pedidoId }
        sharedPrefs.edit().putString(key, gson.toJson(pedidos)).apply()
        AppLogger.i("Pedido eliminado: $pedidoId (removido=$removido)", "PedidosLocal")
        Result.Success(removido)
    } catch (e: Exception) {
        AppLogger.e("Error eliminando pedido", e, "PedidosLocal")
        Result.Error("Error eliminando pedido", e)
    }

    fun limpiarTodos(): Result<Boolean> = try {
        sharedPrefs.edit().remove(key).apply()
        AppLogger.i("Todos los pedidos locales limpiados", "PedidosLocal")
        Result.Success(true)
    } catch (e: Exception) {
        AppLogger.e("Error limpiando pedidos", e, "PedidosLocal")
        Result.Error("Error limpiando pedidos", e)
    }
}
