package cl.duoc.milsabores.data.remote

import cl.duoc.milsabores.data.remote.dto.ProductoDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.POST
import retrofit2.http.Path

interface ProductApiService {

    @GET("/api/productos")
    suspend fun getProductos(): Response<List<ProductoDto>>

    @GET("/api/productos/{id}")
    suspend fun getProductoPorId(
        @Path("id") id: Long
    ): Response<ProductoDto>

    @GET("/api/productos/disponibles")
    suspend fun getProductosDisponibles(): Response<List<ProductoDto>>

    @GET("/api/productos/{categoria}")
    suspend fun getProductosPorCategoria(
        @Path("categoria") categoria: String
    ): Response<List<ProductoDto>>

    @POST("/api/productos")
    suspend fun crearProducto(
        @Body producto: ProductoDto
    ): Response<ProductoDto>

    @PUT("/api/productos/{id}")
    suspend fun actualizarProducto(
        @Path("id") id: Long,
        @Body producto: ProductoDto
    ): Response<ProductoDto>

    @DELETE("/api/productos/{id}")
    suspend fun eliminarProducto(
        @Path("id") id: Long
    ): Response<Unit>
}
