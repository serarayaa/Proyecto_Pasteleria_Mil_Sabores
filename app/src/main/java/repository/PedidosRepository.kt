package cl.duoc.milsabores.repository

import android.util.Log
import cl.duoc.milsabores.model.EstadoPedido
import cl.duoc.milsabores.model.Pedido
import cl.duoc.milsabores.model.ProductoPedido
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class PedidosRepository(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance(),
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) {
    private val pedidosCollection = firestore.collection("pedidos")

    companion object {
        private const val TAG = "PedidosRepository"
    }

    /**
     * Crear un nuevo pedido en Firebase
     */
    suspend fun crearPedido(pedido: Pedido): Result<String> {
        return try {
            val uid = auth.currentUser?.uid
                ?: return Result.failure(Exception("Usuario no autenticado"))

            val pedidoId = pedidosCollection.document().id
            val pedidoConId = pedido.copy(id = pedidoId)

            val pedidoData = hashMapOf(
                "id" to pedidoConId.id,
                "uid" to uid,
                "fecha" to pedidoConId.fecha,
                "productos" to pedidoConId.productos.map { producto ->
                    hashMapOf(
                        "nombre" to producto.nombre,
                        "cantidad" to producto.cantidad,
                        "precio" to producto.precio
                    )
                },
                "total" to pedidoConId.total,
                "estado" to pedidoConId.estado.name,
                "observaciones" to pedidoConId.observaciones
            )

            pedidosCollection.document(pedidoId).set(pedidoData).await()
            Log.d(TAG, "Pedido creado exitosamente: $pedidoId")
            Result.success(pedidoId)
        } catch (e: Exception) {
            Log.e(TAG, "Error al crear pedido", e)
            Result.failure(e)
        }
    }

    /**
     * Obtener pedidos del usuario actual en tiempo real
     */
    fun observarPedidosUsuario(): Flow<List<Pedido>> = callbackFlow {
        val uid = auth.currentUser?.uid
        if (uid == null) {
            Log.e(TAG, "Usuario no autenticado")
            trySend(emptyList())
            close()
            return@callbackFlow
        }

        val listener = pedidosCollection
            .whereEqualTo("uid", uid)
            .orderBy("fecha", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e(TAG, "Error al observar pedidos", error)
                    trySend(emptyList())
                    return@addSnapshotListener
                }

                val pedidos = snapshot?.documents?.mapNotNull { doc ->
                    try {
                        val productosData = doc.get("productos") as? List<HashMap<String, Any>>
                        val productos = productosData?.map { producto ->
                            ProductoPedido(
                                nombre = producto["nombre"] as? String ?: "",
                                cantidad = (producto["cantidad"] as? Long)?.toInt() ?: 0,
                                precio = (producto["precio"] as? Number)?.toDouble() ?: 0.0
                            )
                        } ?: emptyList()

                        val estadoStr = doc.getString("estado") ?: "PENDIENTE"
                        val estado = try {
                            EstadoPedido.valueOf(estadoStr)
                        } catch (e: Exception) {
                            EstadoPedido.PENDIENTE
                        }

                        Pedido(
                            id = doc.getString("id") ?: doc.id,
                            fecha = doc.getLong("fecha") ?: System.currentTimeMillis(),
                            productos = productos,
                            total = doc.getDouble("total") ?: 0.0,
                            estado = estado,
                            observaciones = doc.getString("observaciones") ?: ""
                        )
                    } catch (e: Exception) {
                        Log.e(TAG, "Error al parsear pedido: ${doc.id}", e)
                        null
                    }
                } ?: emptyList()

                trySend(pedidos)
            }

        awaitClose { listener.remove() }
    }

    /**
     * Obtener un pedido específico por ID
     */
    suspend fun obtenerPedido(pedidoId: String): Result<Pedido> {
        return try {
            val doc = pedidosCollection.document(pedidoId).get().await()

            if (!doc.exists()) {
                return Result.failure(Exception("Pedido no encontrado"))
            }

            val productosData = doc.get("productos") as? List<HashMap<String, Any>>
            val productos = productosData?.map { producto ->
                ProductoPedido(
                    nombre = producto["nombre"] as? String ?: "",
                    cantidad = (producto["cantidad"] as? Long)?.toInt() ?: 0,
                    precio = (producto["precio"] as? Number)?.toDouble() ?: 0.0
                )
            } ?: emptyList()

            val estadoStr = doc.getString("estado") ?: "PENDIENTE"
            val estado = try {
                EstadoPedido.valueOf(estadoStr)
            } catch (e: Exception) {
                EstadoPedido.PENDIENTE
            }

            val pedido = Pedido(
                id = doc.getString("id") ?: doc.id,
                fecha = doc.getLong("fecha") ?: System.currentTimeMillis(),
                productos = productos,
                total = doc.getDouble("total") ?: 0.0,
                estado = estado,
                observaciones = doc.getString("observaciones") ?: ""
            )

            Result.success(pedido)
        } catch (e: Exception) {
            Log.e(TAG, "Error al obtener pedido", e)
            Result.failure(e)
        }
    }

    /**
     * Actualizar el estado de un pedido (admin)
     */
    suspend fun actualizarEstadoPedido(pedidoId: String, nuevoEstado: EstadoPedido): Result<Unit> {
        return try {
            pedidosCollection.document(pedidoId)
                .update("estado", nuevoEstado.name)
                .await()
            Log.d(TAG, "Estado del pedido $pedidoId actualizado a ${nuevoEstado.name}")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error al actualizar estado del pedido", e)
            Result.failure(e)
        }
    }

    /**
     * Cancelar un pedido (solo si está PENDIENTE)
     */
    suspend fun cancelarPedido(pedidoId: String): Result<Unit> {
        return try {
            val pedido = obtenerPedido(pedidoId).getOrNull()
                ?: return Result.failure(Exception("Pedido no encontrado"))

            if (pedido.estado != EstadoPedido.PENDIENTE) {
                return Result.failure(Exception("Solo se pueden cancelar pedidos pendientes"))
            }

            pedidosCollection.document(pedidoId).delete().await()
            Log.d(TAG, "Pedido $pedidoId cancelado")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error al cancelar pedido", e)
            Result.failure(e)
        }
    }

    /**
     * Obtener todos los pedidos (admin)
     */
    fun observarTodosPedidos(): Flow<List<Pedido>> = callbackFlow {
        val listener = pedidosCollection
            .orderBy("fecha", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e(TAG, "Error al observar todos los pedidos", error)
                    trySend(emptyList())
                    return@addSnapshotListener
                }

                val pedidos = snapshot?.documents?.mapNotNull { doc ->
                    try {
                        val productosData = doc.get("productos") as? List<HashMap<String, Any>>
                        val productos = productosData?.map { producto ->
                            ProductoPedido(
                                nombre = producto["nombre"] as? String ?: "",
                                cantidad = (producto["cantidad"] as? Long)?.toInt() ?: 0,
                                precio = (producto["precio"] as? Number)?.toDouble() ?: 0.0
                            )
                        } ?: emptyList()

                        val estadoStr = doc.getString("estado") ?: "PENDIENTE"
                        val estado = try {
                            EstadoPedido.valueOf(estadoStr)
                        } catch (e: Exception) {
                            EstadoPedido.PENDIENTE
                        }

                        Pedido(
                            id = doc.getString("id") ?: doc.id,
                            fecha = doc.getLong("fecha") ?: System.currentTimeMillis(),
                            productos = productos,
                            total = doc.getDouble("total") ?: 0.0,
                            estado = estado,
                            observaciones = doc.getString("observaciones") ?: ""
                        )
                    } catch (e: Exception) {
                        Log.e(TAG, "Error al parsear pedido: ${doc.id}", e)
                        null
                    }
                } ?: emptyList()

                trySend(pedidos)
            }

        awaitClose { listener.remove() }
    }
}

