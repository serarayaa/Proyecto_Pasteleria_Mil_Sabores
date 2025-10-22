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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Kitchen
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.duoc.milsabores.model.EstadoPedido
import cl.duoc.milsabores.model.Pedido
import cl.duoc.milsabores.ui.theme.CaramelGold
import cl.duoc.milsabores.ui.theme.ChocolateBrown
import cl.duoc.milsabores.ui.theme.GradientOrange
import cl.duoc.milsabores.ui.theme.GradientPink
import cl.duoc.milsabores.ui.theme.StrawberryRed
import cl.duoc.milsabores.ui.theme.VanillaWhite
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PedidosScreen(
    vm: PedidosViewModel = viewModel()
) {
    val state by vm.ui.collectAsState()

    // Mostrar pantalla de detalle si hay un pedido seleccionado
    if (state.pedidoSeleccionado != null) {
        DetallePedidoScreen(
            pedido = state.pedidoSeleccionado!!,
            onBack = { vm.cerrarDetalle() },
            onCancelar = { pedidoId -> vm.cancelarPedido(pedidoId) }
        )
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.ShoppingBag,
                            contentDescription = null,
                            tint = StrawberryRed,
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text("Mis Pedidos")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { padding ->
        Box(
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
                )
        ) {
            if (state.loading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            } else if (state.pedidos.isEmpty()) {
                EmptyPedidosView(modifier = Modifier.align(Alignment.Center))
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        Text(
                            "Historial de Pedidos",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = ChocolateBrown,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }

                    items(state.pedidos, key = { it.id }) { pedido ->
                        PedidoCard(
                            pedido = pedido,
                            modifier = Modifier.animateItem(),
                            onClick = { vm.seleccionarPedido(pedido) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PedidoCard(
    pedido: Pedido,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 6.dp,
                shape = RoundedCornerShape(20.dp),
                spotColor = pedido.estado.color.copy(alpha = 0.3f)
            ),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(
                                pedido.estado.color.copy(alpha = 0.2f),
                                CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            when (pedido.estado) {
                                EstadoPedido.PENDIENTE -> Icons.Default.Schedule
                                EstadoPedido.EN_PREPARACION -> Icons.Default.Kitchen
                                EstadoPedido.LISTO -> Icons.Default.CheckCircle
                                EstadoPedido.ENTREGADO -> Icons.Default.Done
                            },
                            contentDescription = null,
                            tint = pedido.estado.color,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text(
                            pedido.id,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = ChocolateBrown
                        )
                        Text(
                            formatearFecha(pedido.fecha),
                            style = MaterialTheme.typography.bodySmall,
                            color = ChocolateBrown.copy(alpha = 0.6f)
                        )
                    }
                }

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
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        pedido.estado.displayName,
                        style = MaterialTheme.typography.labelMedium,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                pedido.productos.take(if (expanded) pedido.productos.size else 2).forEach { producto ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(modifier = Modifier.weight(1f)) {
                            Text(
                                "${producto.cantidad}x ",
                                style = MaterialTheme.typography.bodyMedium,
                                color = StrawberryRed,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                producto.nombre,
                                style = MaterialTheme.typography.bodyMedium,
                                color = ChocolateBrown
                            )
                        }
                        Text(
                            "$${String.format(Locale.getDefault(), "%,.0f", producto.precio)}",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = ChocolateBrown
                        )
                    }
                }

                if (pedido.productos.size > 2) {
                    TextButton(
                        onClick = { expanded = !expanded },
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    ) {
                        Text(
                            if (expanded) "Ver menos" else "Ver ${pedido.productos.size - 2} más",
                            color = StrawberryRed
                        )
                        Icon(
                            if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                            contentDescription = null,
                            tint = StrawberryRed,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            if (pedido.observaciones.isNotEmpty()) {
                Spacer(Modifier.height(12.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            GradientOrange.copy(alpha = 0.1f),
                            RoundedCornerShape(8.dp)
                        )
                        .padding(12.dp)
                ) {
                    Icon(
                        Icons.Default.Info,
                        contentDescription = null,
                        tint = CaramelGold,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        pedido.observaciones,
                        style = MaterialTheme.typography.bodySmall,
                        color = ChocolateBrown
                    )
                }
            }

            Spacer(Modifier.height(12.dp))
            HorizontalDivider(color = ChocolateBrown.copy(alpha = 0.1f))
            Spacer(Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Total",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = ChocolateBrown
                )
                Text(
                    "$${String.format(Locale.getDefault(), "%,.0f", pedido.total)}",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = StrawberryRed
                )
            }
        }
    }
}

@Composable
private fun EmptyPedidosView(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            Icons.Default.ShoppingBag,
            contentDescription = null,
            modifier = Modifier.size(120.dp),
            tint = ChocolateBrown.copy(alpha = 0.3f)
        )
        Spacer(Modifier.height(16.dp))
        Text(
            "No tienes pedidos aún",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = ChocolateBrown
        )
        Spacer(Modifier.height(8.dp))
        Text(
            "¡Explora nuestros deliciosos productos!",
            style = MaterialTheme.typography.bodyMedium,
            color = ChocolateBrown.copy(alpha = 0.6f)
        )
    }
}

private fun formatearFecha(timestamp: Long): String {
    val formato = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.forLanguageTag("es-ES"))
    return formato.format(Date(timestamp))
}

