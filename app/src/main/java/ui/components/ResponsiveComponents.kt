package cl.duoc.milsabores.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import cl.duoc.milsabores.ui.theme.ChocolateBrown
import cl.duoc.milsabores.ui.theme.StrawberryRed

/**
 * Componente de card para mostrar elementos con información consistente
 */
@Composable
fun ResponsiveCard(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        )
    ) {
        content()
    }
}

/**
 * Componente para mostrar mensajes de estado (éxito, error, etc.)
 */
@Composable
fun StatusMessage(
    message: String,
    type: StatusType = StatusType.INFO,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null
) {
    val backgroundColor = when (type) {
        StatusType.SUCCESS -> Color(0xFFD4EDDA)
        StatusType.ERROR -> MaterialTheme.colorScheme.errorContainer
        StatusType.WARNING -> Color(0xFFFFF3CD)
        StatusType.INFO -> Color(0xFFD1ECF1)
    }

    val textColor = when (type) {
        StatusType.SUCCESS -> Color(0xFF155724)
        StatusType.ERROR -> MaterialTheme.colorScheme.error
        StatusType.WARNING -> Color(0xFF856404)
        StatusType.INFO -> Color(0xFF0C5460)
    }

    val defaultIcon = when (type) {
        StatusType.SUCCESS -> Icons.Default.CheckCircle
        StatusType.ERROR -> Icons.Default.Close
        StatusType.WARNING -> Icons.Default.Close
        StatusType.INFO -> Icons.Default.Close
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = icon ?: defaultIcon,
                contentDescription = null,
                tint = textColor,
                modifier = Modifier.size(24.dp)
            )
            Text(
                message,
                color = textColor,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

enum class StatusType {
    SUCCESS, ERROR, WARNING, INFO
}

/**
 * Componente para mostrar información en dos columnas
 */
@Composable
fun InfoRow(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            label,
            style = MaterialTheme.typography.bodyMedium,
            color = ChocolateBrown.copy(alpha = 0.7f)
        )
        Text(
            value,
            style = MaterialTheme.typography.bodyMedium,
            color = ChocolateBrown,
            modifier = Modifier.weight(1f),
            textAlign = androidx.compose.ui.text.style.TextAlign.End
        )
    }
}

/**
 * Botón responsivo que se adapta a diferentes tamaños de pantalla
 */
@Composable
fun ResponsiveButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isLoading: Boolean = false,
    icon: ImageVector? = null
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp),
        enabled = enabled && !isLoading,
        colors = ButtonDefaults.buttonColors(
            containerColor = StrawberryRed,
            disabledContainerColor = StrawberryRed.copy(alpha = 0.5f)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(20.dp),
                color = Color.White,
                strokeWidth = 2.dp
            )
            Spacer(Modifier.width(8.dp))
            Text("Procesando...")
        } else {
            if (icon != null) {
                Icon(icon, contentDescription = null)
                Spacer(Modifier.width(8.dp))
            }
            Text(text)
        }
    }
}

/**
 * Contenedor responsivo que se adapta a diferentes resoluciones
 */
@Composable
fun ResponsiveContainer(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        content()
    }
}

