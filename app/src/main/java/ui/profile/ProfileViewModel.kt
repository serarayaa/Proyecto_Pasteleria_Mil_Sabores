package cl.duoc.milsabores.ui.profile

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import cl.duoc.milsabores.data.media.MediaRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

data class ProfileUiState(
    val uid: String? = null,
    val email: String? = null,
    val displayName: String? = null,
    val lastSavedPhoto: Uri? = null,
    val error: String? = null
)

class ProfileViewModel(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val mediaRepo: MediaRepository = MediaRepository()
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
    }

    fun setLastSavedPhoto(uri: Uri?) {
        _ui.update { it.copy(lastSavedPhoto = uri) }
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
