@file:OptIn(
    androidx.compose.material3.ExperimentalMaterial3Api::class,
    androidx.compose.foundation.ExperimentalFoundationApi::class
)

package cl.duoc.milsabores.ui.principal

import android.app.Application
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material.icons.filled.Cake
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.ShoppingBag
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import cl.duoc.milsabores.ui.principal.components.UiProductosCard
import cl.duoc.milsabores.ui.profile.ProfileScreen
import cl.duoc.milsabores.ui.profile.ProfileViewModel
import cl.duoc.milsabores.ui.theme.ChocolateBrown
import cl.duoc.milsabores.ui.theme.GradientOrange
import cl.duoc.milsabores.ui.theme.GradientPink
import cl.duoc.milsabores.ui.theme.PastelPeach
import cl.duoc.milsabores.ui.theme.StrawberryRed
import cl.duoc.milsabores.ui.theme.VanillaWhite
import cl.duoc.milsabores.ui.time.TimeViewModel
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
    val isDarkMode = androidx.compose.foundation.isSystemInDarkTheme()

    NavigationBar(
        containerColor = if (isDarkMode) {
            androidx.compose.ui.graphics.Color(0xFF1A1A1A)
        } else {
            MaterialTheme.colorScheme.surface
        },
        contentColor = if (isDarkMode) {
            androidx.compose.ui.graphics.Color(0xFFF5F5F5)
        } else {
            ChocolateBrown
        },
        modifier = Modifier.height(80.dp)
    ) {
        bottomItems.forEach { item ->
            val label = item.title
            val isSelected = currentRoute == item.route
            val iconDesc = if (item.route == BottomItem.Cart.route && cantidadCarrito > 0)
                "Carrito, $cantidadCarrito artículos"
            else label

            NavigationBarItem(
                selected = isSelected,
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
                    Box(
                        modifier = Modifier
                            .size(if (isSelected) 32.dp else 26.dp)
                            .animateContentSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        if (item.route == BottomItem.Cart.route && cantidadCarrito > 0) {
                            BadgedBox(
                                badge = {
                                    Badge(
                                        containerColor = if (isDarkMode) {
                                            androidx.compose.ui.graphics.Color(0xFFFF4E88)
                                        } else {
                                            StrawberryRed
                                        },
                                        contentColor = androidx.compose.ui.graphics.Color.White,
                                        modifier = Modifier.scale(1.1f)
                                    ) {
                                        Text(
                                            "$cantidadCarrito",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 11.sp
                                        )
                                    }
                                }
                            ) {
                                Icon(
                                    item.icon,
                                    contentDescription = iconDesc,
                                    modifier = Modifier.size(if (isSelected) 28.dp else 24.dp)
                                )
                            }
                        } else {
                            Icon(
                                item.icon,
                                contentDescription = iconDesc,
                                modifier = Modifier.size(if (isSelected) 28.dp else 24.dp)
                            )
                        }
                    }
                },
                label = {
                    Text(
                        label,
                        fontSize = if (isSelected) 13.sp else 12.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = if (isDarkMode) {
                        androidx.compose.ui.graphics.Color(0xFFFF4E88)
                    } else {
                        StrawberryRed
                    },
                    selectedTextColor = if (isDarkMode) {
                        androidx.compose.ui.graphics.Color(0xFFFF4E88)
                    } else {
                        StrawberryRed
                    },
                    unselectedIconColor = if (isDarkMode) {
                        androidx.compose.ui.graphics.Color(0xFFB8B8B8)
                    } else {
                        ChocolateBrown.copy(alpha = 0.6f)
                    },
                    unselectedTextColor = if (isDarkMode) {
                        androidx.compose.ui.graphics.Color(0xFFB8B8B8)
                    } else {
                        ChocolateBrown.copy(alpha = 0.6f)
                    },
                    indicatorColor = if (isDarkMode) {
                        androidx.compose.ui.graphics.Color(0xFF3D1F2E)
                    } else {
                        GradientPink.copy(alpha = 0.3f)
                    }
                )
            )
        }
    }
}

