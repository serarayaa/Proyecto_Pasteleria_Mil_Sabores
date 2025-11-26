# 🍰 Pastelería Mil Sabores — Aplicación Móvil Android

Última actualización: 2025-11-26

Resumen
-------
Pastelería Mil Sabores es una aplicación Android (Kotlin + Jetpack Compose) para gestionar catálogo, carrito y pedidos de una pastelería. Integra persistencia local, consumo de APIs REST y servicios de Firebase para autenticación y sincronización.

Propósito de este README
------------------------
Este README ofrece:
- Resumen del proyecto y estado actual.
- Comandos principales (PowerShell) para build, pruebas y firma.
- Tabla ordenada por carpetas con archivos relevantes (resumen).
- Enlaces a documentación extensa y diagramas en `docs/`.

Estado del proyecto
-------------------
- Plataforma: Android (Kotlin, Jetpack Compose, MVVM).
- Backend propio: no incluido en este repositorio (adaptadores Retrofit preparados).
- Firebase: archivo `app/google-services.json` incluido para desarrollo.
- Rama de trabajo: la rama activa puede variar; revisar el control de versiones remoto.

Requisitos
----------
- Android Studio (recomendado) con SDK Android correspondiente.
- JDK 11+ (el proyecto puede requerir JDK 17 según `gradle.properties`).
- Emulador o dispositivo para pruebas instrumentadas.

Comandos útiles (PowerShell)
---------------------------
Desde la raíz del proyecto en PowerShell (Windows):

```powershell
# Limpia y compila APK debug
.\gradlew clean assembleDebug
# Instala en dispositivo/emulador
.\gradlew installDebug
# Ejecuta lint y tests unitarios
.\gradlew lint
.\gradlew test
# Tests instrumentados (emulador conectado)
.\gradlew connectedAndroidTest
# Generar release (ver sección firma más abajo)
.\gradlew assembleRelease
```

Estructura de archivos (tabla resumida)
--------------------------------------
La lista completa con descripción por archivo (máx. 2 líneas) está en `docs/FILES_SUMMARY.md`. Aquí se ofrece un resumen organizado por carpetas principales.

| Carpeta / Archivo (ruta relativa) | Descripción breve |
|---|---|
| `/` | Archivos raíz del proyecto (Gradle, wrapper, configuración global). |
| `build.gradle.kts` | Script raíz (Kotlin DSL) que configura plugins, repositorios y versiones. |
| `settings.gradle.kts` | Declara módulos del proyecto (p. ej. `:app`). |
| `gradle/` | Dependencias de versión y wrapper. |
| `app/` | Módulo Android principal con código fuente, recursos y configuración. |
| `app/build.gradle.kts` | Script de build del módulo `app` con dependencias (Compose, Room, Retrofit, Firebase). |
| `app/google-services.json` | Configuración de Firebase para desarrollo (no incluir secrets). |
| `app/proguard-rules.pro` | Reglas de ofuscación para builds release. |
| `app/src/main/AndroidManifest.xml` | Declaración de Activities, permisos y providers. |
| `app/src/main/res/` | Recursos (valores, drawables, layouts, icons, themes). |
| `app/src/main/java/cl/duoc/milsabores/` | Código fuente (Application, activities, UI, viewmodels, repositorios). |
| `docs/` | Documentación técnica extendida y diagramas PlantUML. |
| `docs/ARCHITECTURE.md` | Documentación arquitectónica extendida (flujo de datos, decisiones, build/release). |
| `docs/FILES_SUMMARY.md` | Descripción por archivo (máx. 2 líneas) — fuente canónica para revisar archivos nuevos. |
| `docs/diagrams/` | PlantUML (`*.puml`) y versiones generadas (`*.png`) de diagramas. |

Resumen del código relevante
----------------------------
(Entradas representativas; la lista completa está en `docs/FILES_SUMMARY.md`)

| Ruta | Archivo | Descripción |
|---|---|---|
| `app/src/main/java/cl/duoc/milsabores/` | `MilSaboresApplication.kt` | Inicializa servicios globales (Firebase, logging) al arrancar la app. |
| `app/src/main/java/cl/duoc/milsabores/ui/` | `MainActivity.kt` | Activity principal que monta el NavHost Compose y aplica el tema. |
| `app/src/main/java/cl/duoc/milsabores/ui/register/` | `RegistrarseScreen.kt` | Pantalla de registro (Jetpack Compose) y validaciones locales; conecta con `RegistrarseViewModel`. |
| `app/src/main/java/cl/duoc/milsabores/viewmodel/` | `*ViewModel.kt` | ViewModels que exponen estado y lógica de presentación (StateFlow/LiveData). |
| `app/src/main/java/cl/duoc/milsabores/data/` | `repository`, `remote`, `local` | Repositorios, clientes Retrofit, entidades y DAOs para Room. |
| `app/src/main/java/cl/duoc/milsabores/service/` | `PedidosObserverService.kt` | Servicio que observa Firestore y notifica cambios de pedidos. |
| `app/src/main/java/cl/duoc/milsabores/notifications/` | `NotificationHelper.kt` | Utilidades para canales y envío de notificaciones. |

