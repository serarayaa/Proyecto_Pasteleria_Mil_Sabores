package cl.duoc.milsabores.ui.pedidos

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Kitchen
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cl.duoc.milsabores.model.EstadoPedido
import cl.duoc.milsabores.model.Pedido
import cl.duoc.milsabores.ui.theme.CaramelGold
import cl.duoc.milsabores.ui.theme.ChocolateBrown
import cl.duoc.milsabores.ui.theme.GradientOrange
import cl.duoc.milsabores.ui.theme.GradientPink
import cl.duoc.milsabores.ui.theme.StrawberryRed
import cl.duoc.milsabores.ui.theme.VanillaWhite
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetallePedidoScreen(
    pedido: Pedido,
    onBack: () -> Unit,
    onCancelar: (String) -> Unit = {}
) {
    var mostrarDialogoCancelar by remember { mutableStateOf(false) }
    val dateFormat = remember { SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()) }
    val currencyFormat = remember { NumberFormat.getCurrencyInstance(Locale("es", "CL")) }

    // Diálogo de confirmación para cancelar
    if (mostrarDialogoCancelar) {
        AlertDialog(
            onDismissRequest = { mostrarDialogoCancelar = false },
            title = { Text("Cancelar Pedido") },
            text = { Text("¿Estás seguro que deseas cancelar este pedido? Esta acción no se puede deshacer.") },
            confirmButton = {
                Button(
                    onClick = {
                        mostrarDialogoCancelar = false
                        onCancelar(pedido.id)
                        onBack()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Cancelar Pedido")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { mostrarDialogoCancelar = false }) {
                    Text("Volver")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle del Pedido") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            VanillaWhite,
                            GradientPink.copy(alpha = 0.2f)
                        )
                    )
                ),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Encabezado con ID y Estado
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(4.dp, RoundedCornerShape(16.dp)),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    "Pedido",
                                    style = MaterialTheme.typography.titleSmall,
                                    color = ChocolateBrown.copy(alpha = 0.6f)
                                )
                                Text(
                                    pedido.id,
                                    style = MaterialTheme.typography.headlineSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = ChocolateBrown
                                )
                            }

                            // Badge de estado
                            Box(
                                modifier = Modifier
                                    .background(
                                        brush = Brush.horizontalGradient(
                                            colors = listOf(
                                                pedido.estado.color,
                                                pedido.estado.color.copy(alpha = 0.7f)
                                            )
                                        ),
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                    .padding(horizontal = 16.dp, vertical = 8.dp)
                            ) {
                                Text(
                                    pedido.estado.displayName,
                                    style = MaterialTheme.typography.labelLarge,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 16.dp),
                            color = ChocolateBrown.copy(alpha = 0.1f)
                        )

                        // Fecha
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                Icons.Default.DateRange,
                                contentDescription = null,
                                tint = StrawberryRed,
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                dateFormat.format(Date(pedido.fecha)),
                                style = MaterialTheme.typography.bodyLarge,
                                color = ChocolateBrown
                            )
                        }
                    }
                }
            }

            // Timeline de estados
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(4.dp, RoundedCornerShape(16.dp)),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                    ) {
                        Text(
                            "Estado del Pedido",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = ChocolateBrown,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )

                        // Timeline
                        EstadoPedido.entries.forEachIndexed { index, estado ->
                            val isActual = estado == pedido.estado
                            val isCompletado = estado.ordinal <= pedido.estado.ordinal

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Círculo indicador
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .background(
                                            if (isCompletado) estado.color else Color.LightGray,
                                            CircleShape
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        when (estado) {
                                            EstadoPedido.PENDIENTE -> Icons.Default.Schedule
                                            EstadoPedido.EN_PREPARACION -> Icons.Default.Kitchen
                                            EstadoPedido.LISTO -> Icons.Default.CheckCircle
                                            EstadoPedido.ENTREGADO -> Icons.Default.Done
                                        },
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }

                                Spacer(Modifier.width(12.dp))

                                Text(
                                    estado.displayName,
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = if (isActual) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isCompletado) ChocolateBrown else ChocolateBrown.copy(alpha = 0.4f)
                                )
                            }

                            // Línea conectora (excepto en el último)
                            if (index < EstadoPedido.entries.size - 1) {
                                Box(
                                    modifier = Modifier
                                        .padding(start = 19.dp)
                                        .width(2.dp)
                                        .height(30.dp)
                                        .background(
                                            if (isCompletado) estado.color.copy(alpha = 0.5f)
                                            else Color.LightGray
                                        )
                                )
                            }
                        }
                    }
                }
            }

            // Lista de productos
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(4.dp, RoundedCornerShape(16.dp)),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.padding(bottom = 16.dp)
                        ) {
                            Icon(
                                Icons.Default.ShoppingBag,
                                contentDescription = null,
                                tint = StrawberryRed,
                                modifier = Modifier.size(24.dp)
                            )
                            Text(
                                "Productos",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = ChocolateBrown
                            )
                        }

                        pedido.productos.forEach { producto ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    modifier = Modifier.weight(1f),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Text(
                                        "${producto.cantidad}x",
                                        style = MaterialTheme.typography.bodyLarge,
                                        fontWeight = FontWeight.Bold,
                                        color = StrawberryRed
                                    )
                                    Text(
                                        producto.nombre,
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = ChocolateBrown
                                    )
                                }
                                Text(
                                    currencyFormat.format(producto.precio),
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.SemiBold,
                                    color = ChocolateBrown
                                )
                            }
                        }

                        HorizontalDivider(
                            modifier = Modifier.padding(vertical = 12.dp),
                            color = ChocolateBrown.copy(alpha = 0.2f),
                            thickness = 2.dp
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "Total",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = ChocolateBrown
                            )
                            Text(
                                currencyFormat.format(pedido.total),
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = StrawberryRed
                            )
                        }
                    }
                }
            }

            // Observaciones (si existen)
            if (pedido.observaciones.isNotEmpty()) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(4.dp, RoundedCornerShape(16.dp)),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = GradientOrange.copy(alpha = 0.1f)
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Icon(
                                Icons.Default.Info,
                                contentDescription = null,
                                tint = CaramelGold,
                                modifier = Modifier.size(24.dp)
                            )
                            Column {
                                Text(
                                    "Observaciones",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = ChocolateBrown
                                )
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    pedido.observaciones,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = ChocolateBrown.copy(alpha = 0.8f)
                                )
                            }
                        }
                    }
                }
            }

            // Botón de cancelar (solo si está pendiente)
            if (pedido.estado == EstadoPedido.PENDIENTE) {
                item {
                    OutlinedButton(
                        onClick = { mostrarDialogoCancelar = true },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Icon(Icons.Default.Cancel, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("Cancelar Pedido")
                    }
                }
            }

            // Espacio final
            item {
                Spacer(Modifier.height(32.dp))
            }
        }
    }
}

