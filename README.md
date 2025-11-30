# Pastelería Mil Sabores — Aplicación Android

Última actualización: 2025-11-30

Resumen
-------
Pastelería Mil Sabores es una aplicación móvil Android (Kotlin + Jetpack Compose) que permite a los usuarios explorar un catálogo de productos, gestionar un carrito de compras y realizar pedidos en línea. El proyecto usa un patrón MVVM y se integra con servicios externos (Retrofit, Firebase) y almacenamiento local (Room).

Estado del proyecto
-------------------
- Estado: Funcional (interfaz y flujo principal implementados).
- Artefactos: APK de release disponible en `app/release/app-release.apk` (ver `app/release/output-metadata.json`).

Requisitos
----------
- JDK 17
- Android SDK (instalado y configurado en `local.properties`)
- Android Studio Flamingo o superior (recomendado)
- PowerShell (Windows) / terminal compatible
- Gradle Wrapper incluido (`gradlew` / `gradlew.bat`)

Cómo ejecutar (desarrollo)
--------------------------
Abra PowerShell en la raíz del proyecto y ejecute:

```powershell
# Limpiar y compilar versión debug
.\gradlew clean assembleDebug
# Instalar en dispositivo conectado
.\gradlew installDebug
```

O abra el proyecto en Android Studio y ejecute desde el IDE.

Cómo ejecutar tests unitarios
----------------------------
```powershell
.\gradlew test
# Reportes en app/build/reports/tests/testDebugUnitTest/index.html
```

Generar APK firmado (release)
-----------------------------
1. Cree un keystore local (no incluya la llave en el repositorio):
```powershell
keytool -genkeypair -v -keystore release-keystore.jks -alias milsabores_alias -keyalg RSA -keysize 2048 -validity 10000
```
2. Configure las propiedades de firma en `gradle.properties` o variables de entorno (RELEASE_KEYSTORE_PASSWORD, RELEASE_KEY_ALIAS, RELEASE_KEY_PASSWORD). No incluir valores literales en el repo.
3. Generar release firmado:
```powershell
.\gradlew clean assembleRelease
```
El APK firmado se ubicará en `app/build/outputs/apk/release/` y una copia puede encontrarse en `app/release/app-release.apk` si fue copiada manualmente.

Estructura del proyecto (tabla paginada por carpetas)
----------------------------------------------------
A continuación se presenta una tabla resumida por carpetas con la descripción de cada archivo o subcarpeta (máx. 2 líneas por entrada). Para legibilidad se muestran bloques por carpeta; cada página/listado agrupa hasta 12 ítems.

Raíz del proyecto

| Ruta | Tipo | Descripción breve |
|---|---:|---|
| `build.gradle.kts` | archivo | Script raíz de Gradle (Kotlin DSL) que orquesta la compilación del proyecto. |
| `settings.gradle.kts` | archivo | Define los módulos (ej. `:app`) y estructura del build. |
| `gradle.properties` | archivo | Propiedades globales de Gradle y flags de compilación. |
| `gradlew` / `gradlew.bat` | ejecutable | Wrappers de Gradle para ejecutar builds sin instalar Gradle globalmente. |
| `local.properties` | archivo | Ruta local al SDK de Android (no versionar). |
| `README_FRONTEND.md` | md | Documentación específica del frontend con arquitectura y guía rápida. |
| `README.md` | md | Este archivo principal con resumen, pasos y enlace a docs. |
| `docs/` | carpeta | Documentación técnica y diagramas del proyecto. |
| `app/` | carpeta | Módulo Android con código fuente, recursos y artefactos. |
| `.gitignore` | archivo | Ignora archivos locales, keystores y configuraciones no versionables. |

Docs

| Ruta | Tipo | Descripción breve |
|---|---:|---|
| `docs/ARCHITECTURE.md` | md | Documentación detallada de la arquitectura, componentes y guía técnica. |
| `docs/FILES_SUMMARY.md` | md | Resumen de archivos relevantes con explicación pedagógica. |
| `docs/diagrams/architecture.puml` | puml | Diagrama PlantUML: visión por capas y dependencias de la app. |
| `docs/diagrams/order_flow.puml` | puml | Diagrama PlantUML: flujo de creación y observación de pedidos. |

Módulo `app/` (Android)

