package cl.duoc.milsabores.ui.principal

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cake
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material.icons.outlined.ShoppingBag
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import cl.duoc.milsabores.data.media.MediaRepository
import cl.duoc.milsabores.ui.principal.components.UiProductosCard
import cl.duoc.milsabores.ui.profile.ProfileScreen
import cl.duoc.milsabores.ui.profile.ProfileViewModel
import cl.duoc.milsabores.ui.recordatorio.RecordatorioScreen
import cl.duoc.milsabores.ui.recordatorio.RecordatorioViewModel
import cl.duoc.milsabores.ui.theme.*
import cl.duoc.milsabores.ui.vmfactory.RecordatorioVMFactory
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

sealed class BottomItem(
    val route: String,
    val title: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    data object Home : BottomItem("home", "Inicio", Icons.Outlined.Home)
    data object Favs : BottomItem("favs", "Favoritos", Icons.Outlined.FavoriteBorder)
    data object Cart : BottomItem("cart", "Carrito", Icons.Outlined.ShoppingCart)
    data object Pedidos : BottomItem("pedidos", "Pedidos", Icons.Outlined.ShoppingBag)
    data object More : BottomItem("more", "Más", Icons.Outlined.Menu)
}

private val bottomItems = listOf(
    BottomItem.Home, BottomItem.Favs, BottomItem.Cart, BottomItem.Pedidos, BottomItem.More
)

