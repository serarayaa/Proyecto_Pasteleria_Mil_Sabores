#!/usr/bin/env kotlin

package cl.duoc.milsabores.ui.login

import org.junit.Assert.*
import org.junit.Test

class LoginViewModelTest {

    @Test
    fun `cuando el email es invalido se setea emailError y no se marca loggedIn`() {
        val vm = LoginViewModel()
        vm.onEmailChange("correo-malo")
        vm.onPasswordChange("123456")

        vm.login()
        val state = vm.ui.value

        assertNotNull(state.emailError)
        assertFalse(state.loggedIn)
    }

    @Test
    fun `cuando la password es muy corta se setea passwordError`() {
        val vm = LoginViewModel()
        vm.onEmailChange("usuario@milsabores.cl")
        vm.onPasswordChange("123")

        vm.login()
        val state = vm.ui.value

        assertNotNull(state.passwordError)
        assertFalse(state.loggedIn)
    }

    @Test
    fun `cuando email y password son validos no deja errores de formato`() {
        val vm = LoginViewModel()
        vm.onEmailChange("usuario@milsabores.cl")
        vm.onPasswordChange("123456")

        vm.login()
        val state = vm.ui.value

        assertNull(state.emailError)
        assertNull(state.passwordError)
    }
}
