package cl.duoc.milsabores.data.media

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.MediaStore

class MediaRepository {

    // Crea un URI para guardar una imagen de perfil de usuario
    fun createImageUriForUser(context: Context, uid: String): Uri? {
        val fileName = "MilSabores_${uid}_${System.currentTimeMillis()}.jpg"

        val values = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")

            // En Android 10+ (API 29), se puede definir la ruta relativa
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/MilSabores/Profile")
            }
        }

        val collection = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
        } else {
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        }

        return context.contentResolver.insert(collection, values)
    }
}
