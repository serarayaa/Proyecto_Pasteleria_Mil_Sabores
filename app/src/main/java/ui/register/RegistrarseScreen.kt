package cl.duoc.milsabores.ui.register

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Error
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun RegistrarseScreen(
    onBack: () -> Unit,
    onRegistered: () -> Unit,
    vm: RegistrarseViewModel = viewModel()
) {
    val ui = vm.ui.collectAsState().value
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmVisible by remember { mutableStateOf(false) }

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

            // Campo Email con validación visual
            val emailValid = ui.email.isNotEmpty() && android.util.Patterns.EMAIL_ADDRESS.matcher(ui.email).matches()
            OutlinedTextField(
                value = ui.email,
                onValueChange = vm::onEmail,
                label = { Text("Correo electrónico") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                leadingIcon = { Icon(Icons.Outlined.Email, contentDescription = null) },
                trailingIcon = {
                    if (ui.email.isNotEmpty()) {
                        Icon(
                            if (emailValid) Icons.Outlined.CheckCircle else Icons.Outlined.Error,
                            contentDescription = null,
                            tint = if (emailValid) MaterialTheme.colorScheme.primary
                                  else MaterialTheme.colorScheme.error
                        )
                    }
                },
                isError = ui.email.isNotEmpty() && !emailValid,
                supportingText = {
                    if (ui.email.isNotEmpty() && !emailValid) {
                        Text("Ingresa un correo válido")
                    }
                },
                enabled = !ui.loading,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Campo Contraseña
            val passValid = ui.pass.length >= 6
            OutlinedTextField(
                value = ui.pass,
                onValueChange = vm::onPass,
                label = { Text("Contraseña") },
                singleLine = true,
                visualTransformation = if (passwordVisible) VisualTransformation.None
                                      else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                leadingIcon = { Icon(Icons.Outlined.Lock, contentDescription = null) },
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            if (passwordVisible) Icons.Outlined.Visibility else Icons.Outlined.VisibilityOff,
                            contentDescription = if (passwordVisible) "Ocultar" else "Mostrar"
                        )
                    }
                },
                isError = ui.pass.isNotEmpty() && !passValid,
                supportingText = {
                    if (ui.pass.isNotEmpty() && !passValid) {
                        Text("Mínimo 6 caracteres")
                    }
                },
                enabled = !ui.loading,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Campo Confirmar Contraseña
            val confirmValid = ui.confirm.isNotEmpty() && ui.confirm == ui.pass
            OutlinedTextField(
                value = ui.confirm,
                onValueChange = vm::onConfirm,
                label = { Text("Confirmar contraseña") },
                singleLine = true,
                visualTransformation = if (confirmVisible) VisualTransformation.None
                                      else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                leadingIcon = { Icon(Icons.Outlined.Lock, contentDescription = null) },
                trailingIcon = {
                    IconButton(onClick = { confirmVisible = !confirmVisible }) {
                        Icon(
                            if (confirmVisible) Icons.Outlined.Visibility else Icons.Outlined.VisibilityOff,
                            contentDescription = if (confirmVisible) "Ocultar" else "Mostrar"
                        )
                    }
                },
                isError = ui.confirm.isNotEmpty() && !confirmValid,
                supportingText = {
                    if (ui.confirm.isNotEmpty() && !confirmValid) {
                        Text("Las contraseñas no coinciden")
                    } else if (ui.confirm.isNotEmpty() && confirmValid) {
                        Text("✓ Las contraseñas coinciden", color = MaterialTheme.colorScheme.primary)
                    }
                },
                enabled = !ui.loading,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Mensaje de error con animación
            AnimatedVisibility(
                visible = ui.error != null,
                enter = slideInVertically() + fadeIn(),
                exit = slideOutVertically() + fadeOut()
            ) {
                Text(
                    text = ui.error ?: "",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Loading o botón con animación
            AnimatedVisibility(
                visible = ui.loading,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                CircularProgressIndicator()
            }

            AnimatedVisibility(
                visible = !ui.loading,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Button(
                    onClick = vm::submit,
                    enabled = emailValid && passValid && confirmValid,
                    modifier = Modifier.fillMaxWidth()
                ) {
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
