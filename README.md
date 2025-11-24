# 🍰 Pastelería Mil Sabores — Aplicación Móvil Android

[![Kotlin](https://img.shields.io/badge/Kotlin-1.9.0-purple.svg)](https://kotlinlang.org/)
[![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-1.5.14-green.svg)](https://developer.android.com/jetpack/compose)
[![Firebase](https://img.shields.io/badge/Firebase-BOM%2033.2.0-orange.svg)](https://firebase.google.com/)
[![Material3](https://img.shields.io/badge/Material3-1.1.2-blue.svg)](https://m3.material.io/)

Última actualización: 2025-11-22

---

Resumen
-------
Aplicación móvil nativa Android para la gestión de pedidos de una pastelería. Implementa una UX moderna con Jetpack Compose, persistencia local con Room, sincronización con Firebase (Auth / Firestore / Analytics), arquitectura MVVM, Coroutines y Material Design 3.

Estado del proyecto
-------------------
- Versión (en `app/build.gradle.kts`): 1.0 (versionName)
- applicationId: `cl.duoc.milsabores`
- compileSdk: 36
- minSdk: 24
- targetSdk: 36
- Firebase project: `pasteleria-1000-sabores` (archivo `app/google-services.json` presente)

Tecnologías principales
-----------------------
- Kotlin
- Jetpack Compose
- AndroidX (ViewModel, Lifecycle, Navigation)
- Firebase (Authentication, Firestore, Analytics)
- Room (SQLite)
- Retrofit + OkHttp
- Coroutines

Requisitos (desarrollo)
-----------------------
- Windows / macOS / Linux
- Android Studio (recomendado: Arctic Fox o superior; preferible versión reciente que soporte Kotlin 1.9+)
- JDK 17
- Android SDK Platform 36
- Conexión a Internet para descargar dependencias

Preparación del entorno
-----------------------
1. Clona el repositorio:

    git clone "<URL-del-repo>"

2. Abre el proyecto en Android Studio (File → Open → selecciona la carpeta raíz `Proyecto_Pasteleria_Mil_Sabores`).
3. Verifica que tengas instalado el SDK 36 y el JDK 17 en Android Studio.

Configuración de Firebase
-------------------------
Este proyecto ya incluye un archivo `app/google-services.json`. Si vas a usar tu propio proyecto Firebase sigue estos pasos:

1. Ve a https://console.firebase.google.com/ y crea un proyecto.
2. Registra una app Android con el package name: `cl.duoc.milsabores` (o el que uses).
3. Descarga `google-services.json` y colócalo en `app/`.
4. Habilita los servicios necesarios (Authentication, Firestore, Analytics, Storage si lo vas a usar).

IMPORTANTE: `google-services.json` puede contener claves de API públicas; evita commitear archivos con credenciales privadas o secretos (keystores). Si tu equipo necesita usar variables privadas, usa `gradle.properties` o el sistema de CI para inyectarlas.

Permisos requeridos (extraídos de `AndroidManifest.xml`)
--------------------------------------------------------
- android.permission.INTERNET — acceso a red
- android.permission.VIBRATE — vibración de dispositivo
- android.permission.POST_NOTIFICATIONS — notificaciones (Android 13+)
- android.permission.CAMERA — uso de cámara
- android.permission.READ_MEDIA_IMAGES (Android 13+)
- android.permission.READ_EXTERNAL_STORAGE (para Android <= 12)

El manifiesto también declara un FileProvider para manejo de imágenes desde cámara.

Cómo compilar y ejecutar (Windows PowerShell)
---------------------------------------------
Desde la raíz del proyecto ejecuta los siguientes comandos en PowerShell:

- Limpiar y compilar la app (debug):

    .\gradlew clean assembleDebug

- Instalar en dispositivo/emulador conectado e iniciar (debug):

    .\gradlew installDebug

- Ejecutar lint:

    .\gradlew lint

- Ejecutar tests unitarios:

    .\gradlew test

Notas: para pruebas instrumentadas necesitarás un emulador o dispositivo y ejecutar:

    .\gradlew connectedAndroidTest

Consideraciones sobre firma y release
------------------------------------
- El proyecto contiene la configuración básica de `buildTypes` en `app/build.gradle.kts`.
- Para publicar en Play Store deberías crear un keystore y configurar `signingConfigs` en el `build.gradle.kts` o usar los mecanismos de firma por Gradle/Play.

Estructura relevante del proyecto
---------------------------------
- `app/` — módulo principal de la app
- `app/src/main/java/cl/duoc/milsabores/` — código fuente (Activities, ViewModels, Repositories, UI)
- `app/src/main/res/` — recursos (strings, themes, drawables)
- `app/google-services.json` — configuración Firebase (actualmente presente)

Buenas prácticas y secretos
---------------------------
- No guardes secretos (API keys privadas, keystore passwords) en el repositorio.
- Usa `gradle.properties` (local) o variables CI para valores sensibles.
- Si cambias `applicationId` recuerda registrar la nueva app en Firebase y reemplazar `google-services.json`.

Contribuir
----------
Si quieres contribuir:
1. Crea un fork y una rama con un nombre claro: `feature/<descripcion>` o `fix/<descripcion>`.
2. Sigue el estilo de código del proyecto (Kotlin idiomático, coroutines, MVVM).
3. Ejecuta `./gradlew build` y `./gradlew lint` antes de enviar el PR.
4. Abre un Pull Request describiendo el cambio y los pasos para probarlo.

Testing y comprobaciones recomendadas
------------------------------------
- Build: `.\gradlew clean assembleDebug`
- Lint: `.\gradlew lint`
- Tests unitarios: `.\gradlew test`
- Instrumented tests: `.\gradlew connectedAndroidTest` (requiere dispositivo/emulador)

Notas adicionales y elementos detectados
--------------------------------------
- Jetpack Compose está habilitado y la versión del kotlin compiler extension es `1.5.14`.
- Se usan: Room (2.8.1), Firebase BOM `33.2.0`, Retrofit, Coil, DataStore.
- `minSdk`=24 (Android 7.0), `targetSdk`=36.

Licencia
--------
No se encontró un archivo de licencia en el repositorio. Añade un `LICENSE` (por ejemplo MIT) si quieres que el código sea reutilizable públicamente.

Contacto
--------
Para dudas de desarrollo o despliegue, crea un issue en el repositorio o contacta al autor del proyecto.

---

Estructura Detallada del Proyecto
---------------------------------
A continuación, una descripción detallada de cada carpeta y archivo creado en el proyecto. Cada descripción es de no más de 2 líneas.

#### Raíz del Proyecto
- `.git/` — Directorio de control de versiones Git, contiene historial de commits y configuración del repositorio.
- `.gitignore` — Archivo que especifica patrones de archivos/directorios a ignorar en Git (ej. builds, IDE files).
- `app/` — Módulo principal de la aplicación Android, contiene código fuente, recursos y configuraciones.
- `build.gradle.kts` — Script de build raíz de Gradle (Kotlin DSL), configura plugins globales y dependencias.
- `gradle/` — Directorio con wrapper de Gradle para ejecutar builds sin instalar Gradle localmente.
- `gradle.properties` — Archivo de propiedades de Gradle, configura JVM args, AndroidX y otras opciones.
- `gradlew` — Script de wrapper de Gradle para Windows (ejecutable para builds).
- `local.properties` — Archivo local (no versionado) con rutas SDK y propiedades específicas del entorno.
- `README.md` — Documentación del proyecto en Markdown, incluye setup, build y contribución.
- `settings.gradle.kts` — Configuración de módulos del proyecto Gradle (incluye `:app`).

#### app/ (Módulo Principal)
- `.gitignore` — Archivo específico del módulo para ignorar archivos en Git (ej. APKs generados).
- `build.gradle.kts` — Script de build del módulo app, define dependencias (Firebase, Compose, Room), plugins y configuraciones Android.
- `google-services.json` — Archivo de configuración de Firebase, contiene credenciales y servicios habilitados (Auth, Firestore, Analytics).
- `proguard-rules.pro` — Reglas de ProGuard para ofuscación en builds de release.
- `src/` — Directorio fuente del módulo, contiene código y recursos.

#### app/src/main/
- `AndroidManifest.xml` — Manifiesto de la app, declara permisos (cámara, notificaciones), activities y providers (FileProvider).
- `java/` — Código fuente Java/Kotlin organizado en paquetes.
- `res/` — Recursos de la app (layouts, strings, drawables, etc.).

#### app/src/main/java/cl/duoc/milsabores/ (Paquete Principal)
- `core/` — Utilidades centrales como logging y manejo de resultados (Result.kt, AppLogger.kt).
- `data/` — Capa de datos: local (Room DAOs, entidades), media (repositorio para FileProvider), prefs (SharedPreferences).
- `MainActivity.kt` — Activity principal, inicializa tema, navegación y servicios globales.
- `MilSaboresApplication.kt` — Clase Application, inicializa Firebase al arranque.
- `model/` — Modelos de datos: clases como Pedido, Producto, User, EstadoPedido.
- `notifications/` — Servicios de notificaciones locales (NotificationHelper.kt).
- `repository/` — Repositorios: AuthRepository (Firebase Auth), CarritoRepository (Room), PedidosRepository (híbrido Firebase/Room).
- `service/` — Servicios: PedidosObserverService (observa cambios en Firestore para notificaciones).
- `ui/` — Capa de UI: screens (pantallas Compose), components (SkeletonLoader), theme (Color.kt, Theme.kt, Type.kt).
- `utils/` — Utilidades: PermissionHelper.kt (manejo de permisos), CLP.kt (formato de precios).

#### app/src/main/res/ (Recursos)
- `drawable/` — Imágenes y gráficos vectoriales (ej. íconos, placeholders).
- `mipmap-*/` — Íconos de launcher en diferentes densidades (hdpi, xhdpi, etc.).
- `values/` — Valores estáticos: colors.xml (paleta), strings.xml (textos, ej. app_name), themes.xml (temas base).
- `xml/` — Configuraciones XML: file_paths.xml (FileProvider para cámara).

---

Requisitos cubiertos por esta actualización
------------------------------------------
- Resumen del proyecto: Hecho
- Requisitos y herramientas: Hecho
- Instrucciones de instalación y build: Hecho (comandos PowerShell incluidos)
- Permisos y configuración de Firebase: Hecho
- Notas sobre secretos y firma: Hecho
- Descripciones detalladas de carpetas y archivos: Hecho (no más de 2 líneas por elemento)

Si quieres, puedo:
- Añadir capturas de pantalla en `docs/screenshots/` y linkearlas desde este README.
- Insertar badges de CI (GitHub Actions) si configuras un workflow.
- Añadir una sección de despliegue (Play Store) con pasos detallados.
