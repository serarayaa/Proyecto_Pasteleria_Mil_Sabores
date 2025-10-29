package cl.duoc.milsabores.ui.login

import androidx.compose.animation.*
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Login
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import cl.duoc.milsabores.R
import cl.duoc.milsabores.ui.theme.*
import kotlinx.coroutines.delay
import androidx.compose.ui.platform.LocalFocusManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    onBack: () -> Unit,
    onLoginSuccess: () -> Unit,
    vm: LoginViewModel = viewModel()
) {
    val state by vm.ui.collectAsState()
    var visible by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current

    LaunchedEffect(Unit) {
        delay(100)
        visible = true
    }

    LaunchedEffect(state.loggedIn) {
        if (state.loggedIn) onLoginSuccess()
    }

    val snackbarHostState = remember { SnackbarHostState() }
    LaunchedEffect(state.message) {
        state.message?.let {
            snackbarHostState.showSnackbar(it)
            vm.messageConsumed()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        VanillaWhite,
                        GradientPink.copy(alpha = 0.4f),
                        GradientOrange.copy(alpha = 0.3f),
                        PastelPeach.copy(alpha = 0.2f)
                    )
                )
            )
    ) {
        // círculos decorativos
        Box(
            modifier = Modifier
                .size(300.dp)
                .offset(x = (-150).dp, y = (-150).dp)
                .background(StrawberryRed.copy(alpha = 0.1f), CircleShape)
        )
        Box(
            modifier = Modifier
                .size(200.dp)
                .align(Alignment.TopEnd)
                .offset(x = 100.dp, y = 100.dp)
                .background(CaramelGold.copy(alpha = 0.1f), CircleShape)
        )

        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = { },
                    navigationIcon = {
                        IconButton(
                            onClick = onBack,
                            modifier = Modifier.semantics { contentDescription = "Volver" }
                        ) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = null,
                                tint = ChocolateBrown
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent
                    )
                )
            },
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(Modifier.height(20.dp))

                AnimatedVisibility(
                    visible = visible,
                    enter = scaleIn(
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessLow
                        )
                    ) + fadeIn()
                ) {
                    Card(
                        modifier = Modifier
                            .size(120.dp)
                            .shadow(12.dp, CircleShape),
                        shape = CircleShape,
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter = painterResource(R.drawable.logo),
                                contentDescription = "Logo Mil Sabores",
                                modifier = Modifier.size(100.dp)
                            )
                        }
                    }
                }

                Spacer(Modifier.height(24.dp))

                AnimatedVisibility(
                    visible = visible,
                    enter = slideInVertically { -40 } + fadeIn(tween(500))
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            "¡Bienvenido!",
                            style = MaterialTheme.typography.headlineLarge,
                            fontWeight = FontWeight.ExtraBold,
                            color = ChocolateBrown,
                            textAlign = TextAlign.Center
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            "Inicia sesión para continuar",
                            style = MaterialTheme.typography.bodyLarge,
                            color = ChocolateBrown.copy(alpha = 0.7f),
                            textAlign = TextAlign.Center
                        )
                    }
                }

                Spacer(Modifier.height(40.dp))

                AnimatedVisibility(
                    visible = visible,
                    enter = slideInVertically { 40 } + fadeIn(tween(500, delayMillis = 200))
                ) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(8.dp, RoundedCornerShape(24.dp)),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            val emailValid = state.email.isNotEmpty() &&
                                    android.util.Patterns.EMAIL_ADDRESS.matcher(state.email).matches()

                            OutlinedTextField(
                                value = state.email,
                                onValueChange = vm::onEmailChange,
                                label = { Text("Correo electrónico") },
                                singleLine = true,
                                leadingIcon = {
                                    Icon(Icons.Outlined.Email, contentDescription = null, tint = StrawberryRed)
                                },
                                trailingIcon = {
                                    if (state.email.isNotEmpty()) {
                                        Icon(
                                            if (emailValid) Icons.Filled.CheckCircle else Icons.Filled.Error,
                                            contentDescription = null,
                                            tint = if (emailValid) MintGreen else MaterialTheme.colorScheme.error
                                        )
                                    }
                                },
                                isError = state.email.isNotEmpty() && !emailValid,
                                supportingText = {
                                    if (state.email.isNotEmpty() && !emailValid) {
                                        Text("Ingresa un correo válido", color = MaterialTheme.colorScheme.error)
                                    }
                                },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = StrawberryRed,
                                    unfocusedBorderColor = ChocolateBrown.copy(alpha = 0.3f)
                                ),
                                keyboardOptions = KeyboardOptions(
                                    imeAction = ImeAction.Next,
                                    autoCorrect = false
                                )
                            )

                            var passwordVisible by remember { mutableStateOf(false) }
                            val passwordValid = state.password.length >= 6

                            OutlinedTextField(
                                value = state.password,
                                onValueChange = vm::onPasswordChange,
                                label = { Text("Contraseña") },
                                singleLine = true,
                                visualTransformation = if (passwordVisible)
                                    VisualTransformation.None else PasswordVisualTransformation(),
                                leadingIcon = {
                                    Icon(Icons.Outlined.Lock, contentDescription = null, tint = StrawberryRed)
                                },
                                trailingIcon = {
                                    IconButton(
                                        onClick = { passwordVisible = !passwordVisible },
                                        modifier = Modifier.semantics {
                                            contentDescription = if (passwordVisible) "Ocultar contraseña" else "Mostrar contraseña"
                                        }
                                    ) {
                                        Icon(
                                            if (passwordVisible) Icons.Outlined.Visibility
                                            else Icons.Outlined.VisibilityOff,
                                            contentDescription = null,
                                            tint = ChocolateBrown.copy(alpha = 0.6f)
                                        )
                                    }
                                },
                                isError = state.password.isNotEmpty() && !passwordValid,
                                supportingText = {
                                    if (state.password.isNotEmpty() && !passwordValid) {
                                        Text("Mínimo 6 caracteres", color = MaterialTheme.colorScheme.error)
                                    }
                                },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = StrawberryRed,
                                    unfocusedBorderColor = ChocolateBrown.copy(alpha = 0.3f)
                                ),
                                keyboardOptions = KeyboardOptions(
                                    imeAction = ImeAction.Done,
                                    autoCorrect = false
                                ),
                                keyboardActions = KeyboardActions(
                                    onDone = {
                                        if (!state.loading && emailValid && passwordValid) vm.submit()
                                    }
                                )
                            )

                            AnimatedVisibility(
                                visible = state.error != null,
                                enter = slideInVertically() + fadeIn(),
                                exit = slideOutVertically() + fadeOut()
                            ) {
                                Card(
                                    colors = CardDefaults.cardColors(
                                        containerColor = MaterialTheme.colorScheme.errorContainer
                                    ),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(12.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            Icons.Filled.Error,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.error,
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Spacer(Modifier.width(8.dp))
                                        Text(
                                            text = state.error ?: "",
                                            color = MaterialTheme.colorScheme.error,
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                    }
                                }
                            }

                            Spacer(Modifier.height(8.dp))

                            Button(
                                onClick = vm::submit,
                                enabled = !state.loading && emailValid && passwordValid,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(56.dp)
                                    .shadow(4.dp, RoundedCornerShape(16.dp))
                                    .semantics { contentDescription = "Ingresar" },
                                shape = RoundedCornerShape(16.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = StrawberryRed,
                                    disabledContainerColor = ChocolateBrown.copy(alpha = 0.3f)
                                )
                            ) {
                                if (state.loading) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(24.dp),
                                        color = Color.White,
                                        strokeWidth = 2.dp
                                    )
                                } else {
                                    Icon(Icons.AutoMirrored.Filled.Login, contentDescription = null)
                                    Spacer(Modifier.width(8.dp))
                                    Text(
                                        "Ingresar",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            // Botón de invitado bajo el botón principal
                            TextButton(
                                onClick = { vm.guestLogin() },
                                enabled = !state.loading,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Ingresar como invitado", color = ChocolateBrown)
                            }
                        }
                    }
                }

                Spacer(Modifier.height(32.dp))

                AnimatedVisibility(
                    visible = visible,
                    enter = fadeIn(tween(500, delayMillis = 400))
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            Icons.Filled.Cake,
                            contentDescription = null,
                            tint = StrawberryRed,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            "Pastelería Mil Sabores",
                            style = MaterialTheme.typography.bodyMedium,
                            color = ChocolateBrown.copy(alpha = 0.7f),
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }

        AnimatedVisibility(
            visible = state.loading,
            modifier = Modifier.align(Alignment.Center),
            enter = fadeIn() + scaleIn(),
            exit = fadeOut() + scaleOut()
        ) {
            Card(
                modifier = Modifier
                    .size(100.dp)
                    .shadow(8.dp, RoundedCornerShape(20.dp)),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = StrawberryRed,
                        strokeWidth = 4.dp
                    )
                }
            }
        }
    }
}
