// Top-level build file where you can add configuration options common to all sub-projects/modules.

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false

    // Room KSP
    id("com.google.devtools.ksp") version "2.0.21-1.0.27" apply false

    // 🔥 Plugin de Google Services para Firebase
    id("com.google.gms.google-services") version "4.4.2" apply false
}

// ⚠️ Se eliminó buildscript {...} porque ya NO se usa.
// Antes duplicaba el plugin de google-services.
// Con los plugins declarados arriba es más moderno, limpio y correcto.