| Ruta | Tipo | Descripción breve |
|---|---:|---|
| `app/build.gradle.kts` | archivo | Script de build del módulo `app` (dependencias, signingConfigs). |
| `app/google-services.json` | json | Configuración de Firebase (Auth, Firestore, Analytics) para desarrollo. |
| `app/proguard-rules.pro` | archivo | Reglas de ofuscación para builds release. |
| `app/src/main/AndroidManifest.xml` | archivo | Manifiesto Android: declara `MainActivity`, permisos y services. |
| `app/release/app-release.apk` | binario | APK de release (evidencia de APK firmado). |
| `app/release/output-metadata.json` | json | Metadata del output de release (firma y detalles). |

Código fuente principal (resumen)

| Ruta | Tipo | Descripción breve |
|---|---:|---|
| `app/src/main/java/com/milsabores/MainActivity.kt` | kotlin | Actividad principal que inicia la UI y establece `AppNavHost`. |
| `app/src/main/java/com/milsabores/MilSaboresApplication.kt` | kotlin | Clase Application que inicializa SDKs como Firebase al arrancar. |
| `app/src/main/java/com/milsabores/ui/` | carpeta | Pantallas Compose agrupadas por funcionalidad (login, principal, carrito). |
| `app/src/main/java/com/milsabores/viewmodel/` | carpeta | ViewModels que gestionan la lógica y exponen estados a la UI. |
| `app/src/main/java/com/milsabores/data/remote/` | carpeta | Clientes Retrofit y servicios para consumir APIs externas. |
| `app/src/main/java/com/milsabores/data/local/` | carpeta | Implementación Room (DAOs, Entities) para persistencia local. |

Tests y utilidades

| Ruta | Tipo | Descripción breve |
|---|---:|---|
| `app/src/test/` | carpeta | Tests unitarios (ViewModels, utilidades). |
| `app/src/androidTest/` | carpeta | Tests instrumentados (UI/Integration) — revisa si implementados. |
| `core/` | carpeta | Utilidades de plataforma, logger y tipos comunes (Result). |
| `utils/` | carpeta | Helpers para permisos, formateos y utilitarios comunes. |

Diagrama de arquitectura y pantallas
------------------------------------
Se incluyen diagramas PlantUML en `docs/diagrams/`. Para visualizarlos, utilice una extensión PlantUML o genere PNG/SVG localmente con PlantUML:

- `docs/diagrams/architecture.puml` — Diagrama de componentes y dependencias.
- `docs/diagrams/order_flow.puml` — Flujo de pedidos y sincronización.

Cumplimiento con la rúbrica DSY1105 (Parte Frontend)
----------------------------------------------------
A continuación se resume el grado de cumplimiento con la rúbrica solicitada (Evaluación Parcial 4):

- Consumo de APIs externas: Implementado (Retrofit y DTOs en `data/remote`). Evidencia en `app/src/main/java/**/data/remote`.
- Conexión con microservicios (Spring Boot): Preparado para consumir microservicios; el backend no está incluido en este repo (indicar endpoints en docs si se dispone). 
- Pruebas unitarias: Estructura de tests presente en `app/src/test/`; revisar cobertura y completar tests recomendados (Login, Carrito, Pedidos).
- Generación de APK firmado: Documentado y presente (`app/release/app-release.apk`). Reproducir firma localmente con keystore. 
- Documentación técnica: `docs/ARCHITECTURE.md` describe arquitectura y pasos de despliegue; se han añadido diagramas PlantUML.

Puntos faltantes o recomendados:
- Añadir pruebas instrumentadas (`app/src/androidTest/`) y aumentar la cobertura de unit tests.
- Documentar claramente los endpoints del backend (contratos request/response) si el backend existe o está en otro repo.
- Añadir CI/CD para automatizar builds, tests y generación de APK/AAB.

Archivos importantes para la entrega (resumen rápido)
---------------------------------------------------
- README_FRONTEND.md — base del contenido frontend.
- docs/ARCHITECTURE.md — arquitectura extendida y guías.
- app/release/app-release.apk — APK firmado (evidencia de entrega).
- app/google-services.json — configuración Firebase.

Cómo contribuir
---------------
1. Cree una rama (`feature/<nombre>`) desde `main`.
2. Añada tests para la lógica nueva o modificada.
3. Abra un Pull Request describiendo los cambios y pruebas realizadas.
4. Asegúrese de no incluir keystores ni secretos en el PR.

Contacto y créditos
-------------------
- Autores: @serarayaa y equipo Mil Sabores.
- Licencia: MIT (especificar archivo LICENSE si corresponde).

Changelog breve
---------------
- 2025-11-30: README actualizado y tabla de archivos reordenada; enlace a `docs/ARCHITECTURE.md` añadido.


