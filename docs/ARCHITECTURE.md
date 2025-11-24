# Arquitectura y guía técnica — Pastelería Mil Sabores

Última actualización: 2025-11-24

Este documento amplía y organiza la documentación técnica del proyecto, describiendo la arquitectura por capas, componentes clave, flujos principales de la aplicación, integración con Firebase/Retrofit/Room, y pasos prácticos para build, firma de APK y pruebas.

Resumen de alto nivel
---------------------
Pastelería Mil Sabores es una app Android moderna construida con Jetpack Compose (UI declarativa). La arquitectura sigue un patrón por capas: UI (Compose + ViewModels) → Repositorios → Fuentes de datos (Remote: Retrofit / Firebase; Local: Room / SharedPreferences). Los servicios en background (p. ej. `PedidosObserverService`) permiten sincronizar y notificar cambios de pedidos.

Diagrama y artefactos
---------------------
- Diagramas PlantUML:
  - `docs/diagrams/architecture.puml` — visión por capas y dependencias.
  - `docs/diagrams/order_flow.puml` — flujo de creación y observación de pedidos.

Carga: renderiza los `.puml` con PlantUML (en local o con extensiones del editor) para obtener imágenes PNG/SVG.

Estructura por capas (explicación técnica)
-----------------------------------------
1) Capa de presentación (UI)
- Paquetes principales: `ui.app`, `ui.home`, `ui/principal`, `ui/login`, `ui/register`, `ui/carrito`, `ui/pedidos`, `ui/profile`, `ui/settings`, `ui/recordatorio`.
- Composición: Pantallas son Composables; cada pantalla tiene un ViewModel que expone estados (State / UiState) y flujos de eventos.
- Navegación: `ui/app/AppNavHost.kt` y `ui/app/Routes.kt` definen rutas y animaciones entre destinos.

2) Capa de dominio / repositorios
- `repository/*` y `data/*` contienen la lógica de negocio:
  - Repositorios exponen métodos asincrónicos (suspend / Flow / LiveData) combinando fuentes locales y remotas.
  - Ejemplos: `repository/PedidosRepository.kt`, `repository/CarritoRepository.kt`.

3) Capa de datos (fuentes)
- Remoto: `data/remote/RetrofitClient.kt`, servicios Retrofit (`ProductApiService`, `AuthApiService`) y DTOs en `data/remote/dto/`.
- Local: Room (DAOs y entidades en `data/local/`), almacenamiento de archivo (fotos de perfil) y prefs (`data/local/Prefs.kt`).
- Sincronización: `service/PedidosObserverService.kt` observa cambios en Firestore y actualiza el almacenamiento local / notifica al usuario.

4) Servicios y utilidades
- Notificaciones: `notifications/NotificationHelper.kt` gestiona canales y envío de notificaciones.
- Permisos y utilitarios: `utils/PermissionHelper.kt`, `ui/utils/Formatters.kt`, `utils/CLP.kt`.

Componentes clave y responsabilidades
------------------------------------
- `MilSaboresApplication.kt` — Inicializa SDKs y configuraciones globales (Firebase, DI inicial si aplica).
- `MainActivity.kt` — Punto de entrada UI; monta `AppNavHost` y aplica tema.
- ViewModels — Contienen lógica de UI y llaman a repositorios. Deben ser fácilmente testeables (inyectar repositorios/depMocks).
- Repositories — Encapsulan reglas de negocio y orquestan fuentes de datos.
- DAOs/Room — Persistencia local con entidades `CarritoItemEntity`, `PedidoEntity`, `RecordatorioEntity`.
- RetrofitClient — Configura OkHttp interceptors, logging y conversores (Moshi/Gson según configuración).

Flujos principales (resumen paso a paso)
---------------------------------------
1) Inicio / Login (LoginFlow)
- UI: `LoginScreen.kt` recoge credenciales y notifica al `LoginViewModel`.
- VM: valida campos, llama a `AuthRepository.login(email,password)`.
- Repo: intenta autenticación con Firebase Auth o con backend REST (según configuración); en éxito actualiza `Prefs` y navega a `Principal`.

