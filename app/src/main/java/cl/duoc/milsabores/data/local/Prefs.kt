package cl.duoc.milsabores.data.local

import android.content.Context
import androidx.core.content.edit

class Prefs(context: Context) {
    private val sp = context.getSharedPreferences("milsabores_prefs", Context.MODE_PRIVATE)

    var darkMode: Boolean
        get() = sp.getBoolean(KEY_DARK_MODE, false)
        set(value) = sp.edit { putBoolean(KEY_DARK_MODE, value) }

    companion object {
        private const val KEY_DARK_MODE = "dark_mode"
    }
}

