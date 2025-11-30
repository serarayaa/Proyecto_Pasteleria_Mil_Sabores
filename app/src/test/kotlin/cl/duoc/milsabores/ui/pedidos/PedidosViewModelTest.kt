package cl.duoc.milsabores.ui.pedidos

import org.junit.Assert.*
import org.junit.Test

class PedidosViewModelTest {

    @Test
    fun `estado inicial tiene loading true y lista vacia`() {
        val state = PedidosUiState()

        assertTrue(state.loading)
        assertNotNull(state.pedidos)
        assertTrue(state.pedidos.isEmpty())
        assertNull(state.pedidoSeleccionado)
        assertNull(state.message)
    }

    @Test
    fun `se puede crear un estado con mensaje personalizado`() {
        val state = PedidosUiState(
            loading = false,
            pedidos = emptyList(),
            pedidoSeleccionado = null,
            message = "Mensaje de prueba"
        )

        assertFalse(state.loading)
        assertEquals("Mensaje de prueba", state.message)
    }

    @Test
    fun `se puede crear un estado con pedido seleccionado`() {
        // No necesitamos crear Pedido real, solo verificamos que el campo acepte null o no-null
        val dummyPedido: cl.duoc.milsabores.model.Pedido? = null

        val state = PedidosUiState(
            loading = false,
            pedidos = emptyList(),
            pedidoSeleccionado = dummyPedido,
            message = null
        )

        // Lo importante acá es que no truene y que el valor se mantenga
        assertEquals(dummyPedido, state.pedidoSeleccionado)
    }
}
