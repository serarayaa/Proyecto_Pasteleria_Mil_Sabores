package cl.duoc.milsabores.data.repository

import cl.duoc.milsabores.data.remote.AuthApiService
import cl.duoc.milsabores.data.remote.RetrofitClient
import cl.duoc.milsabores.data.remote.dto.CrearUsuarioRequest
import cl.duoc.milsabores.data.remote.dto.LoginRequest
import cl.duoc.milsabores.data.remote.dto.UsuarioResponseDto
import cl.duoc.milsabores.data.remote.dto.ActualizarFotoRequest

class AuthRepository(
    private val api: AuthApiService = RetrofitClient.authApi
) {

    suspend fun login(request: LoginRequest): UsuarioResponseDto {
        return api.login(request)
    }

    suspend fun registrar(request: CrearUsuarioRequest): UsuarioResponseDto {
        return api.registrar(request)
    }

    /**
     * Sube la foto de perfil en Base64 al backend.
     * Asumimos que `rut` es el identificador del usuario en la API.
     */
    suspend fun actualizarFotoPerfil(rut: String, imagenBase64: String) {
        val request = ActualizarFotoRequest(imagenBase64 = imagenBase64)
        // No nos interesa mucho la respuesta, pero podrías revisarla si quieres
        api.actualizarFotoPerfil(rut, request)
    }
}
