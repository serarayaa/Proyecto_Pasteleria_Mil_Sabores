# Arquitectura y guía técnica — Pastelería Mil Sabores

Última actualización: 2025-11-30

Este documento amplía la visión de alto nivel de la aplicación, describe los componentes principales, muestra flujos de datos clave, detalla pasos prácticos de build y firma, y ofrece un mapeo con la rúbrica DSY1105 (Evaluación Parcial 4).

Resumen de alto nivel
---------------------
Pastelería Mil Sabores es una aplicación Android moderna construida con Kotlin y Jetpack Compose. Sigue un enfoque por capas (UI → ViewModel → Repositorio → Fuentes de datos) que facilita pruebas y mantenimiento. La app consume APIs REST mediante Retrofit, usa Firebase (Auth / Firestore / Analytics) y persiste datos localmente con Room.

Principales responsabilidades por capa
--------------------------------------
- UI (presentación): composables con estado expuesto por ViewModels. Navegación centralizada en `AppNavHost`.
- ViewModel / Dominio: orquestan llamadas a repositorios, exponen UiState y eventos; prueban la lógica de interacción.
- Repositorios: determinan origen de datos (cache local vs remoto) y unifican contratos para la app.
- Data Sources: Retrofit (remote) y Room/Prefs (local); interceptores OkHttp añaden token JWT a peticiones.
- Services: componentes en background (p. ej. `PedidosObserverService`) para sincronización y notificaciones.

Diagrams / Artefactos
---------------------
Los diagramas PlantUML ya presentes en `docs/diagrams/` ofrecen vistas de arquitectura y flujos. No se generan imágenes aquí por petición del equipo; use su editor o PlantUML local para renderizarlos.

Componentes clave y archivos (descripción breve, máx. 2 líneas cada uno)
------------------------------------------------------------------------
A continuación se listan los archivos más relevantes y su responsabilidad directa. Esta lista está pensada para evaluadores y desarrolladores que necesiten encontrar rápidamente dónde actuar.

Raíz y build

| Archivo / Ruta | Descripción |
|---|---|
| `build.gradle.kts` | Configuración raíz de Gradle (Kotlin DSL) que orquesta plugins y repositorios del proyecto. |
| `settings.gradle.kts` | Define módulos incluidos en el build (por ejemplo `:app`). |
| `gradle.properties` | Propiedades y flags globales (p.ej. versiones, flags de signing en CI). |
| `gradlew` / `gradlew.bat` | Wrappers para ejecutar Gradle de manera reproducible en cualquier equipo. |

Módulo `app/` y configuración Android

| Archivo / Ruta | Descripción |
|---|---|
| `app/build.gradle.kts` | Script de build del módulo `app`: dependencias (Compose, Retrofit, Room), buildTypes y signingConfigs. |
| `app/google-services.json` | Configuración del proyecto Firebase usada en desarrollo (Auth, Firestore, Analytics). |
| `app/proguard-rules.pro` | Reglas de ofuscación para builds release (R8/ProGuard). |
| `app/src/main/AndroidManifest.xml` | Manifiesto Android: declara `MainActivity`, permisos y servicios (p.ej. Observers). |
| `app/release/app-release.apk` | APK de release generado; sirve como evidencia de que se puede producir un artefacto firmado o empaquetado. |
| `app/release/output-metadata.json` | Metadatos del output de release (información de firma y ruta de artefactos). |

Código fuente Android (entrada y configuración)

| Archivo / Ruta | Descripción |
|---|---|
| `app/src/main/java/.../MilSaboresApplication.kt` | Clase Application que inicializa SDKs (Firebase) y configuraciones globales al arranque. |
| `app/src/main/java/.../MainActivity.kt` | Actividad principal que crea el NavHost y fija el tema; punto de entrada UI. |
| `app/src/main/res/` | Recursos de la app (layouts Compose, drawables, strings, colores, temas). |