@Composable
private fun BottomBar(
    navController: NavHostController,
    onHomeTap: () -> Unit,
    cantidadCarrito: Int = 0
) {
    val backStack by navController.currentBackStackEntryAsState()
    val currentRoute = backStack?.destination?.route

    NavigationBar {
        bottomItems.forEach { item ->
            NavigationBarItem(
                selected = currentRoute == item.route,
                onClick = {
                    if (item.route == BottomItem.Home.route) {
                        onHomeTap()
                        navController.navigate(BottomItem.Home.route) {
                            popUpTo(navController.graph.startDestinationId) { saveState = false }
                            launchSingleTop = true
                            restoreState = false
                        }
                    } else {
                        navController.navigate(item.route) {
                            popUpTo(navController.graph.startDestinationId) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                icon = {
                    if (item.route == BottomItem.Cart.route && cantidadCarrito > 0) {
                        BadgedBox(badge = { Badge { Text("$cantidadCarrito") } }) {
                            Icon(item.icon, contentDescription = item.title)
                        }
                    } else {
                        Icon(item.icon, contentDescription = item.title)
                    }
                },
                label = { Text(item.title) },
                colors = NavigationBarItemDefaults.colors()
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun PrincipalScreen(
    onLogout: () -> Unit = {},
    vm: PrincipalViewModel = viewModel()
) {
    val state by vm.ui.collectAsState()
    val categoriaSel by vm.categoriaSel.collectAsState()
    val productos by vm.productosFiltrados.collectAsState()
    val cantidadCarrito by vm.cantidadCarrito.collectAsState()

    var expanded by remember { mutableStateOf(false) }
    val tabsNav = rememberNavController()
    val context = LocalContext.current

    LaunchedEffect(state.loggedOut) {
        if (state.loggedOut) onLogout()
    }

    val snackbarHostState = remember { SnackbarHostState() }
    LaunchedEffect(state.error) {
        state.error?.let { snackbarHostState.showSnackbar(it) }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Principal") },
                actions = {
                    IconButton(onClick = { expanded = true }) {
                        Icon(Icons.Outlined.MoreVert, contentDescription = "Menú")
                    }
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Perfil") },
                            onClick = {
                                expanded = false
                                tabsNav.navigate("profile")
                            },
                            leadingIcon = { Icon(Icons.Outlined.Info, contentDescription = null) }
                        )
                        DropdownMenuItem(
                            text = { Text("Configuración") },
                            onClick = { expanded = false },
                            leadingIcon = { Icon(Icons.Outlined.Settings, contentDescription = null) }
                        )
                        DropdownMenuItem(
                            text = { Text("Logout") },
                            onClick = {
                                expanded = false
                                vm.logout()
                            }
                        )
                    }
                }
            )
        },
        bottomBar = { BottomBar(tabsNav, onHomeTap = { vm.refreshHome() }, cantidadCarrito = cantidadCarrito) },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { inner ->
        NavHost(
            navController = tabsNav,
            startDestination = BottomItem.Home.route,
            modifier = Modifier.padding(inner)
        ) {
            composable(route = BottomItem.Home.route) {
                LaunchedEffect(Unit) {
                    if (productos.isEmpty()) vm.cargarProductos()
                }

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    VanillaWhite,
                                    GradientPink.copy(alpha = 0.3f),
                                    GradientOrange.copy(alpha = 0.2f)
                                )
                            )
                        )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        val saludo = "Hola ${state.email ?: "usuario"}"
                        Text(
                            saludo,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = ChocolateBrown
                        )
                        Text(
                            "Bienvenido a tu pantalla principal.",
                            style = MaterialTheme.typography.bodyLarge,
                            color = ChocolateBrown.copy(alpha = 0.7f)
                        )

                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            contentPadding = PaddingValues(vertical = 4.dp)
                        ) {
                            items(vm.categorias.size) { idx ->
                                val cat = vm.categorias[idx]
                                AnimatedContent(
                                    targetState = categoriaSel == cat,
                                    label = "filterChip_$cat"
                                ) { selected ->
                                    FilterChip(
                                        selected = selected,
                                        onClick = { vm.setCategoria(cat) },
                                        label = { Text(cat) }
                                    )
                                }
                            }
                        }

                        AnimatedVisibility(
                            visible = state.loading,
                            enter = fadeIn() + slideInVertically(),
                            exit = fadeOut() + slideOutVertically()
                        ) {
                            Box(
                                modifier = Modifier.fillMaxWidth().padding(32.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator()
                            }
                        }

                        AnimatedVisibility(
                            visible = !state.loading && productos.isEmpty(),
                            enter = fadeIn(),
                            exit = fadeOut()
                        ) {
                            Text(
                                "No hay productos en esta categoría",
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier.padding(16.dp)
                            )
                        }

                        LazyVerticalGrid(
                            columns = GridCells.Adaptive(minSize = 180.dp),
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            contentPadding = PaddingValues(top = 8.dp, bottom = 80.dp)
                        ) {
                            items(productos, key = { it.id }) { producto ->
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .aspectRatio(0.6f)
                                        .animateItem()
                                        .animateContentSize()
                                ) {
                                    UiProductosCard(
                                        producto = producto,
                                        onAgregar = {
                                            vm.agregarAlCarrito(producto)

                                            val vibrator = context.getSystemService(android.content.Context.VIBRATOR_SERVICE) as? android.os.Vibrator
                                            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                                                vibrator?.vibrate(
                                                    android.os.VibrationEffect.createOneShot(
                                                        100,
                                                        android.os.VibrationEffect.DEFAULT_AMPLITUDE
                                                    )
                                                )
                                            } else {
                                                @Suppress("DEPRECATION")
                                                vibrator?.vibrate(100)
                                            }

                                            CoroutineScope(Dispatchers.Main).launch {
                                                snackbarHostState.showSnackbar(
                                                    message = "${producto.titulo} agregado al carrito",
                                                    duration = androidx.compose.material3.SnackbarDuration.Short
                                                )
                                            }
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }

            composable(BottomItem.Favs.route) {
                Box(
                    Modifier
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
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        // Icono animado
                        Card(
                            modifier = Modifier
                                .size(120.dp)
                                .shadow(8.dp, CircleShape),
                            shape = CircleShape,
                            colors = CardDefaults.cardColors(
                                containerColor = StrawberryRed.copy(alpha = 0.1f)
                            )
                        ) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Outlined.FavoriteBorder,
                                    contentDescription = null,
                                    modifier = Modifier.size(60.dp),
                                    tint = StrawberryRed
                                )
                            }
                        }

                        Spacer(Modifier.height(24.dp))

                        Text(
                            "Favoritos",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = ChocolateBrown
                        )

                        Spacer(Modifier.height(12.dp))

                        Text(
                            "Aquí aparecerán tus productos favoritos",
                            style = MaterialTheme.typography.bodyLarge,
                            color = ChocolateBrown.copy(alpha = 0.6f),
                            textAlign = TextAlign.Center
                        )

                        Spacer(Modifier.height(24.dp))

                        OutlinedButton(
                            onClick = {
                                tabsNav.navigate(BottomItem.Home.route) {
                                    popUpTo(BottomItem.Home.route) { inclusive = false }
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth(0.7f)
                                .height(48.dp),
                            shape = RoundedCornerShape(24.dp),
                            border = BorderStroke(2.dp, StrawberryRed)
                        ) {
                            Icon(Icons.Filled.Cake, contentDescription = null, tint = StrawberryRed)
                            Spacer(Modifier.width(8.dp))
                            Text(
                                "Explorar Productos",
                                color = StrawberryRed,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            composable(BottomItem.Cart.route) {
                val carritoVmFactory = remember {
                    object : ViewModelProvider.Factory {
                        @Suppress("UNCHECKED_CAST")
                        override fun <T : ViewModel> create(modelClass: Class<T>): T {
                            return cl.duoc.milsabores.ui.carrito.CarritoViewModel(
                                context.applicationContext as android.app.Application
                            ) as T
                        }
                    }
                }
                val carritoVm: cl.duoc.milsabores.ui.carrito.CarritoViewModel = viewModel(factory = carritoVmFactory)

                cl.duoc.milsabores.ui.carrito.CarritoScreen(
                    vm = carritoVm,
                    onPedidoCreado = {
                        // Navegar a la pantalla de pedidos
                        tabsNav.navigate(BottomItem.Pedidos.route) {
                            popUpTo(BottomItem.Home.route) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }

            composable(BottomItem.Pedidos.route) {
                cl.duoc.milsabores.ui.pedidos.PedidosScreen()
            }

            composable(BottomItem.More.route) {
                Column(
                    Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically)
                ) {
                    Text("Más opciones")
                    Button(onClick = { vm.logout() }) {
                        Icon(Icons.Outlined.Close, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text(if (state.loading) "Cerrando..." else "Cerrar sesión")
                    }
                }
            }

            composable("profile") {
                val factory = remember {
                    object : ViewModelProvider.Factory {
                        @Suppress("UNCHECKED_CAST")
                        override fun <T : ViewModel> create(modelClass: Class<T>): T {
                            return ProfileViewModel(
                                auth = FirebaseAuth.getInstance(),
                                mediaRepo = MediaRepository()
                            ) as T
                        }
                    }
                }
                val pvm: ProfileViewModel = viewModel(factory = factory)
                ProfileScreen(pvm)
            }
        }
    }
}

