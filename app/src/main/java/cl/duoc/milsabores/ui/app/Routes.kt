package cl.duoc.milsabores.ui.app

sealed class Routes(val path: String) {
    data object HomeRoot : Routes("home")
    data object Login : Routes("login")
    data object Principal : Routes("principal")
    data object Register : Routes("register")
    data object RecoverPassword : Routes("recover")
}