Capa de UI y navegación

| Archivo / Ruta | Descripción |
|---|---|
| `app/src/main/java/.../ui/app/AppNavHost.kt` | Define la navegación y los destinos principales de la app con Compose Navigation. |
| `app/src/main/java/.../ui/*` | Carpetas por pantalla (`login`, `principal`, `carrito`, `pedidos`, `profile`, etc.) que contienen Composables y layouts. |
| `app/src/main/java/.../ui/components/` | Componentes reutilizables de UI (cards, botones, loaders, etc.). |

ViewModels y dominio

| Archivo / Ruta | Descripción |
|---|---|
| `app/src/main/java/.../viewmodel/*` | ViewModels que exponen estados y responden a eventos de la UI; inyectan repositorios. |
| `app/src/main/java/.../domain/*` | (Si existe) Casos de uso o reglas de negocio centrales; ayuda a aislar lógica para tests. |

Data / repositorios

| Archivo / Ruta | Descripción |
|---|---|
| `app/src/main/java/.../data/remote/RetrofitClient.kt` | Crea instancia Retrofit con OkHttp e interceptores (tokens, logging). |
| `app/src/main/java/.../data/remote/ProductApiService.kt` | Interfaz Retrofit que define endpoints para productos. |
| `app/src/main/java/.../data/remote/AuthApiService.kt` | Interfaz Retrofit para endpoints de autenticación. |
| `app/src/main/java/.../data/local/AppDatabase.kt` | Configuración de Room: entidades, DAOs y versiones de DB. |
| `app/src/main/java/.../data/local/dao/*` | DAOs con operaciones CRUD para `Carrito`, `Pedido` y otros. |
| `app/src/main/java/.../repository/*` | Repositorios que orquestan las fuentes local/remota y exponen APIs para ViewModels. |

Servicios, notificaciones y utilidades

| Archivo / Ruta | Descripción |
|---|---|
| `app/src/main/java/.../service/PedidosObserverService.kt` | Servicio que observa cambios en Firestore y sincroniza la DB local / notifica al usuario. |
| `app/src/main/java/.../notifications/NotificationHelper.kt` | Encapsula la creación de canales y envío de notificaciones locales. |
| `app/src/main/java/.../utils/PermissionHelper.kt` | Helpers para solicitar y verificar permisos en tiempo de ejecución. |
| `core/AppLogger.kt` | Logger centralizado para mensajes durante desarrollo y debugging. |

Flujos principales (paso a paso)
-------------------------------
1) Inicio / Login
- UI: `LoginScreen` recopila credenciales.
- VM: `LoginViewModel` valida y llama a `AuthRepository`.
- Repo: `AuthRepository` usa `AuthApiService` o Firebase Auth; en éxito guarda token en `Prefs`.

2) Carga de catálogo
- `PrincipalViewModel` solicita productos a `ProductRepository`.
- `ProductRepository` devuelve datos desde Room si están cacheados; si no, los solicita vía Retrofit y los persiste.
- UI muestra listas con componentes `ProductCard` y loaders.

3) Carrito y checkout
- `CarritoRepository` gestiona items y totales; persiste en Room.
- Finalizar compra crea `PedidoEntity` y notifica a `PedidosRepository` para enviarlo a backend/Firestore.

4) Observación de pedidos y notificaciones
- `PedidosObserverService` escucha Firestore; al detectar cambios actualiza Room y crea notificaciones locales.

Build, firma y release (detalle práctico)
-----------------------------------------
Nota: nunca incluir el keystore en el repo. Use secrets en CI o un keystore local protegido.

1) Generar un keystore (ejemplo PowerShell):

```powershell
keytool -genkeypair -v -keystore release-keystore.jks -alias milsabores_alias -keyalg RSA -keysize 2048 -validity 10000
```