2) Carga de catálogo (Product flow)
- `PrincipalViewModel` solicita productos al `ProductRepository`.
- `ProductRepository` decide: si hay cache/local disponible sirve desde Room o prefs; si no, hace llamada Retrofit (`ProductApiService`) y persiste datos en Room.
- UI: `UiProductsCard` y listas muestran datos; skeletons (`SkeletonLoader.kt`) se muestran mientras carga.

3) Carrito y checkout
- Usuario agrega productos desde `PrincipalScreen` al carrito (llamadas a `CarritoRepository.addItem()`).
- `CarritoRepository` persiste en `CarritoLocalStorage` (Room o SharedPreferences según implementación) y expone totales.
- Finalizar compra crea un `Pedido` y lo envía a `PedidosRepository.crearPedido()`, que persiste local y crea/actualiza registro en Firestore.

4) Observación y notificaciones de pedidos
- `PedidosObserverService` escucha cambios en la colección de pedidos en Firestore.
- Al detectar cambios relevantes (estado cambiado, pedido nuevo), actualiza la DB local y muestra notificación con `NotificationHelper`.

Integración con Firebase (detalles)
----------------------------------
- `app/google-services.json` contiene la configuración del proyecto Firebase (Auth, Firestore, Analytics).
- Usos principales:
  - Firebase Authentication: login/registro (email/password y guest flows según implementación).
  - Firestore: colección `pedidos` (estructura: idPedido, uidUsuario, items[], total, estado, timestamps).
  - Firebase Analytics: eventos de compra y uso.

Recomendaciones de diseño para seguridad y escalabilidad
-------------------------------------------------------
- No versionar keystore ni credenciales. Mantener `google-services.json` de desarrollo; para producción, usar variables de entorno o CI secrets.
- Separar configuraciones por flavors (dev/staging/prod) si se integra con backend diferente.
- Limitar exponiendo DTOs en la UI; usar mappers (`model/mappers/`) para convertir DTO → Domain → UI models.

Detalles técnicos y ejemplos (cómo está configurado)
--------------------------------------------------
1) Retrofit
- `RetrofitClient.kt` centraliza baseUrl, OkHttp (logging interceptor) y conversor JSON.
- `ProductApiService` define endpoints: `GET /products`, `GET /products/{id}`, `POST /orders` (ejemplo).
- Manejar errores: transformar HTTP exceptions a `core/Result` o a sealed types controlables por ViewModels.

2) Room (persistencia local)
- `AppDatabase.kt` define DB con DAOs: `CarritoDao`, `PedidoDao`, `ReminderDao`.
- Entidades: `CarritoItemEntity`, `PedidoEntity`, `RecordatorioEntity`.
- Migraciones: versionar la DB y agregar `Migration` cuando cambian esquemas.

3) Observabilidad y logging
- `core/AppLogger.kt` provee logging centralizado; activar logging solo en buildTypes debug.
- Usar Firebase Crashlytics (si se añade) para errores en producción.

Build, firma y release (pasos prácticos)
---------------------------------------
Nota: no incluyas keystore en el repositorio. A continuación pasos seguros para generar y usar un keystore local para firmar el APK.

1) Generar keystore (local):
- En PowerShell (ejemplo):

```powershell
keytool -genkeypair -v -keystore release-keystore.jks -alias milsabores_alias -keyalg RSA -keysize 2048 -validity 10000
```

2) Configurar `app/build.gradle.kts` (ejemplo de signingConfigs):
- Añade un `signingConfigs` con referencias a propiedades o variables de entorno (no literales).
- En `gradle.properties` o en CI, define: `RELEASE_KEYSTORE_PASSWORD`, `RELEASE_KEY_ALIAS`, `RELEASE_KEY_PASSWORD`.

3) Comandos para generar release firmado (PowerShell):

```powershell
# Limpia y compila el release firmado
.\gradlew clean assembleRelease
# Genera bundle o APK según configuración
.\gradlew bundleRelease
```

