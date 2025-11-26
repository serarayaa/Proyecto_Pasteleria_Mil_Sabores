# 🍰 Pastelería Mil Sabores — Aplicación Móvil Android

Última actualización: 2025-11-24

Resumen
-------
Pastelería Mil Sabores es una aplicación Android hecha con Jetpack Compose para gestionar catálogo, carrito y pedidos de una pastelería. Integra Room para persistencia local, Retrofit para consumo de APIs y Firebase (Auth, Firestore, Analytics) para autenticación y sincronización.

Propósito del README
--------------------
Este README ofrece:
- Un resumen rápido del proyecto.
- Comandos de build y deploy (PowerShell).
- Tabla ordenada por carpetas con los archivos más relevantes.
- Enlaces a documentación técnica extensa en `docs/`.

Estado y contexto
-----------------
- Proyecto: Android (Kotlin, Jetpack Compose).
- Backend: adaptadores Retrofit preparados; no se incluye microservicio Spring Boot en este repo.
- Firebase: `app/google-services.json` incluido para desarrollo.

Requisitos y comandos (PowerShell)
---------------------------------
- JDK 17 y Android SDK (Platform 36 recomendado).
- Desde la raíz del proyecto en PowerShell:

```powershell
# Limpia y compila APK debug
.\gradlew clean assembleDebug
# Instala en dispositivo/emulador
.\gradlew installDebug
# Lint y tests
.\gradlew lint
.\gradlew test
```

Para generar un release firmado, ver `docs/ARCHITECTURE.md#build-firma-y-release`.

Diagrama y documentación
------------------------
- Documentación técnica completa: `docs/ARCHITECTURE.md`.
- Lista completa de archivos y descripciones (máx. 2 líneas): `docs/FILES_SUMMARY.md`.
- Diagramas PlantUML: `docs/diagrams/architecture.puml`, `docs/diagrams/order_flow.puml`.

Tabla de archivos por carpetas (resumen)
---------------------------------------
A continuación se muestra una tabla por carpeta con los archivos más relevantes; para la lista completa y descripciones por archivo, consulta `docs/FILES_SUMMARY.md`.

### Raíz del proyecto

| Archivo | Descripción |
|---|---|
| `build.gradle.kts` | Script raíz (Kotlin DSL) que configura plugins y repositorios. |
| `settings.gradle.kts` | Define módulos del proyecto (p.ej. `:app`). |
| `gradle.properties` | Propiedades globales de Gradle y flags de compilación. |
| `gradlew` / `gradlew.bat` | Wrappers para ejecutar Gradle localmente. |
| `README.md` | Resumen del proyecto y guía rápida (este archivo). |

### Módulo `app` (resumen)

| Archivo / Carpeta | Descripción |
|---|---|
| `app/build.gradle.kts` | Script de build del módulo con dependencias principales (Compose, Firebase, Room, Retrofit). |
| `app/google-services.json` | Configuración del proyecto Firebase para desarrollo. |
| `app/proguard-rules.pro` | Reglas de ofuscación para builds release. |
| `app/src/main/AndroidManifest.xml` | Manifiesto con actividades, permisos y providers. |
| `app/src/main/res/` | Recursos: `values/`, `drawable/`, `mipmap-*/`, `xml/` (file_paths, network config). |

### Código fuente (selección relevante)

| Ubicación | Archivo | Descripción |
|---|---:|---|
| `app/src/main/java/cl/duoc/milsabores/` | `MilSaboresApplication.kt` | Inicializa servicios globales (Firebase, logging). |
| `/` | `MainActivity.kt` | Activity que monta el NavHost y el tema de la app. |
| `core/` | `AppLogger.kt` | Logger centralizado para debug y tracking. |
| `core/` | `Result.kt` | Tipo sellado para Success/Error/Loading. |
| `data/remote/` | `RetrofitClient.kt` | Configura Retrofit/OkHttp y convertidores. |
| `data/remote/` | `ProductApiService.kt` | Endpoints para obtener productos (REST). |
| `data/local/` | `AppDatabase.kt` | Configuración Room y DAOs (Carrito, Pedidos, Reminder). |
| `repository/` | `PedidosRepository.kt` | Orquesta creación, consulta y sincronización de pedidos. |
| `ui/app/` | `AppNavHost.kt` | NavHost Compose y definición de rutas. |
| `ui/principal/` | `PrincipalScreen.kt` | Pantalla principal con catálogo de productos. |
| `ui/carrito/` | `CarritoScreen.kt` | Pantalla del carrito y resumen de compra. |
| `service/` | `PedidosObserverService.kt` | Servicio que observa cambios en Firestore y notifica. |
| `notifications/` | `NotificationHelper.kt` | Gestión de canales y envío de notificaciones. |

Mapeo con la rúbrica DSY1105 — Frontend (resumen)
-------------------------------------------------
- Consumo de APIs externas: Sí (Retrofit + DTOs).
- Conexión con microservicios Spring Boot: Parcial — adaptadores presentes, backend no incluido aquí.
- Pruebas unitarias: Parcial — falta añadir tests en `app/src/test/`.
- Generación de APK firmado: Parcial — pasos documentados, keystore no incluido por seguridad.
- Documentación técnica: Sí — `docs/ARCHITECTURE.md` y `docs/FILES_SUMMARY.md`.
- Integración Firebase (Auth/Firestore): Sí — `google-services.json` presente y `PedidosObserverService` en uso.

Buenas prácticas y notas rápidas
-------------------------------
- No almacenar claves ni keystores en el repo. Usa secrets en CI.
- Mantener DTOs separados y usar mappers antes de exponer objetos a la UI.
- Añadir pruebas unitarias para ViewModels clave antes de la entrega final.

Cambios recientes y sincronización
---------------------------------
Si se hicieron cambios en el código después de la última actualización de este README, el contenido completo y las descripciones por archivo están actualizados en `docs/FILES_SUMMARY.md`. Si quieres, puedo:

- Actualizar esta tabla para incluir archivos nuevos específicos (dime la ruta). 
- Generar imágenes PNG/SVG desde los `*.puml` en `docs/diagrams/` y añadirlas al repo.
- Añadir una sección "Changelog" con los cambios más recientes si me facilitas el listado o el diff.

Contribuir
----------
1. Crea una rama `feature/...` o `fix/...`.
2. Ejecuta (PowerShell) desde la raíz:

```powershell
.\gradlew clean assembleDebug
.\gradlew lint
```

3. Envía PR con descripción y pasos para reproducir.

Contacto
--------
Para dudas técnicas, mejoras en la documentación, o para que ejecute alguna de las acciones propuestas (PNG/SVG de PlantUML, tests ejemplo, CI), dime cuál prefieres y lo implemento.

---
