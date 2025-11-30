# Resumen completo de archivos — Pastelería Mil Sabores

Última actualización: 2025-11-30

Este documento lista los archivos y carpetas más relevantes del proyecto con una descripción pedagógica (máx. 2 líneas cada una). Está organizado por secciones para facilitar la lectura.

-- Raíz del proyecto --

| Ruta | Tipo | Descripción breve |
|---|---:|---|
| `build.gradle.kts` | archivo | Script Gradle raíz (Kotlin DSL) que configura plugins, repositorios y tareas globales del proyecto. |
| `settings.gradle.kts` | archivo | Define los módulos incluidos en el build (por ejemplo `:app`). |
| `gradle.properties` | archivo | Propiedades globales del build (versiones y flags útiles para CI). |
| `gradlew` / `gradlew.bat` | ejecutable | Wrappers de Gradle para ejecutar builds reproducibles sin instalar Gradle global. |
| `local.properties` | archivo | Archivo local con la ruta al SDK Android (no debe versionarse). |
| `README.md` | md | README principal del repositorio con resumen, instrucciones y enlaces a `docs/`. |
| `README_FRONTEND.md` | md | Documentación extensa orientada al frontend (arquitectura y guía rápida). |
| `.gitignore` | archivo | Reglas para excluir archivos locales, keystores y otros artefactos sensibles. |

-- Documentación (`docs/`) --

| Ruta | Tipo | Descripción breve |
|---|---:|---|
| `docs/ARCHITECTURE.md` | md | Documentación técnica detallada sobre arquitectura, flujos y pasos de build/firma. |
| `docs/FILES_SUMMARY.md` | md | Índice que detalla los archivos más relevantes (este documento). |
| `docs/diagrams/architecture.puml` | puml | Diagrama PlantUML que representa la arquitectura por capas (usar PlantUML para renderizar). |
| `docs/diagrams/order_flow.puml` | puml | Diagrama PlantUML que muestra el flujo de creación/observación de pedidos. |

-- Módulo Android (`app/`) --

| Ruta | Tipo | Descripción breve |
|---|---:|---|
| `app/build.gradle.kts` | archivo | Script de build del módulo `app` (dependencias, buildTypes, signingConfigs). |
| `app/google-services.json` | json | Configuración de Firebase para el proyecto (Auth, Firestore, Analytics). |
| `app/proguard-rules.pro` | archivo | Reglas de ofuscación y optimización para builds de release. |
| `app/src/main/AndroidManifest.xml` | xml | Manifiesto Android: declara actividades, permisos y servicios. |
| `app/release/app-release.apk` | binario | APK de release (evidencia de artefacto generado); mantener fuera de CI si se publica en Releases. |
| `app/release/output-metadata.json` | json | Metadata del artefacto de release (rutas y detalles de firma). |

-- Código fuente: entrada y configuración --

| Ruta | Tipo | Descripción breve |
|---|---:|---|
| `app/src/main/java/.../MilSaboresApplication.kt` | kotlin | Clase `Application` que inicializa SDKs y configuraciones globales (p.ej. Firebase). |
| `app/src/main/java/.../MainActivity.kt` | kotlin | Actividad principal que monta el `AppNavHost` y establece el tema. |
| `app/src/main/res/` | carpeta | Recursos de la app: drawables, strings, temas, layouts y assets. |

-- UI / Navegación --

| Ruta | Tipo | Descripción breve |
|---|---:|---|
| `app/src/main/java/.../ui/app/AppNavHost.kt` | kotlin | Centraliza la navegación entre pantallas usando Compose Navigation. |
| `app/src/main/java/.../ui/*` | carpeta | Subcarpetas por pantallas (`login`, `principal`, `carrito`, `pedidos`, `profile`, etc.). |
| `app/src/main/java/.../ui/components/` | carpeta | Componentes UI reutilizables (cards, botones, loaders, formularios). |

-- ViewModels y dominio --

| Ruta | Tipo | Descripción breve |
|---|---:|---|
| `app/src/main/java/.../viewmodel/` | carpeta | ViewModels que exponen estados a la UI e inyectan repositorios para la lógica. |
| `app/src/main/java/.../domain/` | carpeta | (Opcional) Casos de uso y lógica de dominio para aislar reglas de negocio. |

-- Data / Repositorios --

