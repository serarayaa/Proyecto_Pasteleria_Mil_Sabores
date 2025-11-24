# 🍰 Pastelería Mil Sabores — Aplicación Móvil Android

Última actualización: 2025-11-24

Resumen
-------
Pastelería Mil Sabores es una aplicación Android construida con Jetpack Compose que permite navegar un catálogo de productos, gestionar un carrito y crear pedidos. Usa Room para persistencia local, Retrofit para consumo de APIs y Firebase (Auth, Firestore, Analytics) para autenticación y sincronización de pedidos.

Propósito del repositorio
-------------------------
Este repositorio contiene la aplicación móvil (módulo `app`) y la documentación técnica. El README ofrece un resumen, instrucciones rápidas de build, una vista ordenada de archivos por carpeta y enlaces a la documentación completa en `docs/`.

Requisitos y comandos (PowerShell)
---------------------------------
- JDK 17, Android SDK (API 33+/Platform 36) y Android Studio recomendados.
- Desde la raíz del proyecto en PowerShell:

    .\gradlew clean assembleDebug
    .\gradlew installDebug
    .\gradlew lint
    .\gradlew test

Si necesitas crear un APK firmado para release, consulta `docs/ARCHITECTURE.md#firma-y-release`.

Firebase
--------
El archivo `app/google-services.json` está incluido y apunta al proyecto Firebase usado para desarrollo. Para usar otro proyecto: reemplaza ese archivo y actualiza el `applicationId` si aplica.

Diagramas (PlantUML)
--------------------
Los diagramas de arquitectura y flujo se encuentran en:
- `docs/diagrams/architecture.puml` — visión por capas de la app.
- `docs/diagrams/order_flow.puml` — flujo de creación y observación de pedidos.

Puedes renderizar los `.puml` con PlantUML o con extensiones del editor.

Documentación completa
----------------------
- Documentación técnica y decisiones de arquitectura: `docs/ARCHITECTURE.md`.
- Listado completo de archivos con descripciones (máx 2 líneas por archivo): `docs/FILES_SUMMARY.md`.

Estructura destacada (tabla por carpetas)
-----------------------------------------
A continuación una tabla resumida por carpetas (muestra las entradas más relevantes por carpeta). Para la lista completa de archivos y descripciones, abre `docs/FILES_SUMMARY.md`.

### Raíz del proyecto

| Archivo | Descripción |
|---|---|
| `build.gradle.kts` | Script Gradle raíz (Kotlin DSL) que configura repositorios y plugins. |
| `settings.gradle.kts` | Define módulos del proyecto (incluye `:app`). |
| `gradle.properties` | Propiedades y flags de Gradle para el proyecto. |
| `gradlew` / `gradlew.bat` | Wrapper de Gradle para ejecutar builds localmente. |
| `README.md` | Resumen del proyecto y guía rápida (este archivo). |

### Módulo `app` (resumen)

| Archivo / Carpeta | Descripción |
|---|---|
| `app/build.gradle.kts` | Script de build del módulo con dependencias (Compose, Firebase, Room). |
| `app/google-services.json` | Configuración Firebase (Auth, Firestore, Analytics). |
| `app/src/main/AndroidManifest.xml` | Manifiesto de Android: actividades, permisos y providers. |
| `app/src/main/res/values/strings.xml` | Strings y constantes usadas por la UI. |
| `app/src/main/res/drawable/` | Imágenes y recursos gráficos de la app. |
| `app/src/main/java/cl/duoc/milsabores/` | Código fuente Kotlin (UI, data, repositorios, servicios). |

Frontend — mapeo con la rúbrica DSY1105 (Evaluación Parcial 4)
-------------------------------------------------------------
He revisado la implementación del frontend (módulo `app`) y mapeo rápido de los requisitos de la rúbrica:

- Consumo de APIs externas: Cumple (uso de Retrofit en `data/remote/RetrofitClient.kt` y `ProductApiService.kt`).
- Conexión con microservicios Spring Boot propios: Parcial — el proyecto contiene adaptadores Retrofit y DTOs; no se detectó código de microservicio Spring Boot en este repo. Si el backend está en otro repositorio, indica su ubicación para enlazarlo.
- Pruebas unitarias: Parcial — hay configuración para `test` en Gradle; sin embargo, faltan (o no se incluyen) pruebas unitarias en `app/src/test/`. Se recomienda añadir pruebas para ViewModels y Repositorios.
- Generación de APK firmado: Soportado conceptualmente — las reglas de ProGuard y signingConfigs pueden configurarse; ver `docs/ARCHITECTURE.md` para pasos seguros de firma. No hay keystore en el repo (por seguridad).
- Documentación técnica: Cumple — `docs/ARCHITECTURE.md` y `docs/FILES_SUMMARY.md` con descripciones por archivo.
- Integración Firebase (Auth/Firestore): Cumple — `google-services.json`, uso de Firestore/Observers (`service/PedidosObserverService.kt`) y repositorios. 

Resumen de cumplimiento (Frontend):
- Consumo de APIs externas: Sí
- Conexión a microservicios Spring Boot: Parcial / Falta el backend aquí
- Pruebas unitarias: Parcial / Requiere pruebas específicas
- APK firmado: Parcial / pasos documentados pero keystore no incluido
- Documentación técnica: Sí (actualizada)
- Firebase integrado: Sí

Siguientes pasos recomendados (si necesitas para la evaluación DSY1105)
---------------------------------------------------------------------
- Añadir pruebas unitarias (ViewModels y repositorios) en `app/src/test/`.
- Añadir instrucciones para ejecutar pruebas en CI (p. ej. GitHub Actions).
- Si el backend Spring Boot existe, añade un enlace y resumen de endpoints en `docs/ARCHITECTURE.md`.
- Generar APK firmado: crea una keystore local y añade instrucciones para obtener el release firmado.

Contribuir
----------
1. Crea una rama `feature/...` o `fix/...` para tus cambios.
2. Ejecuta en PowerShell:

    .\gradlew clean assembleDebug
    .\gradlew lint

3. Envía PR con descripción y pasos para reproducir.

Archivos modificados / creados (en esta actualización)
----------------------------------------------------
- Modificado: `README.md` (esta versión).
- Modificado: `docs/ARCHITECTURE.md` (se añadió resumen y enlaces). 
- Añadido: `docs/FILES_SUMMARY.md` (lista completa de archivos con descripciones).
- Diagramas: `docs/diagrams/*.puml` (ya presentes y enlazados).

Contacto
--------
Para dudas técnicas o sugerencias, abre un issue en el repositorio o responde a este PR con preguntas específicas (p. ej. generar imágenes PlantUML o ejecutar build completo en CI).

---
