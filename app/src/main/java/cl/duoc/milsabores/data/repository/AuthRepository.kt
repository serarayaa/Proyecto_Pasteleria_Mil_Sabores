package cl.duoc.milsabores.data.repository

import cl.duoc.milsabores.data.remote.AuthApiService
import cl.duoc.milsabores.data.remote.RetrofitClient
import cl.duoc.milsabores.data.remote.dto.CrearUsuarioRequest
import cl.duoc.milsabores.data.remote.dto.UsuarioResponseDto

class AuthRepository(
    private val api: AuthApiService = RetrofitClient.authApi
) {

    // LOGIN → devuelve usuario o null si fallo
    suspend fun login(email: String, password: String): UsuarioResponseDto? {
        val response = api.login(email, password)
        return if (response.isSuccessful) response.body() else null
    }

    // REGISTRO
    suspend fun registrar(req: CrearUsuarioRequest): UsuarioResponseDto? {
        val response = api.crearUsuario(req)
        return if (response.isSuccessful) response.body() else null
    }

    // CARGAR USUARIO POR ID FIREBASE
    suspend fun cargarPorFirebase(uid: String): UsuarioResponseDto? {
        val response = api.buscarPorFirebase(uid)
        return if (response.isSuccessful) response.body() else null
    }

    // ACTUALIZAR NOMBRE
    suspend fun actualizarNombre(rut: String, nuevoNombre: String): UsuarioResponseDto? {
        val response = api.actualizarNombre(rut, nuevoNombre)
        return if (response.isSuccessful) response.body() else null
    }
}
