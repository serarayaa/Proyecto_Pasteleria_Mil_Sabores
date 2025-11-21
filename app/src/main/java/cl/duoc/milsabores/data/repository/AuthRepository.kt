package cl.duoc.milsabores.repository.auth

import cl.duoc.milsabores.data.remote.RetrofitClient
import cl.duoc.milsabores.data.remote.dto.CrearUsuarioRequest
import cl.duoc.milsabores.data.remote.dto.LoginRequest
import cl.duoc.milsabores.data.remote.dto.UsuarioResponseDto

class AuthRepository {

    private val api = RetrofitClient.authApi

    suspend fun login(mail: String, password: String): UsuarioResponseDto? {
        val response = api.login(LoginRequest(mail, password))
        return if (response.isSuccessful) response.body() else null
    }

    suspend fun registrar(req: CrearUsuarioRequest): UsuarioResponseDto? {
        val response = api.crearUsuario(req)
        return if (response.isSuccessful) response.body() else null
    }

    suspend fun cargarPorFirebase(idFirebase: String): UsuarioResponseDto? {
        val response = api.buscarPorFirebase(idFirebase)
        return if (response.isSuccessful) response.body() else null
    }

    suspend fun actualizarNombre(rut: String, nuevoNombre: String): UsuarioResponseDto? {
        val body = mapOf("nuevoNombre" to nuevoNombre)
        val response = api.actualizarNombre(rut, body)
        return if (response.isSuccessful) response.body() else null
    }
}
