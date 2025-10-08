// app/src/main/java/repository/auth/AuthRepository.kt
package cl.duoc.milsabores.repository.auth

import cl.duoc.milsabores.ui.model.User
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await

class AuthRepository(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) {
    suspend fun login(email: String, pass: String): User? {
        val res = auth.signInWithEmailAndPassword(email, pass).await()
        val fu = res.user ?: return null
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
