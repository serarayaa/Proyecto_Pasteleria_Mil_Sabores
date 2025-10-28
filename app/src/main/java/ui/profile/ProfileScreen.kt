package cl.duoc.milsabores.ui.profile

import android.Manifest
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import cl.duoc.milsabores.data.media.MediaRepository
import cl.duoc.milsabores.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(vm: ProfileViewModel = viewModel()) {
    val ui by vm.ui.collectAsState()
    val context = LocalContext.current

    var pendingUri by remember { mutableStateOf<android.net.Uri?>(null) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    val cameraPermLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (!granted) {
            Toast.makeText(context, "Permiso de cámara requerido", Toast.LENGTH_SHORT).show()
        }
    }

    val takePictureLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { ok ->
        try {
            if (ok && pendingUri != null) {
                val uri = pendingUri!!
                // Validar que el URI sea válido
                val saved = vm.saveProfilePhoto(context, uri)
                if (saved) {
                    Toast.makeText(context, "Foto de perfil actualizada ✓", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(context, "Error: No se pudo guardar la foto", Toast.LENGTH_LONG).show()
                }
            } else {
                Toast.makeText(context, "Cancelaste la captura de foto", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Toast.makeText(context, "Error al guardar: ${e.message}", Toast.LENGTH_LONG).show()
        } finally {
            pendingUri = null
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mi Perfil", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
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
            Box(
                modifier = Modifier
                    .size(160.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .clickable {
                        cameraPermLauncher.launch(Manifest.permission.CAMERA)
                        val mediaRepo = MediaRepository()
                        val dest = mediaRepo.createImageUriForUser(context, ui.uid ?: return@clickable)
                        pendingUri = dest
                        dest?.let { takePictureLauncher.launch(it) }
                    },
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
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.CameraAlt,
                        contentDescription = "Cambiar foto",
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Text(
                "Toca para cambiar foto",
                modifier = Modifier.padding(top = 8.dp),
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(24.dp))

            // === INFORMACIÓN DE USUARIO ===
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerLow
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
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
                    onClick = {
                        cameraPermLauncher.launch(Manifest.permission.CAMERA)
                        val mediaRepo = MediaRepository()
                        val dest = mediaRepo.createImageUriForUser(context, ui.uid ?: return@Button)
                        pendingUri = dest
                        dest?.let { takePictureLauncher.launch(it) }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    enabled = !ui.isLoading
                ) {
                    Icon(Icons.Default.CameraAlt, "Cámara", modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Tomar foto")
                }

                if (ui.profilePhotoUri != null) {
                    Button(
                        onClick = { showDeleteDialog = true },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error
                        ),
                        enabled = !ui.isLoading
                    ) {
                        Icon(Icons.Default.Delete, "Eliminar", modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Eliminar")
                    }
                }
            }

            // === INDICADOR DE CARGA ===
            if (ui.isLoading) {
                Spacer(modifier = Modifier.height(16.dp))
                CircularProgressIndicator(modifier = Modifier.size(32.dp))
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
                        vm.deleteProfilePhoto()
                        showDeleteDialog = false
                        Toast.makeText(context, "Foto eliminada", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Eliminar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancelar")
                }
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
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.SemiBold
        )
        Text(
            value,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}
