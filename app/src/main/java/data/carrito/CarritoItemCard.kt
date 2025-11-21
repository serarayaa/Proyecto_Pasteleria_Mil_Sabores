package cl.duoc.milsabores.ui.carrito

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cl.duoc.milsabores.model.CarritoItem

@Composable
fun CarritoItemCard(
    item: CarritoItem,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit,
    onRemove: () -> Unit
) {
    Card(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(12.dp)) {
            Text(item.nombre, style = MaterialTheme.typography.titleMedium)
            Text("Cantidad: ${item.cantidad}")
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(onClick = onIncrement) { Text("+") }
                OutlinedButton(onClick = onDecrement) { Text("-") }
                OutlinedButton(onClick = onRemove) { Text("Quitar") }
            }
        }
    }
}
