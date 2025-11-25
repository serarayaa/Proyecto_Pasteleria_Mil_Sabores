package cl.duoc.milsabores.data.repository

import cl.duoc.milsabores.data.remote.AuthApiService
import cl.duoc.milsabores.data.remote.RetrofitClient
import cl.duoc.milsabores.data.remote.dto.CrearUsuarioRequest
import cl.duoc.milsabores.data.remote.dto.LoginRequest
import cl.duoc.milsabores.data.remote.dto.UsuarioResponseDto

class AuthRepository(
    private val api: AuthApiService = RetrofitClient.authApi
) {

    suspend fun login(email: String, password: String): UsuarioResponseDto? {
        val response = api.login(LoginRequest(email, password))
        return response.body()
    }

    suspend fun registrar(req: CrearUsuarioRequest): UsuarioResponseDto? {
        val response = api.registrar(req)
        return response.body()
    }
}
