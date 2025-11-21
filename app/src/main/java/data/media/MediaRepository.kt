// app/src/main/java/cl/duoc/milsabores/data/media/MediaRepository.kt
package cl.duoc.milsabores.data.media

import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.annotation.ChecksSdkIntAtLeast
import androidx.core.content.FileProvider
import cl.duoc.milsabores.core.AppLogger
import java.io.File

class MediaRepository {

    @ChecksSdkIntAtLeast(api = Build.VERSION_CODES.Q)
    private fun isApi29Plus() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q

    /**
     * Crea un URI de destino para guardar una imagen de perfil usando FileProvider.
     * Compatible con todos los dispositivos (incluidos Huawei/Honor).
     * NO requiere permisos de almacenamiento externo.
     */
    fun createImageUriForUser(context: Context, uid: String): Uri? {
        val fileName = "MilSabores_${uid}_${System.currentTimeMillis()}.jpg"

        return try {
            // Crear directorio en cache interno
            val imagesDir = File(context.cacheDir, "images").apply {
                if (!exists()) {
                    mkdirs()
                    AppLogger.i("Directorio de imágenes creado: $absolutePath", "MediaRepo")
                }
            }

            // Crear archivo temporal
            val imageFile = File(imagesDir, fileName)

            // Crear URI con FileProvider
            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                imageFile
            )

            AppLogger.i("URI creado con FileProvider: $uri", "MediaRepo")
            uri
        } catch (e: Exception) {
            AppLogger.e("Error creando URI con FileProvider", e, "MediaRepo")
            null
        }
    }

    /**
     * Marca una imagen insertada en MediaStore (API 29+) como completada (IS_PENDING=0).
     * Debe llamarse luego de escribir los bytes.
     */
    fun markImageAsComplete(context: Context, uri: Uri): Boolean {
        if (!isApi29Plus()) return true // no aplica en <29
        return runCatching {
            val values = ContentValues().apply {
                put(MediaStore.Images.Media.IS_PENDING, 0)
            }
            context.contentResolver.update(uri, values, null, null) > 0
        }.onFailure { AppLogger.e("Error marcando imagen como completa", it, "MediaRepo") }
            .getOrDefault(false)
    }

    /**
     * Borra una imagen creada en MediaStore (29+) o un archivo en cache (<29).
     */
    fun delete(context: Context, uri: Uri): Boolean {
        return runCatching {
            if (isApi29Plus()) {
                context.contentResolver.delete(uri, null, null) > 0
            } else {
                // Uri de archivo local (cache)
                val f = File(uri.path ?: return false)
                f.exists() && f.delete()
            }
        }.onFailure { AppLogger.e("Error eliminando imagen $uri", it, "MediaRepo") }
            .getOrDefault(false)
    }

    /**
     * Helper opcional: guarda bytes en un URI (por ejemplo, desde un Bitmap.compress(...)).
     * Devuelve true si se escriben correctamente.
     */
    fun writeBytes(context: Context, uri: Uri, bytes: ByteArray): Boolean {
        return runCatching {
            context.contentResolver.openOutputStream(uri)?.use { out ->
                out.write(bytes)
                out.flush()
                true
            } ?: false
        }.onFailure { AppLogger.e("Error escribiendo bytes en $uri", it, "MediaRepo") }
            .getOrDefault(false)
    }

    /**
     * (Opcional) Obtiene el ID numérico en MediaStore (si aplica).
     */
    fun getMediaStoreId(uri: Uri): Long? = runCatching {
        ContentUris.parseId(uri)
    }.getOrNull()
}
