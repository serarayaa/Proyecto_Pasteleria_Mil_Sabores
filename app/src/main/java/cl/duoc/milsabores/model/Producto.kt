package cl.duoc.milsabores.ui.model

import androidx.annotation.DrawableRes
import cl.duoc.milsabores.R
import cl.duoc.milsabores.data.remote.dto.ProductoDto

data class Producto(
    val id: Int,
    val titulo: String,
    val precio: String,
    @DrawableRes val imagenRes: Int,
    val categoria: String,
    val descripcion: String
)

/**
 * Lista de respaldo local para casos en que el backend falle.
 */
val productosDemo = listOf(
    Producto(
        id = 1,
        titulo = "Bombones de Chocolate",
        precio = "$2.500",
        imagenRes = R.drawable.bombon_chocolate,
        categoria = "Pasteles",
        descripcion = "Delicioso bombón de Chocolate con exquisita cobertura."
    ),
    Producto(
        id = 2,
        titulo = "Torta de Chocolate",
        precio = "$15.000",
        imagenRes = R.drawable.torta_chocolate,
        categoria = "Postres",
        descripcion = "Suave torta de Chocolate con ingredientes seleccionados."
    ),
    Producto(
        id = 3,
        titulo = "Pie de Limón",
        precio = "$8.000",
        imagenRes = R.drawable.pie_limon,
        categoria = "Dulces",
        descripcion = "Sabroso Pie de Limón con ingredientes de alta calidad."
    )
)

/**
 * Mapea un ProductoDto del backend a un modelo de UI [Producto].
 *
 * Por ahora usamos imágenes locales según la categoría para no
 * romper los composables que esperan un @DrawableRes.
 */
fun ProductoDto.toUiModel(): Producto {
    val precioFormateado = formatearPrecioConSimbolo(precio)

    val drawable = when (categoria.lowercase()) {
        "pasteles" -> R.drawable.bombon_chocolate
        "postres" -> R.drawable.torta_chocolate
        "dulces" -> R.drawable.pie_limon
        else -> R.drawable.torta_chocolate
    }

    return Producto(
        id = (id ?: 0L).toInt(),
        titulo = nombre,
        precio = precioFormateado,
        imagenRes = drawable,
        categoria = categoria,
        descripcion = descripcion
    )
}

private fun formatearPrecioConSimbolo(precioBruto: String): String {
    // Dejamos solo dígitos
    val soloDigitos = precioBruto.filter { it.isDigit() }
    if (soloDigitos.isEmpty()) return "$0"

    // Agregamos puntos de miles
    val conPuntos = soloDigitos.reversed()
        .chunked(3)
        .joinToString(".")
        .reversed()

    return "$$conPuntos"
}
