package cl.duoc.milsabores.ui.carrito

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
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
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.duoc.milsabores.model.CarritoItem
import cl.duoc.milsabores.ui.theme.*
import coil.compose.AsyncImage
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CarritoScreen(
    vm: CarritoViewModel,
    onPedidoCreado: () -> Unit = {}
) {
    val items by vm.items.collectAsState()
    val total by vm.total.collectAsState()
    val uiState by vm.uiState.collectAsState()

    var mostrarDialogoConfirmacion by remember { mutableStateOf(false) }

    // Navegar a pedidos cuando el pedido se crea exitosamente
    LaunchedEffect(uiState.pedidoCreado) {
        if (uiState.pedidoCreado) {
            vm.resetPedidoCreado()
            onPedidoCreado()
        }
    }

    // Mostrar diálogo de confirmación mejorado
    if (mostrarDialogoConfirmacion) {
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { mostrarDialogoConfirmacion = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.ShoppingCart,
                        contentDescription = null,
                        tint = StrawberryRed,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(Modifier.width(12.dp))
                    Text("Confirmar Pedido", fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column {
                    Text("¿Deseas confirmar tu pedido por un total de:")
                    Spacer(Modifier.height(12.dp))
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = GradientPink.copy(alpha = 0.2f)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            "$${String.format(Locale.getDefault(), "%.0f", total)}",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = StrawberryRed,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        )
                    }
                    Spacer(Modifier.height(12.dp))
                    Text(
                        "Tu pedido será procesado por la pastelería.",
                        style = MaterialTheme.typography.bodySmall,
                        color = ChocolateBrown.copy(alpha = 0.7f)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        mostrarDialogoConfirmacion = false
                        vm.finalizarCompra()
                    },
                    enabled = !uiState.procesandoPedido,
                    colors = ButtonDefaults.buttonColors(containerColor = StrawberryRed),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    if (uiState.procesandoPedido) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Icon(Icons.Default.CheckCircle, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("Confirmar")
                    }
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { mostrarDialogoConfirmacion = false },
                    enabled = !uiState.procesandoPedido,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Cancelar")
                }
            }
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        VanillaWhite,
                        GradientPink.copy(alpha = 0.15f),
                        PastelPeach.copy(alpha = 0.1f)
                    )
                )
            )
    ) {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.ShoppingCart,
                                contentDescription = null,
                                tint = StrawberryRed,
                                modifier = Modifier.size(28.dp)
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                "Carrito de Compras",
                                fontWeight = FontWeight.Bold
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent
                    )
                )
            }
        ) { padding ->
            if (items.isEmpty()) {
                // Carrito vacío con diseño mejorado
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(24.dp),
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
                                Icons.Default.ShoppingCart,
                                contentDescription = null,
                                modifier = Modifier.size(70.dp),
                                tint = StrawberryRed.copy(alpha = 0.6f)
                            )
                        }
                    }
                    Spacer(Modifier.height(24.dp))
                    Text(
                        "Tu carrito está vacío",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = ChocolateBrown
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "Agrega productos desde la tienda",
                        style = MaterialTheme.typography.bodyLarge,
                        color = ChocolateBrown.copy(alpha = 0.6f),
                        textAlign = TextAlign.Center
                    )
                    Spacer(Modifier.height(24.dp))
                    Button(
                        onClick = { /* Navegar a home */ },
                        modifier = Modifier
                            .fillMaxWidth(0.7f)
                            .height(50.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = StrawberryRed),
                        shape = RoundedCornerShape(25.dp)
                    ) {
                        Icon(Icons.Default.Cake, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("Ver Productos", fontWeight = FontWeight.Bold)
                    }
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                ) {
                    // Lista de productos
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(items) { item ->
                            AnimatedVisibility(
                                visible = true,
                                enter = slideInHorizontally() + fadeIn(),
                                exit = slideOutHorizontally() + fadeOut()
                            ) {
                                CarritoItemCard(
                                    item = item,
                                    onIncrement = { vm.agregarProducto(item.productoId) },
                                    onDecrement = { vm.decrementarProducto(item.productoId) },
                                    onRemove = { vm.removerProducto(item.productoId) }
                                )
                            }
                        }
                    }

                    // Total y botón de pagar
                    AnimatedVisibility(
                        visible = items.isNotEmpty(),
                        enter = slideInHorizontally { it } + fadeIn(),
                        exit = slideOutHorizontally { it } + fadeOut()
                    ) {
                        Surface(
                            shadowElevation = 8.dp,
                            color = MaterialTheme.colorScheme.surface
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(20.dp)
                            ) {
                                // Campo de observaciones mejorado
                                OutlinedTextField(
                                    value = uiState.observaciones,
                                    onValueChange = vm::setObservaciones,
                                    label = { Text("Observaciones (opcional)", color = ChocolateBrown) },
                                    placeholder = { Text("Ej: Sin nueces, para recoger a las 15:00") },
                                    modifier = Modifier.fillMaxWidth(),
                                    maxLines = 2,
                                    shape = RoundedCornerShape(16.dp),
                                    leadingIcon = {
                                        Icon(Icons.Default.EditNote, contentDescription = null, tint = CaramelGold)
                                    },
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = StrawberryRed,
                                        unfocusedBorderColor = ChocolateBrown.copy(alpha = 0.3f)
                                    )
                                )

                                Spacer(Modifier.height(16.dp))

                                // Total mejorado con card
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(
                                        containerColor = GradientPink.copy(alpha = 0.15f)
                                    ),
                                    shape = RoundedCornerShape(16.dp)
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Text(
                                                "Total a pagar",
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = ChocolateBrown.copy(alpha = 0.7f)
                                            )
                                            Text(
                                                "$${String.format(Locale.getDefault(), "%.0f", total)}",
                                                style = MaterialTheme.typography.headlineMedium,
                                                fontWeight = FontWeight.ExtraBold,
                                                color = StrawberryRed
                                            )
                                        }
                                        Icon(
                                            Icons.Default.LocalOffer,
                                            contentDescription = null,
                                            tint = CaramelGold,
                                            modifier = Modifier.size(32.dp)
                                        )
                                    }
                                }

                                Spacer(Modifier.height(16.dp))

                                // Mensaje de error si existe
                                if (uiState.error != null) {
                                    Card(
                                        colors = CardDefaults.cardColors(
                                            containerColor = MaterialTheme.colorScheme.errorContainer
                                        ),
                                        shape = RoundedCornerShape(12.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(12.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                Icons.Default.Error,
                                                contentDescription = null,
                                                tint = MaterialTheme.colorScheme.error
                                            )
                                            Spacer(Modifier.width(8.dp))
                                            Text(
                                                uiState.error!!,
                                                color = MaterialTheme.colorScheme.error,
                                                style = MaterialTheme.typography.bodySmall
                                            )
                                        }
                                    }
                                    Spacer(Modifier.height(12.dp))
                                }

                                // Botón finalizar mejorado
                                Button(
                                    onClick = { mostrarDialogoConfirmacion = true },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(56.dp)
                                        .shadow(6.dp, RoundedCornerShape(16.dp)),
                                    shape = RoundedCornerShape(16.dp),
                                    enabled = !uiState.procesandoPedido,
                                    colors = ButtonDefaults.buttonColors(containerColor = StrawberryRed)
                                ) {
                                    if (uiState.procesandoPedido) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(24.dp),
                                            color = Color.White,
                                            strokeWidth = 2.dp
                                        )
                                        Spacer(Modifier.width(12.dp))
                                        Text("Procesando...", fontWeight = FontWeight.Bold)
                                    } else {
                                        Icon(Icons.Default.CheckCircle, contentDescription = null)
                                        Spacer(Modifier.width(12.dp))
                                        Text("Finalizar Pedido", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                    }
                                }

                                Spacer(Modifier.height(12.dp))

                                // Botón vaciar carrito mejorado
                                OutlinedButton(
                                    onClick = { vm.vaciarCarrito() },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(48.dp),
                                    shape = RoundedCornerShape(16.dp),
                                    enabled = !uiState.procesandoPedido,
                                    border = BorderStroke(1.5.dp, ChocolateBrown.copy(alpha = 0.3f))
                                ) {
                                    Icon(Icons.Default.DeleteOutline, contentDescription = null, tint = ChocolateBrown)
                                    Spacer(Modifier.width(8.dp))
                                    Text("Vaciar Carrito", color = ChocolateBrown)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CarritoItemCard(
    item: CarritoItem,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit,
    onRemove: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Imagen del producto
            Card(
                modifier = Modifier
                    .size(80.dp)
                    .aspectRatio(1f),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = GradientPink.copy(alpha = 0.1f))
            ) {
                AsyncImage(
                    model = item.imagen,
                    contentDescription = item.nombre,
                    modifier = Modifier.fillMaxSize()
                )
            }

            Spacer(Modifier.width(12.dp))

            // Información del producto
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = item.nombre,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = ChocolateBrown
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "$${String.format(Locale.getDefault(), "%.0f", item.precio)}",
                    style = MaterialTheme.typography.bodyLarge,
                    color = StrawberryRed,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(Modifier.height(8.dp))

                // Controles de cantidad
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    IconButton(
                        onClick = onDecrement,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            Icons.Default.Remove,
                            contentDescription = "Decrementar",
                            tint = StrawberryRed
                        )
                    }

                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = GradientPink.copy(alpha = 0.2f)
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "${item.cantidad}",
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                            fontWeight = FontWeight.Bold,
                            color = ChocolateBrown
                        )
                    }

                    IconButton(
                        onClick = onIncrement,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            Icons.Default.Add,
                            contentDescription = "Incrementar",
                            tint = StrawberryRed
                        )
                    }
                }
            }

            // Botón eliminar
            IconButton(
                onClick = onRemove,
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Eliminar",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

