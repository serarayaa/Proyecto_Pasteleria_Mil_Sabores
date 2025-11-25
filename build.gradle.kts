plugins {
    // Plugins globales que los módulos pueden usar
    id("com.android.application") version "8.1.2" apply false
    id("org.jetbrains.kotlin.android") version "1.9.0" apply false

    // 👇 NECESARIO PARA QUE EL APP RECONOZCA el plugin de Hilt
    id("com.google.dagger.hilt.android") version "2.48" apply false
}
