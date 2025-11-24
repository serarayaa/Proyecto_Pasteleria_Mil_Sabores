# 🍰 Pastelería Mil Sabores — Aplicación Móvil Android

Última actualización: 2025-11-24

Resumen
-------
Aplicación Android desarrollada con Jetpack Compose para gestionar catálogo y pedidos de una pastelería; usa Room para persistencia local y Firebase para autenticación, Firestore y analytics.

Objetivo de este README
-----------------------
Guía rápida para preparar, compilar y entender la estructura principal del proyecto. La documentación completa por archivo está en `docs/ARCHITECTURE.md`.

Requisitos y comandos básicos
----------------------------
- JDK 17, Android SDK Platform 36.
- En PowerShell desde la raíz del proyecto:

    .\gradlew clean assembleDebug
    .\gradlew installDebug
    .\gradlew lint
    .\gradlew test

Firebase
--------
El archivo `app/google-services.json` está incluido y configura el proyecto Firebase `pasteleria-1000-sabores` (Auth, Firestore, Analytics). Para usar otro proyecto, reemplaza ese archivo y registra tu `applicationId`.

Permisos importantes
--------------------
Extraídos de `app/src/main/AndroidManifest.xml`:
- INTERNET, VIBRATE, POST_NOTIFICATIONS, CAMERA, READ_MEDIA_IMAGES, READ_EXTERNAL_STORAGE.

Diagramas (PlantUML)
--------------------
- Arquitectura general: `docs/diagrams/architecture.puml`
- Flujo de pedido: `docs/diagrams/order_flow.puml`

Puedes generar imágenes desde estos archivos con PlantUML o usar extensiones en tu editor para renderizarlos.

Estructura por carpetas (tablas)
-------------------------------
### Raíz del proyecto

| Archivo | Descripción |
|---|---|
| `build.gradle.kts` | Script Gradle raíz que configura repositorios y plugins. |
| `settings.gradle.kts` | Define los módulos del proyecto (incluye `:app`). |
| `gradle.properties` | Propiedades globales de Gradle (JVM args, AndroidX). |
| `gradlew` | Wrapper de Gradle para ejecutar builds en la máquina local. |
| `local.properties` | Ruta del SDK y configuración local (no versionada). |
| `README.md` | Documentación principal del proyecto (este archivo). |

### Módulo `app`

| Archivo/Carpeta | Descripción |
|---|---|
| `app/build.gradle.kts` | Script de build del módulo con dependencias (Compose, Firebase, Room). |
| `app/google-services.json` | Configuración de Firebase (project_id, api_key). |
| `app/proguard-rules.pro` | Reglas de ofuscación para builds de release. |
| `app/src/main/AndroidManifest.xml` | Manifiesto: actividades, permisos y providers (FileProvider). |
| `app/src/main/res/values/strings.xml` | Strings estáticos como `app_name`. |
| `app/src/main/res/drawable/` | Imágenes y vectores usados por la UI. |
| `app/src/main/res/mipmap-*/` | Iconos de launcher en diferentes densidades. |
| `app/src/main/res/xml/file_paths.xml` | Config para FileProvider (captura de cámara). |

### Código fuente (`app/src/main/java/cl/duoc/milsabores`)

| Ubicación | Archivo | Descripción |
|---|---|---|
| `/` | `MilSaboresApplication.kt` | Inicializa servicios globales (p.ej. Firebase) al inicio de la app. |
| `/` | `MainActivity.kt` | Activity principal que monta la UI Compose y NavHost. |
| `core/` | `AppLogger.kt` | Utilidad centralizada de logging. |
| `core/` | `Result.kt` | Clase sellada para modelar Success/Error/Loading. |
| `data/remote/` | `RetrofitClient.kt` | Configuración del cliente Retrofit y logging HTTP. |
| `data/remote/` | `ProductApiService.kt` | Endpoints Retrofit para obtener productos. |
| `data/local/` | `AppDatabase.kt` | Configuración de Room y acceso a DAOs. |
| `data/local/dao/` | `CarritoDao.kt` | Operaciones CRUD sobre la tabla carrito. |
| `data/local/dao/` | `PedidoDao.kt` | Operaciones CRUD sobre la tabla pedidos. |
| `model/` | `Producto.kt` | Modelo de dominio para representar un producto. |
| `model/` | `Pedido.kt` | Modelo de dominio para representar un pedido. |
| `ui/app/` | `AppNavHost.kt` | Navegación principal con AnimatedNavHost. |
| `ui/principal/` | `PrincipalScreen.kt` | Pantalla principal con lista/grid de productos. |
| `ui/carrito/` | `CarritoScreen.kt` | Pantalla para revisar y gestionar items del carrito. |
| `ui/pedidos/` | `PedidosScreen.kt` | Lista de pedidos con estado y filtros. |
| `ui/profile/` | `ProfileScreen.kt` | Pantalla de perfil y gestión de foto de usuario. |
| `notifications/` | `NotificationHelper.kt` | Crea canales y muestra notificaciones locales. |
| `service/` | `PedidosObserverService.kt` | Servicio que observa Firestore para cambios en pedidos. |

Más detalles y la descripción completa por archivo (cada una ≤ 2 líneas) están en `docs/ARCHITECTURE.md`.

Contribuir
----------
1. Crea una rama `feature/...` o `fix/...` para tus cambios.
2. Ejecuta en PowerShell desde la raíz del proyecto:

    .\gradlew clean assembleDebug
    .\gradlew lint

3. Envía PR con una descripción y pasos para reproducir.

Contacto
--------
Para dudas técnicas o sugerencias, abre un issue en el repositorio.

---

Si quieres que también genere imágenes PNG/SVG a partir de los archivos PlantUML y las añada en `docs/diagrams/`, lo puedo hacer; dime si deseas PNG o SVG y procedo.
