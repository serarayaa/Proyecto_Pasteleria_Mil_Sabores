// app/src/main/java/cl/duoc/milsabores/cl.duoc.milsabores.repository/auth/AuthRepository.kt
package cl.duoc.milsabores.repository.auth

import cl.duoc.milsabores.core.AppLogger
import cl.duoc.milsabores.ui.model.User
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await

class AuthRepository(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) {
    suspend fun login(email: String, pass: String): User? {
        AppLogger.d("Intentando login con $email", "AuthRepo")
        val res = auth.signInWithEmailAndPassword(email, pass).await()
        val fu = res.user ?: return null
        AppLogger.i("Login OK uid=${fu.uid}", "AuthRepo")
        return User(uid = fu.uid, email = fu.email ?: email)
    }

    suspend fun register(email: String, pass: String): User? {
        val res = auth.createUserWithEmailAndPassword(email, pass).await()
        val fu = res.user ?: return null
        return User(uid = fu.uid, email = fu.email ?: email)
    }

    suspend fun sendPasswordReset(email: String) {
        auth.sendPasswordResetEmail(email).await()
    }

    fun currentUser() = auth.currentUser
    fun signOut() = auth.signOut()
}
