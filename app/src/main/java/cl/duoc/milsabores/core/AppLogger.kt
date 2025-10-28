package cl.duoc.milsabores.core

import android.util.Log

/**
 * Utilidad de logging centralizada para toda la aplicación
 * Facilita debugging y seguimiento de errores
 */
object AppLogger {

    private const val TAG = "MilSabores"
    private var isDebugMode = true

    /**
     * Log de información general
     */
    fun info(message: String, tag: String = TAG) {
        if (isDebugMode) {
            Log.i(tag, message)
        }
    }

    /**
     * Log de errores
     */
    fun error(message: String, throwable: Throwable? = null, tag: String = TAG) {
        if (isDebugMode) {
            if (throwable != null) {
                Log.e(tag, message, throwable)
            } else {
                Log.e(tag, message)
            }
        }
    }

    /**
     * Log de advertencias
     */
    fun warning(message: String, tag: String = TAG) {
        if (isDebugMode) {
            Log.w(tag, message)
        }
    }

    /**
     * Log de debug
     */
    fun debug(message: String, tag: String = TAG) {
        if (isDebugMode) {
            Log.d(tag, message)
        }
    }

    /**
     * Log de acción del usuario
     */
    fun userAction(action: String, details: String = "") {
        if (isDebugMode) {
            Log.d("UserAction", "$action${if (details.isNotEmpty()) " - $details" else ""}")
        }
    }

    /**
     * Log de rendimiento
     */
    fun performance(operation: String, durationMs: Long) {
        if (isDebugMode) {
            Log.d("Performance", "$operation completed in ${durationMs}ms")
        }
    }

    /**
     * Habilitar o deshabilitar logging
     */
    fun setDebugMode(enabled: Boolean) {
        isDebugMode = enabled
    }
}

