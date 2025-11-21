package cl.duoc.milsabores.ui.principal.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cl.duoc.milsabores.ui.model.Producto
import cl.duoc.milsabores.ui.theme.CaramelGold
import cl.duoc.milsabores.ui.theme.ChocolateBrown
import cl.duoc.milsabores.ui.theme.GradientOrange
import cl.duoc.milsabores.ui.theme.GradientPink
import cl.duoc.milsabores.ui.theme.MintGreen
import cl.duoc.milsabores.ui.theme.PastelPink
import cl.duoc.milsabores.ui.theme.StrawberryRed
import kotlinx.coroutines.delay

@Composable
fun UiProductosCard(
    producto: Producto,
    onAgregar: () -> Unit,
    isNuevo: Boolean = false // ← ahora el pulso “nuevo” es real y opcional
) {
    var agregado by remember { mutableStateOf(false) }
    val interactionSource = remember { MutableInteractionSource() }
    val presionado by interactionSource.collectIsPressedAsState()

    // Animaciones de escala / elevación
    val escala by animateFloatAsState(
        targetValue = if (presionado) 0.97f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "scale"
    )

    val elevacion by animateFloatAsState(
        targetValue = if (presionado) 2f else 6f,
        animationSpec = tween(180),
        label = "elevation"
    )

    // Efecto sutil de inclinación al agregar (con cámara en px para evitar distorsión)
    val rotacionY by animateFloatAsState(
        targetValue = if (agregado) 5f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "rotation"
    )
    val density = LocalDensity.current
    val cameraDistancePx = with(density) { 16.dp.toPx() }

    // Pulso sólo si el producto es “nuevo” y aún no se agrega
    val infinite = rememberInfiniteTransition(label = "pulse")
    val shouldPulse = isNuevo && !agregado
    val pulso = if (shouldPulse) {
        infinite.animateFloat(
            initialValue = 1f,
            targetValue = 1.06f,
            animationSpec = infiniteRepeatable(
                animation = tween(1000, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "pulse"
        ).value
    } else 1f

    // Feedback temporal “Agregado”
    LaunchedEffect(agregado) {
        if (agregado) {
            delay(1200)
            agregado = false
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(320.dp)
            .graphicsLayer {
                scaleX = escala
                scaleY = escala
                rotationY = rotacionY
                cameraDistance = cameraDistancePx
            },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = elevacion.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(10.dp)
            ) {
                // Imagen del producto con zoom sutil al presionar
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    GradientPink,
                                    GradientOrange.copy(alpha = 0.3f)
                                )
                            )
                        )
                ) {
                    Image(
                        painter = painterResource(producto.imagenRes),
                        contentDescription = producto.titulo,
                        modifier = Modifier
                            .fillMaxSize()
                            .graphicsLayer {
                                val zoom = if (presionado) 1.05f else 1f
                                scaleX = zoom
                                scaleY = zoom
                            },
                        contentScale = ContentScale.Crop
                    )

                    // Badge de categoría con gradiente
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(6.dp)
                            .background(
                                brush = Brush.horizontalGradient(
                                    colors = listOf(StrawberryRed, PastelPink)
                                ),
                                shape = RoundedCornerShape(10.dp)
                            )
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = producto.categoria,
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp
                        )
                    }
                }

                Spacer(Modifier.height(10.dp))

                // Título animado
                AnimatedContent(
                    targetState = producto.titulo,
                    transitionSpec = { fadeIn() + slideInVertically() togetherWith fadeOut() },
                    label = "title"
                ) { titulo ->
                    Text(
                        text = titulo,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        color = ChocolateBrown,
                        fontSize = 13.sp
                    )
                }

                Spacer(Modifier.height(4.dp))

                // Precio (String en modelo actual)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = producto.precio,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = StrawberryRed,
                        fontSize = 16.sp
                    )
                }

                Spacer(Modifier.weight(1f))

                // Botón accesible + transición “agregado”
                AnimatedContent(
                    targetState = agregado,
                    transitionSpec = {
                        fadeIn(animationSpec = tween(300)) + scaleIn(initialScale = 0.8f) togetherWith
                                fadeOut(animationSpec = tween(300)) + scaleOut(targetScale = 0.8f)
                    },
                    label = "button"
                ) { isAgregado ->
                    Button(
                        onClick = {
                            agregado = true
                            onAgregar()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(40.dp)
                            .semantics {
                                role = Role.Button
                                contentDescription = if (isAgregado)
                                    "Producto agregado" else "Agregar ${producto.titulo} al carrito"
                            },
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isAgregado) MintGreen else StrawberryRed
                        ),
                        interactionSource = interactionSource,
                        elevation = ButtonDefaults.buttonElevation(
                            defaultElevation = 4.dp,
                            pressedElevation = 2.dp
                        ),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = if (isAgregado) Icons.Default.Check else Icons.Default.ShoppingCart,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(Modifier.width(6.dp))
                            Text(
                                text = if (isAgregado) "✓ Agregado" else "Agregar",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }

            // Indicador de “nuevo” con pulso (solo si isNuevo)
            if (shouldPulse) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(8.dp)
                        .graphicsLayer {
                            scaleX = pulso
                            scaleY = pulso
                        }
                        .background(CaramelGold, CircleShape)
                        .size(8.dp)
                        .semantics { contentDescription = "Producto nuevo" }
                )
            }
        }
    }
}
