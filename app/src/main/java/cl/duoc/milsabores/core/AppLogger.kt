package cl.duoc.milsabores.core

import android.util.Log

// ===== API orientada a objetos =====
object AppLogger {
    private const val DEFAULT_TAG = "MilSabores"
    private const val ENABLE_LOGS = true // evita depender de BuildConfig

    // Nivel INFO
    fun info(message: String, tag: String = DEFAULT_TAG) {
        if (ENABLE_LOGS) Log.i(tag, message)
    }

    // Nivel DEBUG
    fun d(message: String, tag: String = DEFAULT_TAG) {
        if (ENABLE_LOGS) Log.d(tag, message)
    }

    // Nivel WARNING
    fun w(message: String, tag: String = DEFAULT_TAG, throwable: Throwable? = null) {
        if (throwable != null) Log.w(tag, message, throwable) else Log.w(tag, message)
    }

    // Nivel ERROR
    fun e(message: String, throwable: Throwable? = null, tag: String = DEFAULT_TAG) {
        if (throwable != null) Log.e(tag, message, throwable) else Log.e(tag, message)
    }

    // Alias compatibilidad
    fun i(message: String, tag: String = DEFAULT_TAG) = info(message, tag)
    fun warning(message: String, throwable: Throwable? = null, tag: String = DEFAULT_TAG) = w(message, tag, throwable)
    fun error(message: String, throwable: Throwable? = null, tag: String = DEFAULT_TAG) = e(message, throwable, tag)
}

// ===== Helpers top-level (compatibilidad con llamadas existentes) =====
fun info(message: String) = AppLogger.info(message)
fun warning(message: String, throwable: Throwable? = null) = AppLogger.warning(message, throwable)
fun error(message: String, throwable: Throwable? = null) = AppLogger.error(message, throwable)
