package cl.duoc.milsabores.ui.login

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class LoginViewModelTest {

    private val dispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `email invalido marca error`() {
        val vm = LoginViewModel()
        vm.onEmailChange("correo-malo")
        vm.onPasswordChange("123456")

        vm.login()
        val state = vm.ui.value

        assertNotNull(state.emailError)
        assertFalse(state.loggedIn)
    }

    @Test
    fun `password muy corta marca error`() {
        val vm = LoginViewModel()
        vm.onEmailChange("usuario@milsabores.cl")
        vm.onPasswordChange("123")

        vm.login()
        val state = vm.ui.value

        assertNotNull(state.passwordError)
        assertFalse(state.loggedIn)
    }

    @Test
    fun `email y password correctos no muestran errores de formato`() {
        val vm = LoginViewModel()
        vm.onEmailChange("usuario@milsabores.cl")
        vm.onPasswordChange("123456")

        vm.login()
        val state = vm.ui.value

        assertNull(state.emailError)
        assertNull(state.passwordError)
    }
}
