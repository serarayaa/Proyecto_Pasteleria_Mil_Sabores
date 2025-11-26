package cl.duoc.milsabores.ui.pedidos

import cl.duoc.milsabores.fakes.FakeApplication
import cl.duoc.milsabores.fakes.FakeAuthProvider
import cl.duoc.milsabores.fakes.FakePedidosStorage
import cl.duoc.milsabores.model.Pedido
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class PedidosViewModelTest {

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
    fun `cuando no hay usuario logueado se cargan pedidos vacios`() = runTest {
        val vm = PedidosViewModel(
            application = FakeApplication(),
            storage = FakePedidosStorage(
                listOf(
                    Pedido("1", "USER123", 10000L),
                )
            ),
            authProvider = FakeAuthProvider(null) // NO LOGUEADO
        )

        advanceUntilIdle()

        val state = vm.ui.value

        assertTrue(state.pedidos.isEmpty())
        assertFalse(state.loading)
    }

    @Test
    fun `cuando hay usuario logueado se cargan pedidos ordenados por fecha desc`() = runTest {
        val pedidosIniciales = listOf(
            Pedido("1", "UID", 1000L),
            Pedido("2", "UID", 5000L),
            Pedido("3", "UID", 3000L)
        )

        val vm = PedidosViewModel(
            application = FakeApplication(),
            storage = FakePedidosStorage(pedidosIniciales),
            authProvider = FakeAuthProvider("UID")
        )

        advanceUntilIdle()

        val fechas = vm.ui.value.pedidos.map { it.fecha }

        assertEquals(listOf(5000L, 3000L, 1000L), fechas)
    }

    @Test
    fun `cancelar pedido elimina el pedido y muestra mensaje`() = runTest {
        val storage = FakePedidosStorage(
            listOf(
                Pedido("1", "UID", 1000L),
                Pedido("2", "UID", 2000L)
            )
        )

        val vm = PedidosViewModel(
            application = FakeApplication(),
            storage = storage,
            authProvider = FakeAuthProvider("UID")
        )

        advanceUntilIdle()

        vm.cancelarPedido("1")
        advanceUntilIdle()

        val state = vm.ui.value

        assertEquals(1, state.pedidos.size)
        assertEquals("Pedido cancelado", state.message)
    }
}
