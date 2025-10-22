package cl.duoc.milsabores.ui.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInVertically
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Login
import androidx.compose.material.icons.filled.Cake
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cl.duoc.milsabores.R
import cl.duoc.milsabores.ui.home.components.AnimatedLogo
import cl.duoc.milsabores.ui.theme.CaramelGold
import cl.duoc.milsabores.ui.theme.ChocolateBrown
import cl.duoc.milsabores.ui.theme.GradientOrange
import cl.duoc.milsabores.ui.theme.GradientPink
import cl.duoc.milsabores.ui.theme.GradientPurple
import cl.duoc.milsabores.ui.theme.PastelPeach
import cl.duoc.milsabores.ui.theme.PastelPink
import cl.duoc.milsabores.ui.theme.StrawberryRed
import cl.duoc.milsabores.ui.theme.VanillaWhite
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onLoginClick: () -> Unit,
    onRegisterClick: () -> Unit,
    onRecoverClick: () -> Unit,
) {
    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(300)
        visible = true
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        VanillaWhite,
                        GradientPink,
                        GradientOrange,
                        PastelPeach
                    )
                )
            )
    ) {
        // Decoración de fondo con círculos animados
        AnimatedBackgroundCircles()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Logo con animación de entrada
            AnimatedVisibility(
                visible = visible,
                enter = fadeIn(animationSpec = tween(800)) +
                        scaleIn(initialScale = 0.8f, animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessLow
                        ))
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(bottom = 32.dp)
                ) {
                    AnimatedLogo(
                        modifier = Modifier
                            .size(180.dp)
                            .shadow(16.dp, RoundedCornerShape(24.dp)),
                        logoRes = R.drawable.logo
                    )

                    Spacer(Modifier.height(24.dp))

                    // Título con efecto de brillo
                    Text(
                        "Pastelería Mil Sabores",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = ChocolateBrown,
                        textAlign = TextAlign.Center
                    )

                    Spacer(Modifier.height(8.dp))

                    Text(
                        "🍰 Delicia en cada bocado",
                        style = MaterialTheme.typography.titleMedium,
                        color = StrawberryRed,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(Modifier.height(8.dp))

                    Text(
                        "Pasteles artesanales hechos con amor",
                        style = MaterialTheme.typography.bodyLarge,
                        color = ChocolateBrown.copy(alpha = 0.7f),
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(Modifier.height(48.dp))

            // Botones con animación escalonada
            AnimatedVisibility(
                visible = visible,
                enter = fadeIn(animationSpec = tween(800, delayMillis = 200)) +
                        slideInVertically(initialOffsetY = { 100 })
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Botón de Iniciar Sesión - Principal
                    GradientButton(
                        text = "Iniciar Sesión",
                        icon = Icons.AutoMirrored.Filled.Login,
                        onClick = onLoginClick,
                        gradientColors = listOf(StrawberryRed, PastelPink),
                        isPrimary = true
                    )

                    // Botón de Registrarse - Secundario
                    GradientButton(
                        text = "Crear Cuenta",
                        icon = Icons.Default.PersonAdd,
                        onClick = onRegisterClick,
                        gradientColors = listOf(CaramelGold, PastelPeach),
                        isPrimary = false
                    )

                    // Botón de Recuperar Contraseña - Terciario
                    TextButton(
                        onClick = onRecoverClick,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                    ) {
                        Text(
                            "¿Olvidaste tu contraseña?",
                            color = ChocolateBrown,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            Spacer(Modifier.weight(1f))

            // Insignia decorativa
            AnimatedVisibility(
                visible = visible,
                enter = fadeIn(animationSpec = tween(800, delayMillis = 400))
            ) {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(16.dp)
                ) {
                    Icon(
                        Icons.Default.Cake,
                        contentDescription = null,
                        tint = StrawberryRed,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        "Desde 2020 endulzando tus momentos",
                        style = MaterialTheme.typography.bodySmall,
                        color = ChocolateBrown.copy(alpha = 0.6f)
                    )
                }
            }
        }
    }
}

@Composable
private fun GradientButton(
    text: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit,
    gradientColors: List<Color>,
    isPrimary: Boolean,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "scale"
    )

    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .shadow(
                elevation = if (isPrimary) 8.dp else 4.dp,
                shape = RoundedCornerShape(16.dp),
                spotColor = gradientColors.first().copy(alpha = 0.4f)
            )
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            },
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent
        ),
        contentPadding = PaddingValues(0.dp),
        interactionSource = interactionSource
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.horizontalGradient(gradientColors)
                ),
            contentAlignment = Alignment.Center
        ) {
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    icon,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(Modifier.width(12.dp))
                Text(
                    text,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
private fun AnimatedBackgroundCircles() {
    val infiniteTransition = rememberInfiniteTransition(label = "circles")

    val offset1 by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(20000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "offset1"
    )

    Box(modifier = Modifier.fillMaxSize()) {
        // Círculo 1
        Box(
            modifier = Modifier
                .size(200.dp)
                .offset(x = (-100).dp, y = (-100).dp)
                .background(
                    GradientPurple.copy(alpha = 0.1f),
                    shape = androidx.compose.foundation.shape.CircleShape
                )
                .graphicsLayer { rotationZ = offset1 }
        )

        // Círculo 2
        Box(
            modifier = Modifier
                .size(150.dp)
                .align(Alignment.TopEnd)
                .offset(x = 50.dp, y = (-50).dp)
                .background(
                    PastelPink.copy(alpha = 0.1f),
                    shape = androidx.compose.foundation.shape.CircleShape
                )
                .graphicsLayer { rotationZ = -offset1 }
        )

        // Círculo 3
        Box(
            modifier = Modifier
                .size(180.dp)
                .align(Alignment.BottomStart)
                .offset(x = (-60).dp, y = 60.dp)
                .background(
                    CaramelGold.copy(alpha = 0.1f),
                    shape = androidx.compose.foundation.shape.CircleShape
                )
                .graphicsLayer { rotationZ = offset1 * 0.5f }
        )
    }
}

