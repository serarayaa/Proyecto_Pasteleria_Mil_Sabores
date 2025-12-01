package cl.duoc.milsabores.core

import android.graphics.Bitmap
import android.util.Base64
import java.io.ByteArrayOutputStream

object ImagenUtils {

    fun bitmapToBase64(bitmap: Bitmap): String {
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, baos)
        val bytes = baos.toByteArray()
        // NO_WRAP para que no meta saltos de línea raros
        return Base64.encodeToString(bytes, Base64.NO_WRAP)
    }
}
