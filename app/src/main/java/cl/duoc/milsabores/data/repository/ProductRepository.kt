package cl.duoc.milsabores.data.repository

import cl.duoc.milsabores.data.remote.ProductApiService
import cl.duoc.milsabores.data.remote.RetrofitClient
import cl.duoc.milsabores.data.remote.dto.ProductoDto
import retrofit2.HttpException
import java.io.IOException

class ProductRepository(
    private val api: ProductApiService = RetrofitClient.productApi
) {

    /**
     * Obtiene todos los productos disponibles desde el microservicio.
     */
    suspend fun obtenerProductosDisponibles(): List<ProductoDto> {
        val response = api.getProductosDisponibles()
        if (response.isSuccessful) {
            return response.body().orEmpty()
        } else {
            throw HttpException(response)
        }
    }

    suspend fun obtenerProductos(): List<ProductoDto> {
        val response = api.getProductos()
        if (response.isSuccessful) {
            return response.body().orEmpty()
        } else {
            throw HttpException(response)
        }
    }

    suspend fun obtenerProductoPorId(id: Long): ProductoDto? {
        val response = api.getProductoPorId(id)
        if (response.isSuccessful) {
            return response.body()
        } else {
            throw HttpException(response)
        }
    }

    suspend fun obtenerPorCategoria(categoria: String): List<ProductoDto> {
        val response = api.getProductosPorCategoria(categoria)
        if (response.isSuccessful) {
            return response.body().orEmpty()
        } else {
            throw HttpException(response)
        }
    }

    suspend fun crearProducto(dto: ProductoDto): ProductoDto? {
        val response = api.crearProducto(dto)
        if (response.isSuccessful) {
            return response.body()
        } else {
            throw HttpException(response)
        }
    }

    suspend fun actualizarProducto(id: Long, dto: ProductoDto): ProductoDto? {
        val response = api.actualizarProducto(id, dto)
        if (response.isSuccessful) {
            return response.body()
        } else {
            throw HttpException(response)
        }
    }

    suspend fun eliminarProducto(id: Long) {
        val response = api.eliminarProducto(id)
        if (!response.isSuccessful) {
            throw HttpException(response)
        }
    }
}
