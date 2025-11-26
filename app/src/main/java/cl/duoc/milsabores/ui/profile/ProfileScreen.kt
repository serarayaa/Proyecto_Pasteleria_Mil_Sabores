package cl.duoc.milsabores.ui.profile

import android.Manifest
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
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
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import androidx.compose.ui.draw.scale
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
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(vm: ProfileViewModel = viewModel()) {
    val ui by vm.ui.collectAsState()
    val context = LocalContext.current

    val isDarkMode = androidx.compose.foundation.isSystemInDarkTheme()

    var isVisible by remember { mutableStateOf(false) }
    var photoScale by remember { mutableStateOf(0.8f) }

    LaunchedEffect(Unit) {
        delay(100)
        isVisible = true
        delay(200)
        photoScale = 1f
    }

    val scaleAnimation by animateFloatAsState(
        targetValue = photoScale,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "photo_scale"
    )

    val surfaceColor = if (isDarkMode) {
        Color(0xFF1E1E1E)
    } else {
        Color.White
    }

    val textColor = if (isDarkMode) {
        Color(0xFFE8E8E8)
    } else {
        ChocolateBrown
    }

    val secondaryTextColor = if (isDarkMode) {
        Color(0xFFB0B0B0)
    } else {
        ChocolateBrown.copy(alpha = 0.7f)
    }

    val isGuest = ui.uid == "guest" || ui.email == "invitado@milsabores.cl"

    if (isGuest) {
        GuestRestrictedScreen()
        return
    }

    LaunchedEffect(Unit) {
        vm.refreshProfilePhoto(context)
    }

    var pendingUri by remember { mutableStateOf<android.net.Uri?>(null) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    val hasCameraPermission = remember {
        androidx.core.content.ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.CAMERA
        ) == android.content.pm.PackageManager.PERMISSION_GRANTED
    }
    var permissionGranted by remember { mutableStateOf(hasCameraPermission) }

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

    val cameraPermLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        permissionGranted = granted
        if (!granted) {
            Toast.makeText(context, "Permiso de cámara requerido para tomar foto", Toast.LENGTH_SHORT).show()
        } else {
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
                            tint = if (isDarkMode) StrawberryRed else StrawberryRed,
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            "Mi Perfil",
                            fontWeight = FontWeight.Bold,
                            color = textColor
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = if (isDarkMode) {
                        Color(0xFF1E1E1E)
                    } else {
                        GradientPink.copy(alpha = 0.3f)
                    }
                )
            )
        }
    ) { inner ->
        Column(
            Modifier
                .padding(inner)
                .fillMaxSize()
                .then(
                    if (isDarkMode) {
                        Modifier.background(Color(0xFF121212))
                    } else {
                        Modifier.background(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    VanillaWhite,
                                    GradientPink.copy(alpha = 0.2f),
                                    PastelPeach.copy(alpha = 0.1f)
                                )
                            )
                        )
                    }
                )
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn(tween(600)) + scaleIn(tween(600)),
                exit = fadeOut() + scaleOut()
            ) {
                Box(
                    modifier = Modifier
                        .size(180.dp)
                        .padding(8.dp)
                        .scale(scaleAnimation),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(160.dp)
                            .shadow(16.dp, CircleShape)
                            .clip(CircleShape)
                            .then(
                                if (isDarkMode) {
                                    Modifier.background(Color(0xFF2A2A2A))
                                } else {
                                    Modifier.background(
                                        brush = Brush.radialGradient(
                                            colors = listOf(
                                                PastelPink.copy(alpha = 0.4f),
                                                GradientOrange.copy(alpha = 0.2f)
                                            )
                                        )
                                    )
                                }
                            )
                            .clickable { launchCameraFlow() },
                        contentAlignment = Alignment.Center
                    ) {
                        if (ui.profilePhotoUri != null) {
                            Image(
                                painter = rememberAsyncImagePainter(ui.profilePhotoUri),
                                contentDescription = "Foto de perfil",
                                modifier = Modifier
                                    .size(156.dp)
                                    .clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Box(
                                modifier = Modifier
                                    .size(156.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (isDarkMode) {
                                            Color(0xFF1E1E1E)
                                        } else {
                                            Color.White
                                        }
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.AccountCircle,
                                    contentDescription = "Sin foto",
                                    modifier = Modifier.size(100.dp),
                                    tint = if (isDarkMode) {
                                        StrawberryRed.copy(alpha = 0.6f)
                                    } else {
                                        PastelPink.copy(alpha = 0.5f)
                                    }
                                )
                            }
                        }
                    }

                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(4.dp)
                            .size(52.dp)
                            .shadow(12.dp, CircleShape)
                            .clip(CircleShape)
                            .then(
                                if (isDarkMode) {
                                    Modifier.background(StrawberryRed)
                                } else {
                                    Modifier.background(
                                        brush = Brush.linearGradient(
                                            colors = listOf(StrawberryRed, GradientOrange)
                                        )
                                    )
                                }
                            )
                            .clickable { launchCameraFlow() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.CameraAlt,
                            contentDescription = "Cambiar foto",
                            tint = Color.White,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
            }

            Text(
                text = "Toca para cambiar foto",
                modifier = Modifier.padding(top = 0.dp, bottom = 8.dp),
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = secondaryTextColor,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn(tween(800)) + slideInVertically(tween(800)) { it / 2 },
                exit = fadeOut() + scaleOut()
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(8.dp, RoundedCornerShape(20.dp)),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = surfaceColor
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(bottom = 8.dp)
                        ) {
                            Icon(
                                Icons.Default.Info,
                                contentDescription = null,
                                tint = if (isDarkMode) StrawberryRed else StrawberryRed,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                "Información Personal",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = textColor
                            )
                        }

                        Divider(
                            thickness = 1.dp,
                            color = if (isDarkMode) {
                                Color(0xFF404040)
                            } else {
                                PastelPink.copy(alpha = 0.3f)
                            }
                        )

                        InfoRow(
                            "Nombre",
                            ui.displayName ?: "No disponible",
                            textColor,
                            secondaryTextColor
                        )
                        InfoRow(
                            "Correo Electrónico",
                            ui.email ?: "No disponible",
                            textColor,
                            secondaryTextColor
                        )
                        InfoRow(
                            "ID de Usuario",
                            ui.uid?.let { it.take(12) + "..." } ?: "No disponible",
                            textColor,
                            secondaryTextColor
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn(tween(1000)) + slideInVertically(tween(1000)) { it },
                exit = fadeOut()
            ) {
                if (ui.profilePhotoUri != null) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 0.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Button(
                            onClick = { launchCameraFlow() },
                            modifier = Modifier
                                .weight(1f)
                                .height(56.dp)
                                .shadow(6.dp, RoundedCornerShape(16.dp)),
                            enabled = !ui.isLoading,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = StrawberryRed,
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    Icons.Default.CameraAlt,
                                    contentDescription = null,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    "Nueva foto",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    maxLines = 1
                                )
                            }
                        }

                        OutlinedButton(
                            onClick = { showDeleteDialog = true },
                            modifier = Modifier
                                .weight(1f)
                                .height(56.dp)
                                .shadow(4.dp, RoundedCornerShape(16.dp)),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = if (isDarkMode) Color(0xFFFF6B6B) else MaterialTheme.colorScheme.error
                            ),
                            enabled = !ui.isLoading,
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    Icons.Default.Delete,
                                    contentDescription = null,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    "Eliminar",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    maxLines = 1
                                )
                            }
                        }
                    }
                } else {
                    Button(
                        onClick = { launchCameraFlow() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .shadow(6.dp, RoundedCornerShape(16.dp)),
                        enabled = !ui.isLoading,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = StrawberryRed,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Icon(
                            Icons.Default.CameraAlt,
                            contentDescription = null,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(Modifier.width(10.dp))
                        Text(
                            "Tomar foto de perfil",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            maxLines = 1
                        )
                    }
                }
            }

            if (ui.isLoading) {
                Spacer(modifier = Modifier.height(16.dp))
                CircularProgressIndicator(
                    modifier = Modifier.size(32.dp),
                    color = StrawberryRed
                )
            }

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

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            icon = {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(32.dp)
                )
            },
            title = {
                Text(
                    "Eliminar foto de perfil",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            },
            text = {
                Text(
                    "¿Estás seguro de que deseas eliminar tu foto de perfil? Esta acción no se puede deshacer.",
                    fontSize = 14.sp,
                    lineHeight = 20.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        vm.deleteProfilePhoto(context)
                        showDeleteDialog = false
                        Toast.makeText(context, "Foto eliminada correctamente", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Eliminar", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { showDeleteDialog = false },
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Cancelar", fontWeight = FontWeight.Medium)
                }
            },
            shape = RoundedCornerShape(20.dp)
        )
    }
}

@Composable
private fun InfoRow(
    label: String,
    value: String,
    textColor: Color,
    secondaryTextColor: Color
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            color = secondaryTextColor,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 0.5.sp
        )
        Text(
            text = value,
            fontSize = 15.sp,
            color = textColor,
            fontWeight = FontWeight.Medium,
            lineHeight = 20.sp,
            maxLines = 2
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

            Text(
                "Modo Invitado",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = ChocolateBrown,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(16.dp))

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

                    Divider(color = ChocolateBrown.copy(alpha = 0.1f))

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
