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

    // ===============================
    // TESTS ORIGINALES (SIN CAMBIOS)
    // ===============================

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

    // ===============================
    // TESTS NUEVOS (SIN BACKEND)
    // ===============================

    @Test
    fun `guestLogin crea sesion de invitado`() {
        val vm = LoginViewModel()

        vm.guestLogin()
        val state = vm.ui.value

        assertTrue(state.loggedIn)
        assertTrue(state.isGuest)
        assertNotNull(state.backendUser)
        assertEquals("GUEST", state.backendUser?.rut)
        assertEquals("invitado@milsabores.cl", state.backendUser?.email)
        assertEquals("Sesión de invitado", state.message)
    }

    @Test
    fun `messageConsumed limpia mensaje y error`() {
        val vm = LoginViewModel()

        // Dejamos un mensaje presente usando guestLogin
        vm.guestLogin()
        assertNotNull(vm.ui.value.message)

        vm.messageConsumed()
        val state = vm.ui.value

        assertNull(state.message)
        assertNull(state.error)
    }
}
