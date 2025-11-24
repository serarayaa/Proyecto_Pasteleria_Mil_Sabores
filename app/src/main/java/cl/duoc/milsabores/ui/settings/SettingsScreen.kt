package cl.duoc.milsabores.ui.settings

import cl.duoc.milsabores.data.local.Prefs
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cl.duoc.milsabores.ui.theme.CaramelGold
import cl.duoc.milsabores.ui.theme.ChocolateBrown
import cl.duoc.milsabores.ui.theme.GradientPink
import cl.duoc.milsabores.ui.theme.PastelPeach
import cl.duoc.milsabores.ui.theme.PastelPink
import cl.duoc.milsabores.ui.theme.StrawberryRed
import cl.duoc.milsabores.ui.theme.VanillaWhite

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(onModoOscuroChanged: (Boolean) -> Unit = {}) {
    val ctx = LocalContext.current
    val prefs = remember { Prefs(ctx) }
    var modoOscuro by remember { mutableStateOf(prefs.darkMode) }

    // Cargar preferencia guardada
    LaunchedEffect(Unit) {
        onModoOscuroChanged(prefs.darkMode)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Configuración", fontWeight = FontWeight.Bold)
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            VanillaWhite,
                            GradientPink.copy(alpha = 0.2f),
                            PastelPeach.copy(alpha = 0.1f)
                        )
                    )
                )
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Sección: Apariencia
            Text(
                "APARIENCIA",
                style = MaterialTheme.typography.labelMedium,
                color = ChocolateBrown.copy(alpha = 0.6f),
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 8.dp, top = 8.dp)
            )

            // Tarjeta Modo Oscuro
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(4.dp, RoundedCornerShape(16.dp))
                    .clickable {
                        modoOscuro = !modoOscuro
                        prefs.darkMode = modoOscuro
                        onModoOscuroChanged(modoOscuro)
                    },
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (modoOscuro)
                        ChocolateBrown.copy(alpha = 0.1f)
                    else
                        PastelPink.copy(alpha = 0.3f)
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            if (modoOscuro) Icons.Filled.DarkMode else Icons.Filled.LightMode,
                            contentDescription = null,
                            tint = if (modoOscuro) ChocolateBrown else CaramelGold,
                            modifier = Modifier.size(24.dp)
                        )
                        Column {
                            Text(
                                "Modo Oscuro",
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Bold,
                                color = ChocolateBrown
                            )
                            Text(
                                if (modoOscuro) "Activado" else "Desactivado",
                                style = MaterialTheme.typography.bodySmall,
                                color = ChocolateBrown.copy(alpha = 0.6f)
                            )
                        }
                    }

                    Switch(
                        checked = modoOscuro,
                        onCheckedChange = {
                            modoOscuro = it
                            prefs.darkMode = it
                            onModoOscuroChanged(it)
                        },
                        modifier = Modifier.scale(1.2f)
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            // Sección: Información
            Text(
                "INFORMACIÓN",
                style = MaterialTheme.typography.labelMedium,
                color = ChocolateBrown.copy(alpha = 0.6f),
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 8.dp, top = 8.dp)
            )

            InfoCard(
                title = "Versión",
                subtitle = "2.2.0",
                icon = "📱"
            )

            InfoCard(
                title = "Desarrollador",
                subtitle = "Mil Sabores Team",
                icon = "👨‍💻"
            )

            Spacer(Modifier.height(16.dp))

            // Botón de ayuda
            Button(
                onClick = { },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(containerColor = StrawberryRed),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Centro de Ayuda", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun InfoCard(
    title: String,
    subtitle: String,
    icon: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(2.dp, RoundedCornerShape(12.dp)),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = PastelPink.copy(alpha = 0.2f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(icon, modifier = Modifier.size(32.dp))
            Column(Modifier.weight(1f)) {
                Text(
                    title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = ChocolateBrown
                )
                Text(
                    subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = ChocolateBrown.copy(alpha = 0.6f)
                )
            }
        }
    }
}
