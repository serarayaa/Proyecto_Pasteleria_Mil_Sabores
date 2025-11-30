package cl.duoc.milsabores.ui.recover

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun RecuperarPasswordScreen(
    onBack: () -> Unit,
    onSent: () -> Unit,
    vm: RecuperarPasswordViewModel = viewModel()
) {
    val ui by vm.ui.collectAsState()

    // ✅ Si quieres que, al completarse, navegue de vuelta o muestre un diálogo
    LaunchedEffect(ui.sent) {
        if (ui.sent) {
            onSent()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "Recuperar Contraseña",
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(Modifier.height(20.dp))

            OutlinedTextField(
                value = ui.email,
                onValueChange = vm::onEmailChange,
                label = { Text("Correo electrónico") },
                enabled = !ui.loading,
                singleLine = true
            )

            Spacer(Modifier.height(20.dp))

            // Error de validación o de red
            ui.error?.let {
                Text(it, color = MaterialTheme.colorScheme.error)
                Spacer(Modifier.height(8.dp))
            }

            // Mensaje de éxito visible en la propia pantalla
            ui.successMessage?.let {
                Text(it, color = MaterialTheme.colorScheme.primary)
                Spacer(Modifier.height(8.dp))
            }

            if (ui.loading) {
                CircularProgressIndicator()
            } else {
                Button(
                    onClick = vm::submit,
                    enabled = ui.email.isNotBlank()
                ) {
                    Text("Enviar instrucciones")
                }
            }

            Spacer(Modifier.height(12.dp))

            TextButton(onClick = onBack) {
                Text("Volver")
            }
        }
    }
}
