// app/src/main/java/repository/auth/AuthRepository.kt
package cl.duoc.milsabores.repository.auth

import android.util.Log
import cl.duoc.milsabores.ui.model.User
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await

class AuthRepository(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) {
    suspend fun login(email: String, pass: String): User? {
        Log.d("AuthRepository", "Intentando login con email: '$email' (longitud contraseña: ${pass.length})")

        try {
            val res = auth.signInWithEmailAndPassword(email, pass).await()
            val fu = res.user

            if (fu == null) {
                Log.e("AuthRepository", "Login exitoso pero user es null")
                return null
            }

            Log.d("AuthRepository", "Login exitoso - UID: ${fu.uid}, Email: ${fu.email}")
            return User(uid = fu.uid, email = fu.email ?: email)
        } catch (e: Exception) {
            Log.e("AuthRepository", "Error en login: ${e.javaClass.simpleName} - ${e.message}", e)
            throw e
        }
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
