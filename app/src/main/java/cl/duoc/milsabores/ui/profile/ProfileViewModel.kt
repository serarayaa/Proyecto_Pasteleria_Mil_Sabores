package cl.duoc.milsabores.ui.profile

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.milsabores.core.ImagenUtils
import cl.duoc.milsabores.data.local.ProfilePhotoManager
import cl.duoc.milsabores.data.media.MediaRepository
import cl.duoc.milsabores.data.repository.AuthRepository
import cl.duoc.milsabores.core.AppLogger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ProfileUiState(
    val uid: String? = null,
    val email: String? = null,
    val displayName: String? = null,
    val profilePhotoUri: Uri? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

/**
 * Versión sin Firebase.
 * Usa un usuario "local" por defecto. Si luego quieres,
 * podemos agregar un método para setear estos datos desde el login.
 */
class ProfileViewModel(
    private val mediaRepo: MediaRepository = MediaRepository(),
    private val photoManager: ProfilePhotoManager? = null,
    private val authRepository: AuthRepository = AuthRepository()
) : ViewModel() {

    private val _ui = MutableStateFlow(
        ProfileUiState(
            uid = "usuario_local",
            email = "usuario@milsabores.cl",
            displayName = "Usuario Mil Sabores"
        )
    )
    val ui: StateFlow<ProfileUiState> = _ui

    /** Permite actualizar datos de usuario (ej. después del login). */
    fun setUserData(uid: String?, email: String?, displayName: String?) {
        _ui.update {
            it.copy(
                uid = uid ?: it.uid,
                email = email ?: it.email,
                displayName = displayName ?: it.displayName
            )
        }
    }

    /** Recarga la foto guardada localmente (usa fallback si no hay inyección). */
    fun refreshProfilePhoto(context: Context) {
        val userId = _ui.value.uid ?: return
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val mgr = photoManager ?: ProfilePhotoManager(context)
                val photoUri = mgr.getProfilePhotoUri(userId)
                _ui.update { it.copy(profilePhotoUri = photoUri) }
            } catch (e: Exception) {
                AppLogger.e("Error al cargar foto de perfil", e, "ProfileVM")
                _ui.update { it.copy(error = "Error al cargar foto: ${e.message}") }
            }
        }
    }

    /** Guarda una nueva foto de perfil localmente y la envía al backend en Base64. */
    fun saveProfilePhoto(context: Context, photoUri: Uri) {
        val userId = _ui.value.uid ?: return   // aquí asumimos que uid = RUT
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _ui.update { it.copy(isLoading = true, error = null) }

                val mgr = photoManager ?: ProfilePhotoManager(context)
                val success = mgr.saveProfilePhoto(userId, photoUri)

                if (!success) {
                    _ui.update {
                        it.copy(
                            isLoading = false,
                            error = "No se pudo guardar la foto"
                        )
                    }
                    return@launch
                }

                // 1) Actualizar la URI local en la UI
                val savedUri = mgr.getProfilePhotoUri(userId)

                // 2) Leer la imagen desde el Uri y convertirla a Base64
                try {
                    val inputStream = context.contentResolver.openInputStream(photoUri)
                    val bitmap = inputStream?.use { BitmapFactory.decodeStream(it) }

                    if (bitmap != null) {
                        val base64 = ImagenUtils.bitmapToBase64(bitmap)
                        // 3) Enviar al backend (si falla, solo dejamos log, no rompemos la UI)
                        try {
                            authRepository.actualizarFotoPerfil(userId, base64)
                            AppLogger.i("Foto de perfil enviada al backend para $userId", "ProfileVM")
                        } catch (e: Exception) {
                            AppLogger.e("Error al enviar foto al backend", e, "ProfileVM")
                        }
                    } else {
                        AppLogger.e("Bitmap nulo al leer Uri de la foto", null, "ProfileVM")
                    }
                } catch (e: Exception) {
                    AppLogger.e("Error al procesar bitmap para Base64", e, "ProfileVM")
                }

                _ui.update { it.copy(profilePhotoUri = savedUri, isLoading = false) }

            } catch (e: Exception) {
                AppLogger.e("Error general en saveProfilePhoto", e, "ProfileVM")
                _ui.update { it.copy(error = "Error: ${e.message}", isLoading = false) }
            }
        }
    }

    /** Elimina la foto de perfil local. */
    fun deleteProfilePhoto(context: Context) {
        val userId = _ui.value.uid ?: return
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val mgr = photoManager ?: ProfilePhotoManager(context)
                mgr.deleteProfilePhoto(userId)
                _ui.update { it.copy(profilePhotoUri = null) }
            } catch (e: Exception) {
                AppLogger.e("Error al eliminar foto de perfil", e, "ProfileVM")
                _ui.update { it.copy(error = "Error al eliminar: ${e.message}") }
            }
        }
    }

    fun setError(message: String?) {
        _ui.update { it.copy(error = message) }
    }

    /** Crea el destino en la galería usando tu MediaRepository. */
    fun createDestinationUriForCurrentUser(context: Context): Uri? {
        val uid = _ui.value.uid ?: return null
        return mediaRepo.createImageUriForUser(context, uid)
    }
}
