package cl.duoc.milsabores.ui.model

import androidx.annotation.DrawableRes
import cl.duoc.milsabores.R

data class Producto(
    val id: Int,
    val titulo: String,
    val precio: String,
    @DrawableRes val imagenRes: Int,
    val categoria: String,
    val descripcion: String
)

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
