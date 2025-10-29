package cl.duoc.milsabores.ui.carrito

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material.icons.filled.Cake
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
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
import cl.duoc.milsabores.ui.theme.CaramelGold
import cl.duoc.milsabores.ui.theme.ChocolateBrown
import cl.duoc.milsabores.ui.theme.GradientPink
import cl.duoc.milsabores.ui.theme.PastelPeach
import cl.duoc.milsabores.ui.theme.StrawberryRed
import cl.duoc.milsabores.ui.theme.VanillaWhite
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CarritoScreen(
    vm: CarritoViewModel,
    onPedidoCreado: () -> Unit = {}
) {
    val cartItemsState = vm.items.collectAsState()
    val cartItems = cartItemsState.value
    val total = vm.total.collectAsState().value
    val ui = vm.uiState.collectAsState().value

    var mostrarDialogoConfirmacion by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(ui.pedidoCreado) {
        if (ui.pedidoCreado) {
            vm.resetPedidoCreado()
            onPedidoCreado()
        }
    }

    if (mostrarDialogoConfirmacion) {
        AlertDialog(
            onDismissRequest = { mostrarDialogoConfirmacion = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.ShoppingCart, contentDescription = null, tint = StrawberryRed, modifier = Modifier.size(28.dp))
                    Spacer(Modifier.width(12.dp))
                    Text("Confirmar Pedido", fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column {
                    Text("¿Deseas confirmar tu pedido por un total de:")
                    Spacer(Modifier.height(12.dp))
                    Card(
                        colors = CardDefaults.cardColors(containerColor = GradientPink.copy(alpha = 0.2f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = formatCLP(total),
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
                    enabled = !ui.procesandoPedido,
                    colors = ButtonDefaults.buttonColors(containerColor = StrawberryRed),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    if (ui.procesandoPedido) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White, strokeWidth = 2.dp)
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
                    enabled = !ui.procesandoPedido,
                    shape = RoundedCornerShape(12.dp)
                ) { Text("Cancelar") }
            }
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(VanillaWhite, GradientPink.copy(alpha = 0.15f), PastelPeach.copy(alpha = 0.1f))
                )
            )
    ) {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.ShoppingCart, contentDescription = null, tint = StrawberryRed, modifier = Modifier.size(28.dp))
                            Spacer(Modifier.width(8.dp))
                            Text("Carrito de Compras", fontWeight = FontWeight.Bold)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
                )
            }
        ) { padding ->
            if (cartItems.isEmpty()) {
                EmptyCart(padding)
            } else {
                Column(modifier = Modifier.fillMaxSize().padding(padding)) {
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(cartItems, key = { it.productoId }) { item ->
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

                    AnimatedVisibility(
                        visible = cartItems.isNotEmpty(),
                        enter = slideInHorizontally { it } + fadeIn(),
                        exit = slideOutHorizontally { it } + fadeOut()
                    ) {
                        Surface(shadowElevation = 8.dp, color = MaterialTheme.colorScheme.surface) {
                            Column(modifier = Modifier.fillMaxWidth().padding(20.dp)) {

                                OutlinedTextField(
                                    value = ui.observaciones,
                                    onValueChange = vm::setObservaciones,
                                    label = { Text("Observaciones (opcional)", color = ChocolateBrown) },
                                    placeholder = { Text("Ej: Sin nueces, para retirar a las 15:00") },
                                    modifier = Modifier.fillMaxWidth(),
                                    maxLines = 2,
                                    shape = RoundedCornerShape(16.dp),
                                    leadingIcon = { Icon(Icons.Default.EditNote, contentDescription = null, tint = CaramelGold) },
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = StrawberryRed,
                                        unfocusedBorderColor = ChocolateBrown.copy(alpha = 0.3f)
                                    )
                                )

                                Spacer(Modifier.height(16.dp))

                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(containerColor = GradientPink.copy(alpha = 0.15f)),
                                    shape = RoundedCornerShape(16.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Text("Total a pagar", style = MaterialTheme.typography.bodyMedium, color = ChocolateBrown.copy(alpha = 0.7f))
                                            Text(
                                                formatCLP(total),
                                                style = MaterialTheme.typography.headlineMedium,
                                                fontWeight = FontWeight.ExtraBold,
                                                color = StrawberryRed
                                            )
                                        }
                                        Icon(Icons.Default.LocalOffer, contentDescription = null, tint = CaramelGold, modifier = Modifier.size(32.dp))
                                    }
                                }

                                Spacer(Modifier.height(16.dp))

                                if (ui.error != null) {
                                    Card(
                                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                                        shape = RoundedCornerShape(12.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth().padding(12.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(Icons.Default.Error, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                                            Spacer(Modifier.width(8.dp))
                                            Text(ui.error!!, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                                        }
                                    }
                                    Spacer(Modifier.height(12.dp))
                                }

                                Button(
                                    onClick = { mostrarDialogoConfirmacion = true },
                                    modifier = Modifier.fillMaxWidth().height(56.dp).shadow(6.dp, RoundedCornerShape(16.dp)),
                                    shape = RoundedCornerShape(16.dp),
                                    enabled = !ui.procesandoPedido,
                                    colors = ButtonDefaults.buttonColors(containerColor = StrawberryRed)
                                ) {
                                    if (ui.procesandoPedido) {
                                        CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White, strokeWidth = 2.dp)
                                        Spacer(Modifier.width(12.dp))
                                        Text("Procesando...", fontWeight = FontWeight.Bold)
                                    } else {
                                        Icon(Icons.Default.CheckCircle, contentDescription = null)
                                        Spacer(Modifier.width(12.dp))
                                        Text("Finalizar Pedido", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                    }
                                }

                                Spacer(Modifier.height(12.dp))

                                OutlinedButton(
                                    onClick = { vm.vaciarCarrito() },
                                    modifier = Modifier.fillMaxWidth().height(48.dp),
                                    shape = RoundedCornerShape(16.dp),
                                    enabled = !ui.procesandoPedido,
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
private fun EmptyCart(padding: PaddingValues) {
    Column(
        modifier = Modifier.fillMaxSize().padding(padding).padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Card(
            modifier = Modifier.size(140.dp).shadow(12.dp, CircleShape),
            shape = CircleShape,
            colors = CardDefaults.cardColors(containerColor = GradientPink.copy(alpha = 0.2f))
        ) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Icon(Icons.Default.ShoppingCart, contentDescription = null, modifier = Modifier.size(70.dp), tint = StrawberryRed.copy(alpha = 0.6f))
            }
        }
        Spacer(Modifier.height(24.dp))
        Text("Tu carrito está vacío", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = ChocolateBrown)
        Spacer(Modifier.height(8.dp))
        Text("Agrega productos desde la tienda", style = MaterialTheme.typography.bodyLarge, color = ChocolateBrown.copy(alpha = 0.6f), textAlign = TextAlign.Center)
        Spacer(Modifier.height(24.dp))
        Button(
            onClick = { /* TODO: navegar a Home */ },
            modifier = Modifier.fillMaxWidth(0.7f).height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = StrawberryRed),
            shape = RoundedCornerShape(25.dp)
        ) {
            Icon(Icons.Default.Cake, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text("Ver Productos", fontWeight = FontWeight.Bold)
        }
    }
}

private fun formatCLP(value: Double): String {
    val nf = NumberFormat.getCurrencyInstance(Locale("es", "CL"))
    // El símbolo CLP suele ser $; NumberFormat devuelve "$ 1.234"
    return nf.format(value).replace(",00", "") // sin decimales para precios enteros
}
