package cl.milsabores.movil.data.remote

import cl.milsabores.movil.data.remote.dto.LoginRequest
import cl.milsabores.movil.data.remote.dto.CrearUsuarioRequest
import cl.milsabores.movil.data.remote.dto.UsuarioResponseDto
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface AuthApiService {

    @POST("/api/usuarios")
    suspend fun crearUsuario(
        @Body body: CrearUsuarioRequest
    ): Response<UsuarioResponseDto>

    @POST("/api/usuarios/login")
    suspend fun login(
        @Body req: LoginRequest
    ): Response<UsuarioResponseDto>

    @GET("/api/usuarios/firebase/{idFirebase}")
    suspend fun buscarPorFirebase(
        @Path("idFirebase") id: String
    ): Response<UsuarioResponseDto>

    @PATCH("/api/usuarios/{rut}/nombre")
    suspend fun actualizarNombre(
        @Path("rut") rut: String,
        @Body nombreMap: Map<String, String>
    ): Response<UsuarioResponseDto>

    @Multipart
    @PATCH("/api/usuarios/{rut}/imagen")
    suspend fun subirImagen(
        @Path("rut") rut: String,
        @Part file: MultipartBody.Part
    ): Response<UsuarioResponseDto>
}
