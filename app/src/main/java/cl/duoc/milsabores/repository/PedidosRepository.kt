// app/src/main/java/cl/duoc/milsabores/cl.duoc.milsabores.repository/PedidosRepository.kt
package cl.duoc.milsabores.repository

import cl.duoc.milsabores.core.AppLogger
import cl.duoc.milsabores.core.Result
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

    suspend fun crearPedido(pedido: Pedido): Result<String> {
        return try {
            AppLogger.d("Creando pedido...", "PedidosRepo")
            val uid = auth.currentUser?.uid ?: return Result.Error("Usuario no autenticado")
            val pedidoId = pedidosCollection.document().id
            val pedidoConId = pedido.copy(id = pedidoId)

            val pedidoData = hashMapOf(
                "id" to pedidoConId.id,
                "uid" to uid,
                "fecha" to pedidoConId.fecha,
                "productos" to pedidoConId.productos.map { p ->
                    hashMapOf("nombre" to p.nombre, "cantidad" to p.cantidad, "precio" to p.precio)
                },
                "total" to pedidoConId.total,
                "estado" to pedidoConId.estado.name,
                "observaciones" to pedidoConId.observaciones
            )

            pedidosCollection.document(pedidoId).set(pedidoData).await()
            AppLogger.i("Pedido creado: $pedidoId", "PedidosRepo")
            Result.Success(pedidoId)
        } catch (e: Exception) {
            AppLogger.e("Error creando pedido", e, "PedidosRepo")
            Result.Error("No se pudo crear el pedido", e)
        }
    }

    fun observarPedidosUsuario(): Flow<List<Pedido>> = callbackFlow {
        val uid = auth.currentUser?.uid
        if (uid == null) {
            AppLogger.w("observarPedidosUsuario: usuario no autenticado", "PedidosRepo")
            trySend(emptyList())
            close()
            return@callbackFlow
        }

        val listener = pedidosCollection
            .whereEqualTo("uid", uid)
            .orderBy("fecha", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    AppLogger.e("Error observando pedidos", error, "PedidosRepo")
                    trySend(emptyList()); return@addSnapshotListener
                }
                val pedidos = snapshot?.documents?.mapNotNull { doc ->
                    runCatching {
                        val productosData = doc.get("productos") as? List<Map<String, Any>>
                        val productos = productosData?.map { pr ->
                            ProductoPedido(
                                nombre = pr["nombre"] as? String ?: "",
                                cantidad = (pr["cantidad"] as? Number)?.toInt() ?: 0,
                                precio = (pr["precio"] as? Number)?.toDouble() ?: 0.0
                            )
                        } ?: emptyList()

                        val estado = runCatching {
                            EstadoPedido.valueOf(doc.getString("estado") ?: "PENDIENTE")
                        }.getOrDefault(EstadoPedido.PENDIENTE)

                        Pedido(
                            id = doc.getString("id") ?: doc.id,
                            uid = uid,
                            fecha = doc.getLong("fecha") ?: System.currentTimeMillis(),
                            productos = productos,
                            total = (doc.get("total") as? Number)?.toDouble() ?: 0.0,
                            estado = estado,
                            observaciones = doc.getString("observaciones") ?: ""
                        )
                    }.onFailure {
                        AppLogger.e("Parse error pedido ${doc.id}", it, "PedidosRepo")
                    }.getOrNull()
                } ?: emptyList()

                trySend(pedidos)
            }

        awaitClose { listener.remove() }
    }

    suspend fun obtenerPedido(pedidoId: String): Result<Pedido> {
        return try {
            val doc = pedidosCollection.document(pedidoId).get().await()
            if (!doc.exists()) return Result.Error("Pedido no encontrado")

            val productosData = doc.get("productos") as? List<Map<String, Any>>
            val productos = productosData?.map { pr ->
                ProductoPedido(
                    nombre = pr["nombre"] as? String ?: "",
                    cantidad = (pr["cantidad"] as? Number)?.toInt() ?: 0,
                    precio = (pr["precio"] as? Number)?.toDouble() ?: 0.0
                )
            } ?: emptyList()

            val estado = runCatching {
                EstadoPedido.valueOf(doc.getString("estado") ?: "PENDIENTE")
            }.getOrDefault(EstadoPedido.PENDIENTE)

            val uid = auth.currentUser?.uid ?: ""
            val pedido = Pedido(
                id = doc.getString("id") ?: doc.id,
                uid = uid,
                fecha = doc.getLong("fecha") ?: System.currentTimeMillis(),
                productos = productos,
                total = (doc.get("total") as? Number)?.toDouble() ?: 0.0,
                estado = estado,
                observaciones = doc.getString("observaciones") ?: ""
            )
            Result.Success(pedido)
        } catch (e: Exception) {
            AppLogger.e("Error obteniendo pedido", e, "PedidosRepo")
            Result.Error("No se pudo obtener el pedido", e)
        }
    }

    suspend fun actualizarEstadoPedido(pedidoId: String, nuevoEstado: EstadoPedido): Result<Unit> {
        return try {
            pedidosCollection.document(pedidoId).update("estado", nuevoEstado.name).await()
            AppLogger.i("Estado actualizado $pedidoId → ${nuevoEstado.name}", "PedidosRepo")
            Result.Success(Unit)
        } catch (e: Exception) {
            AppLogger.e("Error actualizando estado", e, "PedidosRepo")
            Result.Error("No se pudo actualizar el estado", e)
        }
    }

    suspend fun cancelarPedido(pedidoId: String): Result<Unit> {
        return try {
            val pedido = obtenerPedido(pedidoId)
            val p = (pedido as? Result.Success)?.data
                ?: return Result.Error("Pedido no encontrado")
            if (p.estado != EstadoPedido.PENDIENTE) {
                return Result.Error("Solo se pueden cancelar pedidos pendientes")
            }
            pedidosCollection.document(pedidoId).delete().await()
            AppLogger.i("Pedido cancelado $pedidoId", "PedidosRepo")
            Result.Success(Unit)
        } catch (e: Exception) {
            AppLogger.e("Error cancelando pedido", e, "PedidosRepo")
            Result.Error("No se pudo cancelar el pedido", e)
        }
    }

    fun observarTodosPedidos(): Flow<List<Pedido>> = callbackFlow {
        val listener = pedidosCollection
            .orderBy("fecha", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    AppLogger.e("Error observando todos los pedidos", error, "PedidosRepo")
                    trySend(emptyList()); return@addSnapshotListener
                }
                val pedidos = snapshot?.documents?.mapNotNull { doc ->
                    runCatching {
                        val productosData = doc.get("productos") as? List<Map<String, Any>>
                        val productos = productosData?.map { pr ->
                            ProductoPedido(
                                nombre = pr["nombre"] as? String ?: "",
                                cantidad = (pr["cantidad"] as? Number)?.toInt() ?: 0,
                                precio = (pr["precio"] as? Number)?.toDouble() ?: 0.0
                            )
                        } ?: emptyList()

                        val estado = runCatching {
                            EstadoPedido.valueOf(doc.getString("estado") ?: "PENDIENTE")
                        }.getOrDefault(EstadoPedido.PENDIENTE)

                        Pedido(
                            id = doc.getString("id") ?: doc.id,
                            fecha = doc.getLong("fecha") ?: System.currentTimeMillis(),
                            productos = productos,
                            total = (doc.get("total") as? Number)?.toDouble() ?: 0.0,
                            estado = estado,
                            observaciones = doc.getString("observaciones") ?: ""
                        )
                    }.onFailure {
                        AppLogger.e("Parse error pedido ${doc.id}", it, "PedidosRepo")
                    }.getOrNull()
                } ?: emptyList()

                trySend(pedidos)
            }
        awaitClose { listener.remove() }
    }
}
