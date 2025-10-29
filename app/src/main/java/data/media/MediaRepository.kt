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
import cl.duoc.milsabores.core.AppLogger
import java.io.File

class MediaRepository {

    @ChecksSdkIntAtLeast(api = Build.VERSION_CODES.Q)
    private fun isApi29Plus() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q

    /**
     * Crea un URI de destino para guardar una imagen de perfil.
     * - En Android 10+ usa MediaStore con RELATIVE_PATH + IS_PENDING.
     * - En <29 usa cache interna (no requiere permisos de almacenamiento).
     *
     * IMPORTANTE: En 29+ debes llamar a [markImageAsComplete] cuando termines de escribir.
     */
    fun createImageUriForUser(context: Context, uid: String): Uri? {
        val fileName = "MilSabores_${uid}_${System.currentTimeMillis()}.jpg"

        return if (isApi29Plus()) {
            val values = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
                put(MediaStore.Images.Media.RELATIVE_PATH, "${Environment.DIRECTORY_PICTURES}/MilSabores/Profile")
                put(MediaStore.Images.Media.IS_PENDING, 1) // ✅ evita que aparezca incompleta
            }
            val collection = MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
            context.contentResolver.insert(collection, values).also {
                AppLogger.i("URI creado en MediaStore: $it", "MediaRepo")
            }
        } else {
            // Fallback sin permisos: archivo en cache de la app (no visible en galería)
            val cacheDir = File(context.cacheDir, "images/profile").apply { mkdirs() }
            val target = File(cacheDir, fileName)
            runCatching {
                if (!target.exists()) target.createNewFile()
                Uri.fromFile(target)
            }.onFailure { AppLogger.e("Error creando archivo cache", it, "MediaRepo") }
                .getOrNull()
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
