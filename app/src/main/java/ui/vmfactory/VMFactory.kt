package cl.duoc.milsabores.vmfactory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import cl.duoc.milsabores.data.media.MediaRepository
import cl.duoc.milsabores.ui.profile.ProfileViewModel
import com.google.firebase.auth.FirebaseAuth

class ProfileVMFactory(
    private val auth: FirebaseAuth,
    private val mediaRepo: MediaRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProfileViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ProfileViewModel(auth, mediaRepo) as T
        }
        throw IllegalArgumentException("VM no soportado: $modelClass")
    }
}
