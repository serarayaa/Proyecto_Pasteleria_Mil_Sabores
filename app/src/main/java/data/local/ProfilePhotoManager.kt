package cl.duoc.milsabores.data.local

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import cl.duoc.milsabores.core.AppLogger
import java.io.File
import java.io.FileOutputStream

/**
 * Gestor de fotos de perfil almacenadas localmente
 * Maneja guardado, carga y eliminación de fotos del usuario
 */
class ProfilePhotoManager(private val context: Context) {

    private val profilePhotosDir = File(context.filesDir, "profile_photos").apply {
        if (!exists()) {
            mkdirs()
            AppLogger.info("Directorio de fotos de perfil creado")
        }
    }

    /**
     * Obtiene la ruta de la foto de perfil para un usuario específico
     */
    fun getProfilePhotoFile(userId: String): File {
        return File(profilePhotosDir, "profile_${userId}.jpg")
    }

    /**
     * Guarda una foto de perfil desde una URI
     * @param userId ID del usuario
     * @param photoUri URI de la foto a guardar
     * @return true si se guardó exitosamente
     */
    fun saveProfilePhoto(userId: String, photoUri: Uri): Boolean {
        return try {
            AppLogger.debug("Intentando guardar foto para usuario: $userId")

            val inputStream = context.contentResolver.openInputStream(photoUri)
                ?: return false.also {
                    AppLogger.error("No se pudo abrir el stream de entrada para la URI: $photoUri")
                }

            val bitmap = BitmapFactory.decodeStream(inputStream)
            inputStream.close()

            if (bitmap != null) {
                AppLogger.debug("Bitmap decodificado exitosamente: ${bitmap.width}x${bitmap.height}")

                // Validar que el directorio exista
                if (!profilePhotosDir.exists()) {
                    profilePhotosDir.mkdirs()
                    AppLogger.info("Directorio recreado")
                }

                val photoFile = getProfilePhotoFile(userId)

                // Eliminar archivo anterior si existe
                if (photoFile.exists()) {
                    photoFile.delete()
                    AppLogger.info("Archivo anterior eliminado")
                }

                FileOutputStream(photoFile).use { outputStream ->
                    // Comprimir a 80% de calidad para ahorrar espacio
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream)
                    outputStream.flush()
                    AppLogger.info("Foto guardada exitosamente: ${photoFile.absolutePath}")
                }
                bitmap.recycle()
                true
            } else {
                AppLogger.error("No se pudo decodificar el bitmap desde la URI")
                false
            }
        } catch (e: SecurityException) {
            AppLogger.error("Error de seguridad al guardar foto", e)
            false
        } catch (e: java.io.IOException) {
            AppLogger.error("Error de I/O al guardar foto", e)
            false
        } catch (e: Exception) {
            AppLogger.error("Error inesperado al guardar foto", e)
            false
        }
    }

    /**
     * Carga la foto de perfil de un usuario
     * @param userId ID del usuario
     * @return Bitmap de la foto o null si no existe
     */
    fun loadProfilePhoto(userId: String): Bitmap? {
        return try {
            val photoFile = getProfilePhotoFile(userId)
            if (photoFile.exists()) {
                BitmapFactory.decodeFile(photoFile.absolutePath)
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Obtiene el URI de la foto de perfil guardada
     * @param userId ID del usuario
     * @return Uri de la foto o null si no existe
     */
    fun getProfilePhotoUri(userId: String): Uri? {
        return try {
            val photoFile = getProfilePhotoFile(userId)
            if (photoFile.exists()) {
                Uri.fromFile(photoFile)
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Verifica si un usuario tiene foto de perfil guardada
     */
    fun hasProfilePhoto(userId: String): Boolean {
        return getProfilePhotoFile(userId).exists()
    }

    /**
     * Elimina la foto de perfil de un usuario
     */
    fun deleteProfilePhoto(userId: String): Boolean {
        return try {
            val photoFile = getProfilePhotoFile(userId)
            photoFile.delete()
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    /**
     * Obtiene el tamaño en KB de la foto guardada
     */
    fun getProfilePhotoSizeKB(userId: String): Long {
        return try {
            val photoFile = getProfilePhotoFile(userId)
            if (photoFile.exists()) {
                photoFile.length() / 1024
            } else {
                0
            }
        } catch (e: Exception) {
            0
        }
    }
}