2) Configurar `app/build.gradle.kts` con `signingConfigs` que apunten a variables/propiedades no versionadas.
3) Definir las propiedades en `gradle.properties` local (o en variables de entorno en CI): `RELEASE_KEYSTORE_PASSWORD`, `RELEASE_KEY_ALIAS`, `RELEASE_KEY_PASSWORD`.
4) Comandos para generar artefactos (PowerShell):

```powershell
# Release firmado
.\gradlew clean assembleRelease
# Debug
.\gradlew clean assembleDebug
# Pruebas unitarias
.\gradlew test
```

Testing
-------
- Unit tests: `app/src/test/` — pruebas para ViewModels y utilidades usando MockK/Mockito y `kotlinx-coroutines-test`.
- Instrumented tests: `app/src/androidTest/` — UI/E2E con Espresso/Compose Testing (revisar si implementadas). 
- Reportes: `app/build/reports/tests/`.

Control de calidad y CI (recomendado)
-------------------------------------
Pipeline mínimo recomendado (GitHub Actions / GitLab CI):
- checkout
- setup JDK 17 + Android SDK
- cache Gradle
- ejecutar `./gradlew lint test assembleDebug`
- publicar artifacts (APK/AAB) si procede
- usar secrets para keystore y Firebase configs

Mapeo con la rúbrica DSY1105 (Frontend)
---------------------------------------
A continuación describo evidencias y estado actual (qué buscar en el repo) para cada punto de la rúbrica:

- Consumo de APIs externas: Evidencia en `data/remote/*` (Retrofit interfaces y `RetrofitClient.kt`). Estado: Implementado.
- Conexión con microservicios propios (Spring Boot): El backend no está en este repo; la app está preparada para consumirlo (buscar `BASE_URL` en `RetrofitClient` o constantes). Estado: Preparado (backend externo).
- Pruebas unitarias: Estructura en `app/src/test/` existe; revisar cobertura y añadir tests faltantes para `LoginViewModel`, `CarritoRepository` y `PedidosRepository`. Estado: Parcial (estructura, pero falta cobertura completa).
- Generación de APK firmado: `app/release/app-release.apk` y `app/release/output-metadata.json` sirven como evidencia. Documentación de firma incluida en este archivo. Estado: Implementado pero requiere keystore local/CI para reproducir.
- Documentación técnica: `docs/ARCHITECTURE.md` y `docs/FILES_SUMMARY.md` (actualizados) describen arquitectura y archivos. Estado: Implementado.

Checklist para entrega DSY1105
------------------------------
- [x] Documentación técnica ampliada (`docs/ARCHITECTURE.md`).
- [x] Tabla de archivos legible y navegable (`docs/FILES_SUMMARY.md`).
- [ ] Aumentar cobertura de unit tests (Login, Carrito, Pedidos).
- [ ] Añadir pruebas instrumentadas (si la rúbrica lo exige explícitamente).
- [ ] Incluir (o enlazar) backend Spring Boot con contratos de endpoints si existe.
- [x] Evidencia de APK (app/release/app-release.apk).

Problemas conocidos y recomendaciones rápidas
--------------------------------------------
- No versionar keystores ni secretos: documentado en `docs/ARCHITECTURE.md` y `README.md`.
- Si el backend está en otro repositorio, añadir un enlace o archivo `API_CONTRACT.md` con detalles de endpoints y ejemplos de request/response.
- Añadir CI para automatizar compilaciones y verificación de PRs.

Siguientes pasos sugeridos
-------------------------
1. Completar y ejecutar pruebas unitarias críticas (dos o tres tests por ViewModel clave).
2. Añadir un pequeño `API_DOCS.md` o contrato OpenAPI si el backend propio existe.
3. (Opcional) Generar imágenes PNG/SVG de los PlantUML y añadirlas a `docs/diagrams/exports/` para incluirlas en la documentación.

Contacto
--------
Para dudas sobre diseño o si quieres que agregue tests de ejemplo o pipeline CI, dime cuál prefieres y lo implemento.
