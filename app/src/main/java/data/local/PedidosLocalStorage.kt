package cl.duoc.milsabores.data.local

import android.content.Context
import android.content.SharedPreferences
import cl.duoc.milsabores.model.EstadoPedido
import cl.duoc.milsabores.model.Pedido
import cl.duoc.milsabores.model.ProductoPedido
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

/**
 * Gestor de pedidos locales usando SharedPreferences + JSON
 * Para almacenamiento local sin necesidad de Firebase
 */
class PedidosLocalStorage(private val context: Context) {

    private val sharedPrefs: SharedPreferences = context.getSharedPreferences(
        "MilSaboresPedidos",
        Context.MODE_PRIVATE
    )

    private val gson = Gson()
    private val key = "pedidos_list"

    /**
     * Guardar un nuevo pedido localmente
     */
    fun guardarPedido(pedido: Pedido): Result<String> {
        return try {
            val pedidos = obtenerTodosPedidos().toMutableList()
            val nuevoId = "PEDIDO_${System.currentTimeMillis()}"
            val pedidoGuardado = pedido.copy(id = nuevoId)

            pedidos.add(pedidoGuardado)

            val json = gson.toJson(pedidos)
            sharedPrefs.edit().putString(key, json).apply()

            Result.success(nuevoId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Obtener todos los pedidos guardados localmente
     */
    fun obtenerTodosPedidos(): List<Pedido> {
        return try {
            val json = sharedPrefs.getString(key, null) ?: return emptyList()
            val type = object : TypeToken<List<Pedido>>() {}.type
            gson.fromJson(json, type) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    /**
     * Obtener pedidos del usuario actual (simulado con filtro)
     */
    fun obtenerPedidosUsuario(uid: String): List<Pedido> {
        return obtenerTodosPedidos().filter { it.uid == uid }
    }

    /**
     * Actualizar estado de un pedido
     */
    fun actualizarPedido(pedido: Pedido): Result<String> {
        return try {
            val pedidos = obtenerTodosPedidos().toMutableList()
            val indice = pedidos.indexOfFirst { it.id == pedido.id }

            if (indice != -1) {
                pedidos[indice] = pedido
                val json = gson.toJson(pedidos)
                sharedPrefs.edit().putString(key, json).apply()
                Result.success(pedido.id)
            } else {
                Result.failure(Exception("Pedido no encontrado"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Obtener un pedido por ID
     */
    fun obtenerPedidoPorId(pedidoId: String): Pedido? {
        return obtenerTodosPedidos().firstOrNull { it.id == pedidoId }
    }

    /**
     * Eliminar un pedido
     */
    fun eliminarPedido(pedidoId: String): Result<Boolean> {
        return try {
            val pedidos = obtenerTodosPedidos().toMutableList()
            val removido = pedidos.removeAll { it.id == pedidoId }

            val json = gson.toJson(pedidos)
            sharedPrefs.edit().putString(key, json).apply()

            Result.success(removido)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Limpiar todos los pedidos
     */
    fun limpiarTodos(): Result<Boolean> {
        return try {
            sharedPrefs.edit().remove(key).apply()
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

