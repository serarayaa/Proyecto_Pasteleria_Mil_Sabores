package cl.duoc.milsabores.data.remote

import cl.duoc.milsabores.data.remote.dto.CrearUsuarioRequest
import cl.duoc.milsabores.data.remote.dto.LoginRequest
import cl.duoc.milsabores.data.remote.dto.UsuarioResponseDto
import cl.duoc.milsabores.data.remote.dto.RecoverPasswordRequestDto
import cl.duoc.milsabores.data.remote.dto.ActualizarFotoRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface AuthApiService {

    @POST("auth/login")
    suspend fun login(
        @Body request: LoginRequest
    ): UsuarioResponseDto

    @POST("auth/register")
    suspend fun registrar(
        @Body request: CrearUsuarioRequest
    ): UsuarioResponseDto

    // ============================
    // Recuperar contraseña
    // ============================
    @POST("auth/recover-password")
    suspend fun recoverPassword(
        @Body request: RecoverPasswordRequestDto
    ): Response<Unit>

    // ============================
    // Nueva: Actualizar foto de perfil (Base64)
    // ============================
    @PUT("auth/usuarios/{rut}/foto")
    suspend fun actualizarFotoPerfil(
        @Path("rut") rut: String,
        @Body request: ActualizarFotoRequest
    ): Response<Unit>
}
