package cl.duoc.milsabores.ui.profile

import android.Manifest
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.duoc.milsabores.core.AppLogger
import cl.duoc.milsabores.ui.theme.CaramelGold
import cl.duoc.milsabores.ui.theme.ChocolateBrown
import cl.duoc.milsabores.ui.theme.GradientOrange
import cl.duoc.milsabores.ui.theme.GradientPink
import cl.duoc.milsabores.ui.theme.MintGreen
import cl.duoc.milsabores.ui.theme.PastelPeach
import cl.duoc.milsabores.ui.theme.PastelPink
import cl.duoc.milsabores.ui.theme.StrawberryRed
import cl.duoc.milsabores.ui.theme.VanillaWhite
import coil.compose.rememberAsyncImagePainter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(vm: ProfileViewModel = viewModel()) {
    val ui by vm.ui.collectAsState()
    val context = LocalContext.current

    // Verificar si es usuario invitado
    val isGuest = ui.uid == "guest" || ui.email == "invitado@milsabores.cl"

    // Si es invitado, mostrar pantalla restringida
    if (isGuest) {
        GuestRestrictedScreen()
        return
    }

    // Carga/recarga inicial de la foto desde almacenamiento local
    LaunchedEffect(Unit) {
        vm.refreshProfilePhoto(context)
    }

    var pendingUri by remember { mutableStateOf<android.net.Uri?>(null) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    // Verificar permiso de cámara al inicio
    val hasCameraPermission = remember {
        androidx.core.content.ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.CAMERA
        ) == android.content.pm.PackageManager.PERMISSION_GRANTED
    }
    var permissionGranted by remember { mutableStateOf(hasCameraPermission) }

    // 1) Tomar foto (declarado primero)
    val takePictureLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { ok ->
        try {
            if (ok && pendingUri != null) {
                val uri = pendingUri!!
                AppLogger.i("Foto capturada exitosamente, guardando...", "ProfileScreen")
                vm.saveProfilePhoto(context, uri)
                Toast.makeText(context, "Foto de perfil actualizada ✓", Toast.LENGTH_LONG).show()
            } else {
                AppLogger.w("Captura cancelada - ok=$ok, pendingUri=$pendingUri", "ProfileScreen")
                Toast.makeText(context, "Captura cancelada", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            AppLogger.e("Error al guardar foto", e, "ProfileScreen")
            Toast.makeText(context, "Error al guardar: ${e.message}", Toast.LENGTH_LONG).show()
        } finally {
            pendingUri = null
        }
    }

    // 2) Permiso de cámara
    val cameraPermLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        permissionGranted = granted
        if (!granted) {
            Toast.makeText(context, "Permiso de cámara requerido para tomar foto", Toast.LENGTH_SHORT).show()
        } else {
            // Permiso concedido, intentar lanzar cámara
            try {
                val dest = vm.createDestinationUriForCurrentUser(context)
                if (dest == null) {
                    Toast.makeText(context, "No se pudo crear destino para la foto", Toast.LENGTH_LONG).show()
                    AppLogger.e("createDestinationUriForCurrentUser devolvió null", null, "ProfileScreen")
                } else {
                    AppLogger.i("URI creada: $dest", "ProfileScreen")
                    pendingUri = dest
                    takePictureLauncher.launch(dest)
                }
            } catch (e: Exception) {
                AppLogger.e("Error creando URI", e, "ProfileScreen")
                Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    fun launchCameraFlow() {
        try {
            if (permissionGranted) {
                val dest = vm.createDestinationUriForCurrentUser(context)
                if (dest == null) {
                    Toast.makeText(context, "No se pudo crear destino para la foto", Toast.LENGTH_LONG).show()
                    AppLogger.e("createDestinationUriForCurrentUser devolvió null", null, "ProfileScreen")
                } else {
                    AppLogger.i("URI creada: $dest", "ProfileScreen")
                    pendingUri = dest
                    takePictureLauncher.launch(dest)
                }
            } else {
                // Solicitar permiso
                cameraPermLauncher.launch(Manifest.permission.CAMERA)
            }
        } catch (e: Exception) {
            AppLogger.e("Error en launchCameraFlow", e, "ProfileScreen")
            Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.AccountCircle,
                            contentDescription = null,
                            tint = StrawberryRed,
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text("Mi Perfil", fontWeight = FontWeight.Bold)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = GradientPink.copy(alpha = 0.3f)
                )
            )
        }
    ) { inner ->
        Column(
            Modifier
                .padding(inner)
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            VanillaWhite,
                            GradientPink.copy(alpha = 0.2f),
                            PastelPeach.copy(alpha = 0.1f)
                        )
                    )
                )
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // === FOTO DE PERFIL ===
            Card(
                modifier = Modifier
                    .size(160.dp)
                    .shadow(12.dp, CircleShape)
                    .clickable { launchCameraFlow() },
                shape = CircleShape,
                colors = CardDefaults.cardColors(
                    containerColor = PastelPeach.copy(alpha = 0.3f)
                )
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                if (ui.profilePhotoUri != null) {
                    Image(
                        painter = rememberAsyncImagePainter(ui.profilePhotoUri),
                        contentDescription = "Foto de perfil",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(
                        Icons.Default.AccountCircle,
                        contentDescription = "Sin foto",
                        modifier = Modifier.size(80.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                    // Overlay con icono de cámara
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .size(48.dp)
                            .shadow(8.dp, CircleShape)
                            .clip(CircleShape)
                            .background(
                                brush = Brush.horizontalGradient(
                                    colors = listOf(StrawberryRed, PastelPink)
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.CameraAlt,
                            contentDescription = "Cambiar foto",
                            tint = VanillaWhite
                        )
                    }
                }
            }

            Text(
                "Toca para cambiar foto",
                modifier = Modifier.padding(top = 4.dp),
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = ChocolateBrown.copy(alpha = 0.6f)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // === INFORMACIÓN DE USUARIO ===
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(4.dp, RoundedCornerShape(16.dp)),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                )
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    InfoRow("Nombre", ui.displayName ?: "No disponible")
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                    InfoRow("Correo", ui.email ?: "No disponible")
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                    InfoRow("ID Usuario", ui.uid ?: "No disponible")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // === BOTONES DE ACCIÓN ===
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { launchCameraFlow() },
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .shadow(4.dp, RoundedCornerShape(12.dp)),
                    enabled = !ui.isLoading,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = StrawberryRed
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.CameraAlt, "Cámara")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Tomar foto", fontWeight = FontWeight.Bold)
                }

                if (ui.profilePhotoUri != null) {
                    OutlinedButton(
                        onClick = { showDeleteDialog = true },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.error
                        ),
                        enabled = !ui.isLoading,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Delete, "Eliminar")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Eliminar", fontWeight = FontWeight.Bold)
                    }
                }
            }

            // === INDICADOR DE CARGA ===
            if (ui.isLoading) {
                Spacer(modifier = Modifier.height(16.dp))
                CircularProgressIndicator(
                    modifier = Modifier.size(32.dp),
                    color = StrawberryRed
                )
            }

            // === MENSAJES DE ERROR ===
            ui.error?.let {
                Spacer(modifier = Modifier.height(16.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Text(
                        it,
                        modifier = Modifier.padding(12.dp),
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 12.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    // === DIALOG DE CONFIRMACIÓN ===
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Eliminar foto") },
            text = { Text("¿Deseas eliminar tu foto de perfil?") },
            confirmButton = {
                Button(
                    onClick = {
                        vm.deleteProfilePhoto(context)
                        showDeleteDialog = false
                        Toast.makeText(context, "Foto eliminada", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) { Text("Eliminar") }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) { Text("Cancelar") }
            }
        )
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            label,
            fontSize = 12.sp,
            color = ChocolateBrown.copy(alpha = 0.6f),
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(4.dp))
        Text(
            value,
            fontSize = 14.sp,
            color = ChocolateBrown,
            fontWeight = FontWeight.Medium
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun GuestRestrictedScreen() {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.AccountCircle,
                            contentDescription = null,
                            tint = StrawberryRed,
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text("Mi Perfil", fontWeight = FontWeight.Bold)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = GradientPink.copy(alpha = 0.3f)
                )
            )
        }
    ) { inner ->
        Column(
            Modifier
                .padding(inner)
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            VanillaWhite,
                            GradientPink.copy(alpha = 0.2f),
                            PastelPeach.copy(alpha = 0.1f)
                        )
                    )
                )
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Icono grande de usuario invitado
            Card(
                modifier = Modifier
                    .size(140.dp)
                    .shadow(12.dp, CircleShape),
                shape = CircleShape,
                colors = CardDefaults.cardColors(
                    containerColor = PastelPink.copy(alpha = 0.5f)
                )
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.AccountCircle,
                        contentDescription = "Usuario Invitado",
                        modifier = Modifier.size(100.dp),
                        tint = ChocolateBrown.copy(alpha = 0.5f)
                    )
                }
            }

            Spacer(Modifier.height(32.dp))

            // Título
            Text(
                "Modo Invitado",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = ChocolateBrown,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(16.dp))

            // Mensaje informativo
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(4.dp, RoundedCornerShape(16.dp)),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = GradientOrange.copy(alpha = 0.2f)
                )
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        Icons.Default.Info,
                        contentDescription = null,
                        tint = CaramelGold,
                        modifier = Modifier.size(32.dp)
                    )

                    Spacer(Modifier.height(12.dp))

                    Text(
                        "Funciones Limitadas",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = ChocolateBrown,
                        textAlign = TextAlign.Center
                    )

                    Spacer(Modifier.height(8.dp))

                    Text(
                        "Como usuario invitado, tienes acceso limitado a la aplicación. " +
                        "Para acceder a todas las funciones (cambiar foto de perfil, " +
                        "guardar pedidos, etc.), necesitas crear una cuenta.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = ChocolateBrown.copy(alpha = 0.8f),
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            // Características disponibles para invitados
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(4.dp, RoundedCornerShape(16.dp)),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    Text(
                        "Tienes acceso a:",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = ChocolateBrown
                    )

                    Spacer(Modifier.height(12.dp))

                    FeatureRow("✓", "Ver catálogo de productos", MintGreen)
                    FeatureRow("✓", "Agregar productos al carrito", MintGreen)
                    FeatureRow("✓", "Finalizar pedidos", MintGreen)

                    Spacer(Modifier.height(8.dp))

                    HorizontalDivider(color = ChocolateBrown.copy(alpha = 0.1f))

                    Spacer(Modifier.height(8.dp))

                    Text(
                        "No disponible:",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = ChocolateBrown
                    )

                    Spacer(Modifier.height(12.dp))

                    FeatureRow("✗", "Cambiar foto de perfil", MaterialTheme.colorScheme.error)
                    FeatureRow("✗", "Historial de pedidos persistente", MaterialTheme.colorScheme.error)
                    FeatureRow("✗", "Configuración personalizada", MaterialTheme.colorScheme.error)
                }
            }

            Spacer(Modifier.height(24.dp))

            // Mensaje motivacional
            Text(
                "💡 Crea una cuenta para disfrutar de todas las funciones",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = StrawberryRed,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun FeatureRow(icon: String, text: String, color: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            icon,
            style = MaterialTheme.typography.titleMedium,
            color = color,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.width(24.dp)
        )
        Spacer(Modifier.width(8.dp))
        Text(
            text,
            style = MaterialTheme.typography.bodyMedium,
            color = ChocolateBrown.copy(alpha = 0.8f)
        )
    }
}
