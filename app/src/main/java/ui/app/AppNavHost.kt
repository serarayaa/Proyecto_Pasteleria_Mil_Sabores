package cl.duoc.milsabores.ui.app


import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import cl.duoc.milsabores.ui.home.HomeScreen
import cl.duoc.milsabores.ui.login.LoginScreen
import cl.duoc.milsabores.ui.principal.PrincipalScreen
import cl.duoc.milsabores.ui.recover.RecuperarPasswordScreen
import cl.duoc.milsabores.ui.register.RegistrarseScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavHost() {
    val nav = rememberNavController()

    NavHost(
        navController = nav,
        startDestination = Routes.HomeRoot.path
    ) {
        composable(Routes.HomeRoot.path) {
            HomeScreen(
                onLoginClick = { nav.navigate(Routes.Login.path) },
                onRegisterClick = { nav.navigate(Routes.Register.path) },
                onRecoverClick = { nav.navigate(Routes.RecoverPassword.path) }
            )
        }

        composable(Routes.Login.path) {
            LoginScreen(
                onBack = { nav.popBackStack() },
                onLoginSuccess = {
                    nav.navigate(Routes.Principal.path) {
                        launchSingleTop = true
                        // Si quieres bloquear volver al login:
                        popUpTo(Routes.HomeRoot.path) { inclusive = false }
                    }
                }
            )
        }

        composable(Routes.Principal.path) {
            PrincipalScreen(
                onLogout = {
                    nav.navigate(Routes.HomeRoot.path) {
                        // Limpia todo hasta Home para que el botón atrás no vuelva al principal
                        popUpTo(Routes.HomeRoot.path) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }

        composable(Routes.Register.path) {
            RegistrarseScreen(
                onBack = { nav.popBackStack() },
                onRegistered = {
                    nav.navigate(Routes.Login.path) {
                        popUpTo(Routes.HomeRoot.path) { inclusive = false }
                        launchSingleTop = true
                    }
                }
            )
        }

        composable(Routes.RecoverPassword.path) {
            RecuperarPasswordScreen(
                onBack = { nav.popBackStack() },
                onSent = {
                    nav.navigate(Routes.Login.path) {
                        popUpTo(Routes.HomeRoot.path) { inclusive = false }
                        launchSingleTop = true
                    }
                }
            )
        }
    }
}
