package cl.duoc.milsabores.data.remote

import cl.duoc.milsabores.data.remote.dto.CrearUsuarioRequest
import cl.duoc.milsabores.data.remote.dto.LoginRequest
import cl.duoc.milsabores.data.remote.dto.UsuarioResponseDto
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*

interface AuthApiService {

    // Crear usuario (registro)
    @POST("api/usuarios")
    suspend fun crearUsuario(
        @Body req: CrearUsuarioRequest
    ): Response<UsuarioResponseDto>

    // Login con email + password como form-data/query (como tu backend)
    @FormUrlEncoded
    @POST("api/auth/login")
    suspend fun login(
        @Field("mail") mail: String,
        @Field("password") password: String
    ): Response<UsuarioResponseDto>

    // Buscar usuario por idFirebase
    @GET("api/usuarios/firebase/{idFirebase}")
    suspend fun buscarPorFirebase(
        @Path("idFirebase") idFirebase: String
    ): Response<UsuarioResponseDto>

    // Actualizar nombre
    @FormUrlEncoded
    @PATCH("api/usuarios/{rut}/nombre")
    suspend fun actualizarNombre(
        @Path("rut") rut: String,
        @Field("nuevoNombre") nuevoNombre: String
    ): Response<UsuarioResponseDto>

    // Subir imagen
    @Multipart
    @PATCH("api/usuarios/{rut}/imagen")
    suspend fun subirImagen(
        @Path("rut") rut: String,
        @Part imagen: MultipartBody.Part
    ): Response<UsuarioResponseDto>
}