4) Notas de seguridad:
- Guarda `release-keystore.jks` en un lugar seguro (Vault o carpeta privada fuera del repo) y utiliza secrets en CI para firmas automáticas.

Testing (unit & integration)
----------------------------
- Estructura recomendada:
  - `app/src/test/` — pruebas unitarias para ViewModels y utilidades.
  - `app/src/androidTest/` — pruebas instrumentadas para interacciones UI o Room.

- Recomendaciones específicas:
  - Mockear `Repository` en pruebas de ViewModel con `kotlinx-coroutines-test`.
  - Añadir pruebas para: validación de formularios (Login/Register), lógica de totales del carrito, creación de pedido y manejo de errores.

Ejemplo (pasos rápidos para crear pruebas de ViewModel):
1. Añadir dependencias `testImplementation` para JUnit, MockK (o Mockito), kotest-assertions y kotlinx-coroutines-test.
2. Crear un test para `LoginViewModel` que simule respuestas de `AuthRepository` y verifique cambios en `UiState`.

Integración continua (CI) — sugerencias
--------------------------------------
- Pipeline básico:
  - step: checkout
  - step: set up JDK 17 + Android SDK
  - step: restore cache (Gradle)
  - step: run `./gradlew lint test assembleDebug` (o `assembleRelease` con keystore en secrets)
  - step: upload artifacts (APK/AAB) si se desea.
- Usar secrets para keystore y variables Firebase (si aplica).

Mapping con la entrega DSY1105 (Evaluación Parcial 4)
----------------------------------------------------
- Consumo de APIs externas: Implementado (Retrofit + DTOs).
- Microservicios Spring Boot: No hay backend en este repo; repo frontend preparado para consumirlo.
- Pruebas unitarias: Falta implementación de tests, se recomienda añadir.
- Generación archivos APK firmados: Documentado; se requiere keystore local o en CI.
- Documentación técnica: Documentación ampliada en `docs/` (este documento y `FILES_SUMMARY.md`).
- Integración Firebase: Implementada (google-services.json + observer service).

Guía rápida para desarrolladores (cómo correr la app localmente)
--------------------------------------------------------------
1) Prerrequisitos: JDK 17, Android SDK (platform-tools), Android Studio.
2) Desde la raíz del proyecto (PowerShell):

```powershell
# Build y deploy en emulador o dispositivo conectado
.\gradlew clean assembleDebug
.\gradlew installDebug
# Ejecuta lint y tests
.\gradlew lint
.\gradlew test
```

3) Para desarrollo con cambios en Firebase: reemplaza `app/google-services.json` por el del proyecto deseado.

Problemas comunes y soluciones rápidas
-------------------------------------
- Error de versión de SDK: verifica `compileSdk` y `targetSdk` en `app/build.gradle.kts` y que el SDK esté instalado localmente.
- Crash por permisos (cámara/archivo): revisar `AndroidManifest.xml` y pedir permisos en runtime mediante `PermissionHelper`.
- Problemas de sincronización con Firestore: revisar reglas de seguridad y que `google-services.json` corresponda al proyecto.

Siguientes pasos recomendados (prioritarios para entrega DSY1105)
----------------------------------------------------------------
1) Añadir pruebas unitarias para ViewModels críticos (Login, Principal, Carrito, Pedidos).
2) Documentar o añadir un enlace al backend Spring Boot (endpoints y contratos) si existe.
3) Configurar un pipeline CI básico para ejecutar tests y generar artefactos.
4) Generar imágenes de los PlantUML y agregarlas a `docs/diagrams/` si quieres incluir visuales en la entrega.

Contacto y referencias
----------------------
- Para cambios arquitectónicos (por ejemplo migrar a Clean Architecture o añadir DI con Hilt), propongo un plan de refactor por etapas.
- Si quieres que implemente: (a) pruebas unitarias de ejemplo, (b) pipeline CI, o (c) imágenes PlantUML, dime cuál y lo implemento.
