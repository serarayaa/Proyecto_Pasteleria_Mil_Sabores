package cl.duoc.milsabores.ui.home.components
// o si lo guardaste en principal/components:
// package cl.duoc.milsabores.ui.principal.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Surface
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import cl.duoc.milsabores.R

@Composable
fun AnimatedLogo(
    modifier: Modifier = Modifier,
    logoRes: Int = R.drawable.logo,
    backgroundGradient: List<Color> = listOf(
        Color(0xFFFFE0B2),
        Color(0xFFFFCC80),
        Color(0xFFFFB74D)
    )
) {
    var visible by remember { mutableStateOf(false) }

    // Renombramos para evitar choque con graphicsLayer.alpha
    val logoScale by animateFloatAsState(
        targetValue = if (visible) 1f else 0.6f,
        animationSpec = spring(stiffness = Spring.StiffnessLow),
        label = "scale"
    )
    val logoAlpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(800, easing = FastOutSlowInEasing),
        label = "alpha"
    )

    val infinite = rememberInfiniteTransition(label = "logoInfinite")
    val pulse by infinite.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )
    val rotation by infinite.animateFloat(
        initialValue = -2f,
        targetValue = 2f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "rotation"
    )

    LaunchedEffect(Unit) { visible = true }

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            modifier = Modifier
                .size(220.dp)
                .clip(CircleShape),
            color = Color.Transparent
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        scaleX = pulse
                        scaleY = pulse
                        rotationZ = rotation
                    }
            ) {
                // Capa de fondo con degradado y alpha fijo
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .graphicsLayer { this.alpha = 0.6f } // ← clave
                        .background(Brush.radialGradient(backgroundGradient))
                )

                // Logo animado (usa las variables renombradas)
                Image(
                    painter = painterResource(id = logoRes),
                    contentDescription = "Logo Pastelería Mil Sabores",
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer {
                            scaleX = logoScale
                            scaleY = logoScale
                            this.alpha = logoAlpha // ← clave
                        },
                    contentScale = ContentScale.Fit
                )
            }
        }
    }
}