Arquitectura (resumen ampliado)
-------------------------------
Patrón principal: MVVM (View → ViewModel → Repository → DataSource).
- UI: Jetpack Compose; composables desacoplados consumen ViewModels.
- ViewModels: manejan estado con StateFlow/LiveData, validaciones y llamadas a repositorios.
- Repositorios: coordinan fuentes de datos (local Room y remoto via Retrofit/Firebase).
- Persistencia: Room para datos locales (carrito, pedidos pendientes).
- Red: Retrofit + OkHttp para APIs REST; mapeo DTO → dominio en capas de datos.
- Inyección: Hilt o solución propia si está configurado (ver `app/build.gradle.kts`).

Flujo típico de registro (ejemplo real en el código)
- `RegistrarseScreen.kt` (Composable) recoge inputs y delega acciones en `RegistrarseViewModel`.
- `RegistrarseViewModel` valida campos, llama al repositorio, expone `ui` con `loading`, `error`, `message`, `registered`.
- Al completarse con éxito, la UI navega según `registered` y limpia mensajes.

Documentación técnica y diagramas
--------------------------------
- `docs/ARCHITECTURE.md`: explicación detallada del diseño, decisiones, patrones y guía para builds y firma de APK.
- `docs/FILES_SUMMARY.md`: inventario exhaustivo con descripción por archivo (máx. 2 líneas).
- `docs/diagrams/`: fuentes PlantUML (`*.puml`) y PNG generados. Ejemplos: `architecture.puml`, `order_flow.puml`.

Regenerar diagramas PlantUML
---------------------------
Si editas `*.puml` localmente puedes generar PNG con PlantUML (ejemplo con `plantuml.jar`):

```powershell
# Desde la raíz del proyecto (PowerShell)
java -jar tools/plantuml.jar -tpng docs/diagrams/*.puml -o docs/diagrams/
```

(Opcional) Instala extensión PlantUML en tu IDE para previsualizar y exportar las imágenes.

Mapeo detallado con la rúbrica DSY1105 — Frontend
------------------------------------------------
(Incluye una revisión de lo que el proyecto cumple respecto a la rúbrica solicitada)

Requisitos de la rúbrica y estado actual:
- Consumo de APIs externas: Cumple — existen adaptadores Retrofit y llamadas a endpoints (ver `data/remote/*`).
- Conexión con microservicios Spring Boot: Parcial — el cliente está preparado; el microservicio no está incluido en este repo.
- Pruebas unitarias (frontend): Parcial — hay tests o plantilla, pero faltan pruebas unitarias completas para ViewModels; revisar `app/src/test/`.
- Generación de APK firmado: Parcial — el proceso está documentado en `docs/ARCHITECTURE.md`; el keystore no está incluido por seguridad.
- Documentación técnica: Cumple — `docs/ARCHITECTURE.md`, `docs/FILES_SUMMARY.md` y diagramas en `docs/diagrams/`.
- Integración Firebase (Auth/Firestore): Cumple — `google-services.json` presente y servicios (ej. `PedidosObserverService`) implementados.

Puntos a mejorar para cumplir totalmente la rúbrica (sugerencias):
- Añadir tests unitarios para ViewModels críticos (registro, carrito, pedidos).
- Proveer un microservicio Spring Boot de ejemplo o contratos OpenAPI para pruebas end-to-end.
- Incluir un ejemplo de pipeline de CI que genere un APK firmado (usando secrets para keystore).

Guía rápida para firmar un APK (resumen)
--------------------------------------
1. Genera un keystore localmente y guarda sus datos en variables seguras (no subir al repo).
2. Configura `signingConfigs` en `app/build.gradle.kts` usando propiedades en `gradle.properties` o variables de entorno.
3. Ejecuta: `.\gradlew assembleRelease` y firma con las credenciales.

Changelog (resumen de cambios recientes)
---------------------------------------
- 2025-11-24: Actualización de README y mover documentación extensa a `docs/`.
- 2025-11-26: README ampliado con tabla por carpetas y mapeo con la rúbrica DSY1105.

Cómo contribuir
---------------
1. Crea una rama `feature/<nombre>` o `fix/<descripcion>`.
2. Añade tests para cambios de lógica (ViewModels/repositorios).
3. Formatea y ejecuta lint antes de enviar el PR:

```powershell
.\gradlew clean assembleDebug
.\gradlew lint
```

4. Envía PR con descripción y pasos para reproducir.

Tareas pendientes recomendadas
-----------------------------
- Completar y ejecutar tests unitarios para ViewModels (registro, carrito, pedidos).
- Añadir un microservicio de ejemplo en Spring Boot o documentación OpenAPI.
- Agregar un `CHANGELOG.md` con estándar semántico.

Contacto y ayuda
----------------
Si deseas que actualice `docs/FILES_SUMMARY.md` con la lista completa de archivos (descripciones de 1-2 líneas por archivo), o que genere los PNG de PlantUML y los añada al repo, dime y lo hago.

Licencia
--------
- Añadir archivo `LICENSE` con la licencia elegida (p. ej. MIT) si procede.

---

(La descripción por archivo completa se mantiene en `docs/FILES_SUMMARY.md`. Este README sirve como resumen y guía rápida para desarrolladores y evaluadores.)
