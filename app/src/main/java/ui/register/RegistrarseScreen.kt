package cl.duoc.milsabores.ui.register

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
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun RegistrarseScreen(
    onBack: () -> Unit,
    onRegistered: () -> Unit,
    vm: RegistrarseViewModel = viewModel()
) {
    val ui = vm.ui.collectAsState().value

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "Crear cuenta nueva",
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = ui.email,
                onValueChange = vm::onEmail,
                label = { Text("Correo electrónico") },
                enabled = !ui.loading
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = ui.pass,
                onValueChange = vm::onPass,
                label = { Text("Contraseña") },
                enabled = !ui.loading
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = ui.confirm,
                onValueChange = vm::onConfirm,
                label = { Text("Confirmar contraseña") },
                enabled = !ui.loading
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (ui.error != null) {
                Text(ui.error, color = MaterialTheme.colorScheme.error)
                Spacer(modifier = Modifier.height(8.dp))
            }

            if (ui.loading) {
                CircularProgressIndicator()
            } else {
                Button(onClick = vm::submit) {
                    Text("Registrarse")
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            TextButton(onClick = onBack) {
                Text("Volver")
            }

            if (ui.registered) onRegistered()
        }
    }
}
