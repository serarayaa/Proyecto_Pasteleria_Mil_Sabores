package cl.duoc.milsabores.ui.app

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController
import cl.duoc.milsabores.ui.home.HomeScreen
import cl.duoc.milsabores.ui.login.LoginScreen
import cl.duoc.milsabores.ui.principal.PrincipalScreen
import cl.duoc.milsabores.ui.recover.RecuperarPasswordScreen
import cl.duoc.milsabores.ui.register.RegistrarseScreen
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun AppNavHost(onDarkModeChanged: (Boolean) -> Unit = {}) {
    val nav = rememberNavController()
    val dur = 300

    AnimatedNavHost(
        navController = nav,
        startDestination = Routes.HomeRoot.path,
        enterTransition = { slideInHorizontally(initialOffsetX = { it }, animationSpec = tween(dur)) },
        exitTransition  = { slideOutHorizontally(targetOffsetX = { -it / 2 }, animationSpec = tween(dur)) },
        popEnterTransition = { slideInHorizontally(initialOffsetX = { -it / 2 }, animationSpec = tween(dur)) },
        popExitTransition  = { slideOutHorizontally(targetOffsetX = { it }, animationSpec = tween(dur)) }
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
                        popUpTo(Routes.HomeRoot.path) { inclusive = false }
                    }
                }
            )
        }

        composable(Routes.Principal.path) {
            PrincipalScreen(
                onLogout = {
                    nav.navigate(Routes.HomeRoot.path) {
                        popUpTo(Routes.HomeRoot.path) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                onDarkModeChanged = onDarkModeChanged
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
