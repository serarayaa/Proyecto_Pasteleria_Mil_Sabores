package cl.duoc.milsabores.data.repository

import cl.duoc.milsabores.data.remote.AuthApiService
import cl.duoc.milsabores.data.remote.RetrofitClient
import cl.duoc.milsabores.data.remote.dto.CrearUsuarioRequest
import cl.duoc.milsabores.data.remote.dto.LoginRequest
import cl.duoc.milsabores.data.remote.dto.UsuarioResponseDto

class AuthRepository(
    private val api: AuthApiService = RetrofitClient.authApi
) {

    suspend fun login(request: LoginRequest): UsuarioResponseDto {
        return api.login(request)
    }

    suspend fun registrar(request: CrearUsuarioRequest): UsuarioResponseDto {
        return api.registrar(request)
    }
}
