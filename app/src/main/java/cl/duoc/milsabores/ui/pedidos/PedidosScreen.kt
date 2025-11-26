@file:OptIn(
    androidx.compose.material3.ExperimentalMaterial3Api::class,
    androidx.compose.foundation.ExperimentalFoundationApi::class
)

package cl.duoc.milsabores.ui.pedidos

import android.app.Application
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.duoc.milsabores.model.EstadoPedido
import cl.duoc.milsabores.model.Pedido
import cl.duoc.milsabores.ui.mapper.color
import cl.duoc.milsabores.ui.theme.CaramelGold
import cl.duoc.milsabores.ui.theme.ChocolateBrown
import cl.duoc.milsabores.ui.theme.GradientOrange
import cl.duoc.milsabores.ui.theme.GradientPink
import cl.duoc.milsabores.ui.theme.StrawberryRed
import cl.duoc.milsabores.ui.theme.VanillaWhite
import cl.duoc.milsabores.ui.util.clp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlinx.coroutines.delay

// ---------- Helpers de fecha ----------
private val fechaClFormatter by lazy {
    SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.forLanguageTag("es-ES"))
}
private fun formatearFecha(timestamp: Long): String = fechaClFormatter.format(Date(timestamp))

// ---------- Pantalla principal ----------
@Composable
fun PedidosScreen() {
    val context = androidx.compose.ui.platform.LocalContext.current
    val isDarkMode = androidx.compose.foundation.isSystemInDarkTheme()
    var isVisible by remember { mutableStateOf(false) }

    // Animación de entrada
    LaunchedEffect(Unit) {
        delay(100)
        isVisible = true
    }

    val vmFactory = remember {
        object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return PedidosViewModel(context.applicationContext as Application) as T
            }
        }
    }
    val vm: PedidosViewModel = viewModel(factory = vmFactory)
    val state by vm.ui.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }
    LaunchedEffect(state.message) {
        state.message?.let {
            snackbarHostState.showSnackbar(it)
            vm.consumeMessage()
        }
    }

    // Si hay pedido seleccionado, mostramos la pantalla de detalle
    if (state.pedidoSeleccionado != null) {
        DetallePedidoScreen(
            pedido = state.pedidoSeleccionado!!,
            onBack = { vm.cerrarDetalle() },
            onCancelar = { pedidoId -> vm.cancelarPedido(pedidoId) },
            isLoadingCancel = state.loading
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
                        Text(
                            "Mis Pedidos",
                            fontWeight = FontWeight.Bold,
                            color = if (isDarkMode) {
                                Color(0xFFE8E8E8)
                            } else {
                                ChocolateBrown
                            }
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = if (isDarkMode) {
                        Color(0xFF1E1E1E)
                    } else {
                        MaterialTheme.colorScheme.surface
                    }
                )
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .then(
                    if (isDarkMode) {
                        Modifier.background(Color(0xFF121212))
                    } else {
                        Modifier.background(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    VanillaWhite,
                                    GradientPink.copy(alpha = 0.2f)
                                )
                            )
                        )
                    }
                )
        ) {
            when {
                state.loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                state.pedidos.isEmpty() -> {
                    EmptyPedidosView(
                        modifier = Modifier.align(Alignment.Center),
                        onVerProductos = { /* navegar a tienda */ }
                    )
                }

                else -> {
                    AnimatedVisibility(
                        visible = isVisible,
                        enter = fadeIn(tween(400)),
                        exit = fadeOut()
                    ) {
                        LazyColumn(
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            item {
                                Text(
                                    "Historial de Pedidos",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isDarkMode) {
                                        Color(0xFFE8E8E8)
                                    } else {
                                        ChocolateBrown
                                    },
                                    modifier = Modifier.padding(bottom = 8.dp)
                                )
                            }

                            items(state.pedidos, key = { it.id }) { pedido ->
                                AnimatedVisibility(
                                    visible = true,
                                    enter = fadeIn(tween(300)) +
                                            slideInVertically(tween(300)) { it / 2 },
                                    exit = fadeOut()
                                ) {
                                    PedidoCard(
                                        pedido = pedido,
                                        modifier = Modifier
                                            .animateContentSize(
                                                animationSpec = spring(
                                                    dampingRatio = Spring.DampingRatioMediumBouncy,
                                                    stiffness = Spring.StiffnessLow
                                                )
                                            ),
                                        onClick = { vm.seleccionarPedido(pedido) },
                                        isDarkMode = isDarkMode
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// ---------- Card de pedido ----------
@Composable
private fun PedidoCard(
    pedido: Pedido,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    isDarkMode: Boolean = false
) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 6.dp,
                shape = RoundedCornerShape(20.dp),
                spotColor = pedido.estado.color().copy(alpha = 0.3f)
            ),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isDarkMode) {
                Color(0xFF2A2A2A)
            } else {
                Color.White
            }
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Cabecera con estado
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
                                pedido.estado.color().copy(alpha = 0.2f),
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
                            tint = pedido.estado.color(),
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

                // Badge del estado
                Box(
                    modifier = Modifier
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(
                                    pedido.estado.color(),
                                    pedido.estado.color().copy(alpha = 0.7f)
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

            // Lista de productos
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                val toShow = if (expanded) pedido.productos else pedido.productos.take(2)
                toShow.forEach { producto ->
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
                            clp(producto.precio),
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
                            imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                            contentDescription = null,
                            tint = StrawberryRed,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            // Observaciones
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
            Divider(color = ChocolateBrown.copy(alpha = 0.1f), thickness = 1.dp)
            Spacer(Modifier.height(12.dp))

            // Total
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
                    clp(pedido.total),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = StrawberryRed
                )
            }
        }
    }
}

// ---------- Vista vacía ----------
@Composable
private fun EmptyPedidosView(
    modifier: Modifier = Modifier,
    onVerProductos: () -> Unit
) {
    Column(
        modifier = modifier.padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Card(
            modifier = Modifier
                .size(140.dp)
                .shadow(12.dp, CircleShape),
            shape = CircleShape,
            colors = CardDefaults.cardColors(
                containerColor = GradientPink.copy(alpha = 0.2f)
            )
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.ShoppingBag,
                    contentDescription = null,
                    modifier = Modifier.size(70.dp),
                    tint = StrawberryRed.copy(alpha = 0.6f)
                )
            }
        }

        Spacer(Modifier.height(24.dp))

        Text(
            "No tienes pedidos aún",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = ChocolateBrown,
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(12.dp))

        Text(
            "Tus pedidos aparecerán aquí una vez que realices tu primera compra",
            style = MaterialTheme.typography.bodyLarge,
            color = ChocolateBrown.copy(alpha = 0.6f),
            textAlign = TextAlign.Center
        )
    }
}
