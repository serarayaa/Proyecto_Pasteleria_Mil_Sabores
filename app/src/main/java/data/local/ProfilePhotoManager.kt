package cl.duoc.milsabores.data.local

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import cl.duoc.milsabores.core.AppLogger
import java.io.File
import java.io.FileOutputStream

class ProfilePhotoManager(private val context: Context) {

    private val profilePhotosDir = File(context.filesDir, "profile_photos").apply {
        if (!exists() && mkdirs()) {
            AppLogger.i("Directorio de fotos de perfil creado: $absolutePath", "ProfilePhoto")
        }
    }

    fun getProfilePhotoFile(userId: String): File =
        File(profilePhotosDir, "profile_${userId}.jpg")

    fun saveProfilePhoto(userId: String, photoUri: Uri): Boolean = try {
        AppLogger.d("Guardando foto para uid=$userId uri=$photoUri", "ProfilePhoto")

        val photoFile = getProfilePhotoFile(userId)
        context.contentResolver.openInputStream(photoUri)?.use { input ->
            val bitmap = BitmapFactory.decodeStream(input) ?: run {
                AppLogger.e("Bitmap nulo desde $photoUri", null, "ProfilePhoto"); return false
            }

            // Asegura directorio
            if (!profilePhotosDir.exists()) profilePhotosDir.mkdirs()

            if (photoFile.exists()) {
                if (photoFile.delete()) AppLogger.d("Foto anterior eliminada", "ProfilePhoto")
            }

            FileOutputStream(photoFile).use { output ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 80, output)
                output.flush()
            }
            AppLogger.i("Foto guardada: ${photoFile.absolutePath}", "ProfilePhoto")
            true
        } ?: false.also { AppLogger.e("No se pudo abrir inputStream de $photoUri", null, "ProfilePhoto") }
    } catch (e: SecurityException) {
        AppLogger.e("SecurityException guardando foto", e, "ProfilePhoto"); false
    } catch (e: Exception) {
        AppLogger.e("Error inesperado guardando foto", e, "ProfilePhoto"); false
    }

    fun loadProfilePhoto(userId: String): Bitmap? = try {
        val photoFile = getProfilePhotoFile(userId)
        if (photoFile.exists()) BitmapFactory.decodeFile(photoFile.absolutePath) else null
    } catch (e: Exception) {
        AppLogger.e("Error cargando foto", e, "ProfilePhoto"); null
    }

    fun getProfilePhotoUri(userId: String): Uri? = try {
        val photoFile = getProfilePhotoFile(userId)
        if (photoFile.exists()) Uri.fromFile(photoFile) else null
    } catch (e: Exception) {
        AppLogger.e("Error obteniendo URI de foto", e, "ProfilePhoto"); null
    }

    fun hasProfilePhoto(userId: String): Boolean = getProfilePhotoFile(userId).exists()

    fun deleteProfilePhoto(userId: String): Boolean = try {
        val deleted = getProfilePhotoFile(userId).delete()
        if (deleted) AppLogger.d("Foto eliminada para uid=$userId", "ProfilePhoto")
        deleted
    } catch (e: Exception) {
        AppLogger.e("Error eliminando foto", e, "ProfilePhoto"); false
    }

    fun getProfilePhotoSizeKB(userId: String): Long = try {
        val file = getProfilePhotoFile(userId)
        if (file.exists()) file.length() / 1024 else 0
    } catch (_: Exception) {
        0
    }
}
