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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import cl.duoc.milsabores.ui.model.Producto
import cl.duoc.milsabores.ui.theme.CaramelGold
import cl.duoc.milsabores.ui.theme.ChocolateBrown
import cl.duoc.milsabores.ui.theme.GradientOrange
import cl.duoc.milsabores.ui.theme.GradientPink
import cl.duoc.milsabores.ui.theme.MintGreen
import cl.duoc.milsabores.ui.theme.PastelPink
import cl.duoc.milsabores.ui.theme.StrawberryRed

@Composable
fun UiProductosCard(
    producto: Producto,
    onAgregar: () -> Unit
) {
    var agregado by remember { mutableStateOf(false) }
    val interactionSource = remember { MutableInteractionSource() }
    val presionado by interactionSource.collectIsPressedAsState()

    // Animaciones avanzadas
    val escala by animateFloatAsState(
        targetValue = if (presionado) 0.95f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "scale"
    )

    val elevacion by animateFloatAsState(
        targetValue = if (presionado) 2f else 6f,
        animationSpec = tween(200),
        label = "elevation"
    )

    val rotacionY by animateFloatAsState(
        targetValue = if (agregado) 5f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "rotation"
    )

    // Animación de pulso para productos nuevos
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulso by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(340.dp)
            .graphicsLayer {
                scaleX = escala
                scaleY = escala
                rotationY = rotacionY
            }
            .shadow(
                elevation = elevacion.dp,
                shape = RoundedCornerShape(20.dp),
                spotColor = StrawberryRed.copy(alpha = 0.3f),
                ambientColor = PastelPink.copy(alpha = 0.2f)
            ),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp)
            ) {
                // Imagen del producto con efecto de zoom
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp)
                        .clip(RoundedCornerShape(16.dp))
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
                                scaleX = if (presionado) 1.1f else 1f
                                scaleY = if (presionado) 1.1f else 1f
                            },
                        contentScale = ContentScale.Crop
                    )

                    // Badge de categoría con gradiente
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(8.dp)
                            .background(
                                brush = Brush.horizontalGradient(
                                    colors = listOf(
                                        StrawberryRed,
                                        PastelPink
                                    )
                                ),
                                shape = RoundedCornerShape(12.dp)
                            )
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = producto.categoria,
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(Modifier.height(12.dp))

                // Título del producto con animación
                AnimatedContent(
                    targetState = producto.titulo,
                    transitionSpec = {
                        fadeIn() + slideInVertically() togetherWith fadeOut()
                    },
                    label = "title"
                ) { titulo ->
                    Text(
                        text = titulo,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        color = ChocolateBrown
                    )
                }

                Spacer(Modifier.height(4.dp))

                // Precio con efecto de brillo
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = producto.precio,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color = StrawberryRed,
                        modifier = Modifier.graphicsLayer {
                            scaleX = if (presionado) pulso else 1f
                            scaleY = if (presionado) pulso else 1f
                        }
                    )
                }

                Spacer(Modifier.weight(1f))

                // Botón animado con transición suave
                AnimatedContent(
                    targetState = agregado,
                    transitionSpec = {
                        fadeIn(animationSpec = tween(300)) +
                        scaleIn(initialScale = 0.8f) togetherWith
                        fadeOut(animationSpec = tween(300)) +
                        scaleOut(targetScale = 0.8f)
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
                            .height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isAgregado) MintGreen else StrawberryRed
                        ),
                        interactionSource = interactionSource,
                        elevation = ButtonDefaults.buttonElevation(
                            defaultElevation = 4.dp,
                            pressedElevation = 2.dp
                        )
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = if (isAgregado) Icons.Default.Check else Icons.Default.ShoppingCart,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = if (isAgregado) "✓ Agregado" else "Agregar",
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // Efecto de "nuevo" con animación de pulso
            if (!agregado) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(8.dp)
                        .graphicsLayer {
                            scaleX = pulso
                            scaleY = pulso
                        }
                        .background(
                            CaramelGold,
                            CircleShape
                        )
                        .size(8.dp)
                )
            }
        }
    }
}

