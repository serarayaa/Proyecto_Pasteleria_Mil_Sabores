package cl.duoc.milsabores.data.remote.dto

/**
 * DTO que refleja el esquema Producto del microservicio product-service.
 *
 * Campos según Swagger:
 *  - id: integer(int64)
 *  - nombre: string
 *  - descripcion: string
 *  - precio: string
 *  - categoria: string
 *  - stock: integer(int32)
 *  - urlImagen: string
 */
data class ProductoDto(
    val id: Long?,
    val nombre: String,
    val descripcion: String,
    val precio: String,
    val categoria: String,
    val stock: Int,
    val urlImagen: String?
)