@Composable
fun PrincipalScreen(
    onLogout: () -> Unit = {},
    onDarkModeChanged: (Boolean) -> Unit = {},
    vm: PrincipalViewModel = viewModel()
) {
    val state by vm.ui.collectAsState()
    val categoriaSel by vm.categoriaSel.collectAsState()
    val productos by vm.productosFiltrados.collectAsState()
    val cantidadCarrito by vm.cantidadCarrito.collectAsState()

    // 🔹 ViewModel de la API externa de hora
    val timeVm: TimeViewModel = viewModel()
    val horaChile by timeVm.horaChile.collectAsState()

    var expanded by remember { mutableStateOf(false) }
    val tabsNav = rememberNavController()
    val context = LocalContext.current
    val haptics = LocalHapticFeedback.current
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val columnas = GridCells.Adaptive(minSize = 170.dp)

    LaunchedEffect(state.loggedOut) {
        if (state.loggedOut) onLogout()
    }

    LaunchedEffect(state.error) {
        state.error?.let { msg ->
            scope.launch { snackbarHostState.showSnackbar(msg) }
        }
    }

    val isDarkMode = androidx.compose.foundation.isSystemInDarkTheme()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Principal",
                        fontWeight = FontWeight.Bold,
                        color = if (isDarkMode) androidx.compose.ui.graphics.Color(0xFFF5F5F5) else ChocolateBrown
                    )
                },
                actions = {
                    IconButton(onClick = { expanded = true }) {
                        Icon(
                            Icons.Outlined.MoreVert,
                            contentDescription = "Menú",
                            tint = if (isDarkMode) androidx.compose.ui.graphics.Color(0xFFF5F5F5) else ChocolateBrown
                        )
                    }
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Mi Perfil") },
                            onClick = {
                                expanded = false
                                tabsNav.navigate("profile")
                            },
                            leadingIcon = { Icon(Icons.Outlined.Info, contentDescription = null) }
                        )
                        DropdownMenuItem(
                            text = { Text("Mis Pedidos") },
                            onClick = {
                                expanded = false
                                tabsNav.navigate(BottomItem.Pedidos.route) {
                                    popUpTo(BottomItem.Home.route) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            leadingIcon = { Icon(Icons.Outlined.ShoppingBag, contentDescription = null) }
                        )
                        DropdownMenuItem(
                            text = { Text("Configuración") },
                            onClick = {
                                expanded = false
                                tabsNav.navigate(BottomItem.More.route) {
                                    popUpTo(BottomItem.Home.route) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            leadingIcon = { Icon(Icons.Outlined.Settings, contentDescription = null) }
                        )
                        DropdownMenuItem(
                            text = { Text("Cerrar Sesión") },
                            onClick = {
                                expanded = false
                                vm.logout()
                            },
                            leadingIcon = { Icon(Icons.AutoMirrored.Outlined.Logout, contentDescription = null) }
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = if (isDarkMode) {
                        androidx.compose.ui.graphics.Color(0xFF1A1A1A)
                    } else {
                        GradientPink.copy(alpha = 0.2f)
                    },
                    titleContentColor = if (isDarkMode) {
                        androidx.compose.ui.graphics.Color(0xFFF5F5F5)
                    } else {
                        ChocolateBrown
                    }
                )
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
                        .then(
                            if (isDarkMode) {
                                Modifier.background(
                                    brush = Brush.verticalGradient(
                                        colors = listOf(
                                            androidx.compose.ui.graphics.Color(0xFF0D0D0D),
                                            androidx.compose.ui.graphics.Color(0xFF1A1A1A),
                                            androidx.compose.ui.graphics.Color(0xFF0D0D0D)
                                        )
                                    )
                                )
                            } else {
                                Modifier.background(
                                    brush = Brush.verticalGradient(
                                        colors = listOf(
                                            VanillaWhite,
                                            GradientPink.copy(alpha = 0.3f),
                                            GradientOrange.copy(alpha = 0.2f)
                                        )
                                    )
                                )
                            }
                        )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 0.dp),
                        verticalArrangement = Arrangement.spacedBy(0.dp)
                    ) {
                        // Banner
                        AnimatedVisibility(
                            visible = true,
                            enter = fadeIn(tween(600)) + slideInVertically(tween(600)) { -it },
                            exit = fadeOut()
                        ) {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                shape = RoundedCornerShape(20.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isDarkMode) {
                                        androidx.compose.ui.graphics.Color(0xFF2D2D2D)
                                    } else {
                                        StrawberryRed.copy(alpha = 0.1f)
                                    }
                                ),
                                elevation = CardDefaults.cardElevation(defaultElevation = if (isDarkMode) 8.dp else 4.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(
                                            if (isDarkMode) {
                                                Brush.horizontalGradient(
                                                    colors = listOf(
                                                        androidx.compose.ui.graphics.Color(0xFF3D1F2E),
                                                        androidx.compose.ui.graphics.Color(0xFF3D2F1F)
                                                    )
                                                )
                                            } else {
                                                Brush.horizontalGradient(
                                                    colors = listOf(
                                                        GradientPink.copy(alpha = 0.3f),
                                                        GradientOrange.copy(alpha = 0.3f)
                                                    )
                                                )
                                            }
                                        )
                                        .padding(20.dp)
                                ) {
                                    Column {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                "¡Hola! ",
                                                style = MaterialTheme.typography.headlineSmall,
                                                fontWeight = FontWeight.Bold,
                                                color = if (isDarkMode) {
                                                    androidx.compose.ui.graphics.Color(0xFFF5F5F5)
                                                } else {
                                                    ChocolateBrown
                                                }
                                            )
                                            Text(
                                                "👋",
                                                style = MaterialTheme.typography.headlineSmall
                                            )
                                        }
                                        Text(
                                            state.email?.split("@")?.firstOrNull() ?: "Usuario",
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Medium,
                                            color = if (isDarkMode) {
                                                androidx.compose.ui.graphics.Color(0xFFFF4E88)
                                            } else {
                                                StrawberryRed
                                            }
                                        )
                                        Spacer(Modifier.height(8.dp))
                                        Text(
                                            "Descubre nuestros deliciosos productos 🍰",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = if (isDarkMode) {
                                                androidx.compose.ui.graphics.Color(0xFFB8B8B8)
                                            } else {
                                                ChocolateBrown.copy(alpha = 0.7f)
                                            }
                                        )

                                        // 🔹 Hora desde la API externa (siempre muestra algo)
                                        Spacer(Modifier.height(4.dp))
                                        Text(
                                            text = if (horaChile != null) {
                                                "⏰ Hora en Chile: $horaChile"
                                            } else {
                                                "⏰ Hora en Chile: cargando o sin conexión"
                                            },
                                            style = MaterialTheme.typography.bodySmall,
                                            fontWeight = FontWeight.SemiBold,
                                            color = if (isDarkMode) {
                                                androidx.compose.ui.graphics.Color(0xFFB8B8B8)
                                            } else {
                                                ChocolateBrown
                                            }
                                        )
                                    }
                                }
                            }
                        }

                        Text(
                            "Categorías",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = if (isDarkMode) androidx.compose.ui.graphics.Color(0xFFE8E8E8) else ChocolateBrown,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        )

                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            items(vm.categorias.size) { idx ->
                                val cat = vm.categorias[idx]
                                val isSelected = categoriaSel == cat

                                Card(
                                    onClick = { vm.setCategoria(cat) },
                                    modifier = Modifier
                                        .shadow(if (isSelected) 8.dp else 2.dp, RoundedCornerShape(16.dp)),
                                    shape = RoundedCornerShape(16.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = if (isSelected) {
                                            if (isDarkMode) {
                                                androidx.compose.ui.graphics.Color(0xFFFF4E88)
                                            } else {
                                                StrawberryRed
                                            }
                                        } else if (isDarkMode) {
                                            androidx.compose.ui.graphics.Color(0xFF2D2D2D)
                                        } else {
                                            androidx.compose.ui.graphics.Color.White
                                        }
                                    ),
                                    border = if (!isSelected) BorderStroke(
                                        1.5.dp,
                                        if (isDarkMode) {
                                            androidx.compose.ui.graphics.Color(0xFF4A4A4A)
                                        } else {
                                            StrawberryRed.copy(alpha = 0.2f)
                                        }
                                    ) else null,
                                    elevation = CardDefaults.cardElevation(
                                        defaultElevation = if (isSelected) 6.dp else 2.dp
                                    )
                                ) {
                                    Text(
                                        cat,
                                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp),
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        fontSize = if (isSelected) 14.sp else 13.sp,
                                        color = if (isSelected) {
                                            androidx.compose.ui.graphics.Color.White
                                        } else if (isDarkMode) {
                                            androidx.compose.ui.graphics.Color(0xFFF5F5F5)
                                        } else {
                                            ChocolateBrown
                                        }
                                    )
                                }
                            }
                        }

                        Text(
                            text = if (categoriaSel == "Todos") "Todos los Productos" else (categoriaSel ?: "Productos"),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = if (isDarkMode) androidx.compose.ui.graphics.Color(0xFFE8E8E8) else ChocolateBrown,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
                        )

                        AnimatedVisibility(
                            visible = state.loading,
                            enter = fadeIn(),
                            exit = fadeOut()
                        ) {
                            LazyVerticalGrid(
                                columns = columnas,
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.spacedBy(12.dp),
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 80.dp)
                            ) {
                                items(6) {
                                    cl.duoc.milsabores.ui.components.ProductCardSkeleton(
                                        isDarkMode = isDarkMode
                                    )
                                }
                            }
                        }

                        AnimatedVisibility(
                            visible = !state.loading && productos.isEmpty(),
                            enter = fadeIn(tween(400)),
                            exit = fadeOut(tween(200))
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(32.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    "🍪",
                                    style = MaterialTheme.typography.displayLarge
                                )
                                Spacer(Modifier.height(16.dp))
                                Text(
                                    "No hay productos en esta categoría",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = if (isDarkMode) androidx.compose.ui.graphics.Color(0xFFB0B0B0) else ChocolateBrown.copy(alpha = 0.6f),
                                    textAlign = TextAlign.Center
                                )
                            }
                        }

                        LazyVerticalGrid(
                            columns = columnas,
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 80.dp)
                        ) {
                            items(productos, key = { it.id }) { producto ->
                                val hapticsLocal = haptics
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .animateContentSize()
                                ) {
                                    UiProductosCard(
                                        producto = producto,
                                        onAgregar = {
                                            vm.agregarAlCarrito(producto)
                                            hapticsLocal.performHapticFeedback(HapticFeedbackType.LongPress)
                                            scope.launch {
                                                snackbarHostState.showSnackbar(
                                                    message = "✓ ${producto.titulo} agregado al carrito",
                                                    duration = SnackbarDuration.Short
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
                        .then(
                            if (isDarkMode) {
                                Modifier.background(androidx.compose.ui.graphics.Color(0xFF121212))
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
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Card(
                            modifier = Modifier
                                .size(120.dp)
                                .shadow(8.dp, CircleShape),
                            shape = CircleShape,
                            colors = CardDefaults.cardColors(
                                containerColor = if (isDarkMode) {
                                    androidx.compose.ui.graphics.Color(0xFF2A2A2A)
                                } else {
                                    StrawberryRed.copy(alpha = 0.1f)
                                }
                            )
                        ) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Outlined.FavoriteBorder,
                                    contentDescription = "Favoritos",
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
                            color = if (isDarkMode) androidx.compose.ui.graphics.Color(0xFFE8E8E8) else ChocolateBrown
                        )

                        Spacer(Modifier.height(12.dp))

                        Text(
                            "Aquí aparecerán tus productos favoritos",
                            style = MaterialTheme.typography.bodyLarge,
                            color = if (isDarkMode) androidx.compose.ui.graphics.Color(0xFFB0B0B0) else ChocolateBrown.copy(alpha = 0.6f),
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
                                context.applicationContext as Application
                            ) as T
                        }
                    }
                }
                val carritoVm: cl.duoc.milsabores.ui.carrito.CarritoViewModel =
                    viewModel(factory = carritoVmFactory)

                cl.duoc.milsabores.ui.carrito.CarritoScreen(
                    vm = carritoVm,
                    onPedidoCreado = {
                        tabsNav.navigate(BottomItem.Pedidos.route) {
                            popUpTo(BottomItem.Home.route) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    onNavigateToHome = {
                        tabsNav.navigate(BottomItem.Home.route) {
                            popUpTo(BottomItem.Home.route) { inclusive = false }
                            launchSingleTop = true
                        }
                    }
                )
            }

            composable(BottomItem.Pedidos.route) {
                cl.duoc.milsabores.ui.pedidos.PedidosScreen()
            }

            composable(BottomItem.More.route) {
                cl.duoc.milsabores.ui.settings.SettingsScreen(
                    onModoOscuroChanged = onDarkModeChanged
                )
            }

            // Perfil sin factory especial (usa el ProfileViewModel por defecto)
            composable("profile") {
                val pvm: ProfileViewModel = viewModel()
                ProfileScreen(pvm)
            }
        }
    }
}
