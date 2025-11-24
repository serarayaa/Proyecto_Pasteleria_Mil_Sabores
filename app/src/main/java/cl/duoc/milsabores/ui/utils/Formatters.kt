package cl.duoc.milsabores.ui.util

import java.text.NumberFormat
import java.util.Locale

// Formateador de moneda para Chile (CLP)
val ClCurrency: NumberFormat by lazy { NumberFormat.getCurrencyInstance(Locale("es", "CL")) }
fun clp(amount: Number): String = ClCurrency.format(amount)

// (Opcional) Si más adelante quieres fechas centralizadas, se puede agregar aquí.
