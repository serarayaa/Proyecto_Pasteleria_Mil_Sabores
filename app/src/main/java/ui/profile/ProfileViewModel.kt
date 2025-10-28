package cl.duoc.milsabores.ui.profile

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cl.duoc.milsabores.data.local.ProfilePhotoManager
import cl.duoc.milsabores.data.media.MediaRepository
import com.google.firebase.auth.FirebaseAuth
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

class ProfileViewModel(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val mediaRepo: MediaRepository = MediaRepository(),
    private val photoManager: ProfilePhotoManager? = null
) : ViewModel() {

    private val _ui = MutableStateFlow(ProfileUiState())
    val ui: StateFlow<ProfileUiState> = _ui

    init {
        val u = auth.currentUser
        _ui.update {
            it.copy(
                uid = u?.uid,
                email = u?.email,
                displayName = u?.displayName
            )
        }
        // Cargar foto de perfil guardada
        u?.uid?.let { uid ->
            photoManager?.let { loadProfilePhoto(uid) }
        }
    }

    /**
     * Carga la foto de perfil guardada localmente
     */
    private fun loadProfilePhoto(userId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val photoUri = photoManager?.getProfilePhotoUri(userId)
                _ui.update { it.copy(profilePhotoUri = photoUri) }
            } catch (e: Exception) {
                _ui.update { it.copy(error = "Error al cargar foto: ${e.message}") }
            }
        }
    }

    /**
     * Guarda una nueva foto de perfil localmente
     * Retorna true si se guardó exitosamente, false si hubo error
     */
    fun saveProfilePhoto(context: Context, photoUri: Uri): Boolean {
        val userId = _ui.value.uid ?: return false

        var result = false
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _ui.update { it.copy(isLoading = true, error = null) }

                val success = photoManager?.saveProfilePhoto(userId, photoUri) ?: false

                if (success) {
                    // Recargar la URI guardada
                    val savedUri = photoManager?.getProfilePhotoUri(userId)
                    _ui.update {
                        it.copy(
                            profilePhotoUri = savedUri,
                            isLoading = false
                        )
                    }
                    result = true
                } else {
                    _ui.update {
                        it.copy(
                            error = "No se pudo guardar la foto",
                            isLoading = false
                        )
                    }
                    result = false
                }
            } catch (e: Exception) {
                _ui.update {
                    it.copy(
                        error = "Error: ${e.message}",
                        isLoading = false
                    )
                }
                result = false
            }
        }
        return result
    }

    /**
     * Elimina la foto de perfil
     */
    fun deleteProfilePhoto() {
        val userId = _ui.value.uid ?: return

        viewModelScope.launch(Dispatchers.IO) {
            try {
                photoManager?.deleteProfilePhoto(userId)
                _ui.update { it.copy(profilePhotoUri = null) }
            } catch (e: Exception) {
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
