package cl.duoc.milsabores.data.remote

import cl.duoc.milsabores.data.remote.dto.CrearUsuarioRequest
import cl.duoc.milsabores.data.remote.dto.LoginRequest
import cl.duoc.milsabores.data.remote.dto.UsuarioResponseDto
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApiService {

    @POST("auth/login")
    suspend fun login(
        @Body request: LoginRequest
    ): UsuarioResponseDto

    @POST("auth/register")
    suspend fun registrar(
        @Body request: CrearUsuarioRequest
    ): UsuarioResponseDto
}