| Ruta | Tipo | Descripción breve |
|---|---:|---|
| `app/src/main/java/.../data/remote/RetrofitClient.kt` | kotlin | Configura Retrofit y OkHttp (interceptores, logging, baseUrl). |
| `app/src/main/java/.../data/remote/ProductApiService.kt` | kotlin | Interfaz Retrofit que define endpoints para productos. |
| `app/src/main/java/.../data/remote/AuthApiService.kt` | kotlin | Interfaz Retrofit para autenticación (login/register). |
| `app/src/main/java/.../data/remote/dto/` | carpeta | DTOs que mapean requests/responses JSON entre app y API. |
| `app/src/main/java/.../data/local/AppDatabase.kt` | kotlin | Configura Room (entidades, DAOs) y versiones de la base de datos. |
| `app/src/main/java/.../data/local/dao/` | carpeta | DAOs para acceder a tablas (Carrito, Pedido, Recordatorio). |
| `app/src/main/java/.../data/local/entity/` | carpeta | Entidades Room que representan tablas de la base de datos. |
| `app/src/main/java/.../data/local/Prefs.kt` | kotlin | Abstracción simple para almacenamiento de preferencias (DataStore/SharedPrefs). |

-- Repositorios e implementación --

| Ruta | Tipo | Descripción breve |
|---|---:|---|
| `app/src/main/java/.../repository/CarritoRepository.kt` | kotlin | Lógica de alto nivel para gestionar el carrito y calcular totales. |
| `app/src/main/java/.../repository/PedidosRepository.kt` | kotlin | Gestiona creación, consulta y actualización de pedidos (local y remoto). |
| `app/src/main/java/.../repository/auth/` | carpeta | Implementaciones y contratos para autenticación. |

-- Modelos / Mappers --

| Ruta | Tipo | Descripción breve |
|---|---:|---|
| `app/src/main/java/.../model/Producto.kt` | kotlin | Data class que modela un producto (id, nombre, precio, imagen). |
| `app/src/main/java/.../model/Pedido.kt` | kotlin | Data class que representa un pedido completo con items y totales. |
| `app/src/main/java/.../model/CarritoItem.kt` | kotlin | Data class para un item del carrito con cantidad y subtotal. |
| `app/src/main/java/.../model/Recordatorio.kt` | kotlin | Modelo para recordatorios (fecha, mensaje, estado). |
| `app/src/main/java/.../model/user.kt` | kotlin | Modelo de usuario con campos públicos relevantes. |
| `app/src/main/java/.../model/mappers/` | carpeta | Funciones que convierten DTOs a modelos de dominio/ UI. |

-- Servicios y notificaciones --

| Ruta | Tipo | Descripción breve |
|---|---:|---|
| `app/src/main/java/.../service/PedidosObserverService.kt` | kotlin | Servicio que escucha Firestore y sincroniza pedidos con la DB local. |
| `app/src/main/java/.../notifications/NotificationHelper.kt` | kotlin | Crea canales y encapsula la lógica para enviar notificaciones locales. |

-- Tests y QA --

| Ruta | Tipo | Descripción breve |
|---|---:|---|
| `app/src/test/` | carpeta | Pruebas unitarias para ViewModels, repositorios y utilidades. |
| `app/src/androidTest/` | carpeta | Pruebas instrumentadas para UI/Integration con Espresso/Compose Test (revisar si implementadas). |
| `app/build/reports/tests/` | carpeta | Reportes generados tras ejecutar los tests unitarios y instrumentados. |

-- Utilidades y core --

| Ruta | Tipo | Descripción breve |
|---|---:|---|
| `core/AppLogger.kt` | kotlin | Utilidad global para logging estructurado durante desarrollo. |
| `core/Result.kt` | kotlin | Clase sellada para manejar estados: Success, Error y Loading. |
| `utils/PermissionHelper.kt` | kotlin | Helpers para solicitar permisos y manejar flujos de autorización. |
| `data/media/MediaRepository.kt` | kotlin | Manejo de archivos multimedia y URIs (captura/almacenamiento de imágenes). |

-- Archivos y notas adicionales --

| Ruta | Tipo | Descripción breve |
|---|---:|---|
| `proguard-rules.pro` | archivo | Reglas de ofuscación aplicadas en builds de release. |
| `docs/FILES_SUMMARY.md` | md | Este índice de archivos; mantener actualizado con cambios de estructura. |


Si necesitas, puedo generar una versión ampliada que incluya cada archivo Kotlin individual con 1–2 líneas de descripción (mapear `app/src/main/java/...` por paquete). ¿Deseas que lo haga ahora?
