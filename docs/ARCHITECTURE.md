# Arquitectura y descripción de archivos — Pastelería Mil Sabores

Este documento contiene descripciones detalladas y pedagógicas (máximo 2 líneas por archivo) de los archivos y carpetas relevantes del proyecto.

Última actualización: 2025-11-24

-- Raíz del proyecto --
- `build.gradle.kts` — Script Gradle raíz (Kotlin DSL) que configura repositorios y plugins del proyecto.
- `settings.gradle.kts` — Define los módulos del proyecto (incluye `:app`).
- `gradle.properties` — Propiedades globales de Gradle (memoria JVM, AndroidX, estilo Kotlin).
- `gradlew` — Ejecutable wrapper para ejecutar Gradle en Windows sin instalarlo globalmente.
- `local.properties` — Ruta local del SDK y configuración específica de la máquina (no versionada).
- `README.md` — Documentación principal del proyecto; contiene resumen y guía rápida.

-- Módulo app --
- `app/build.gradle.kts` — Script de build del módulo app; lista dependencias (Compose, Firebase, Room) y settings de Android.
- `app/google-services.json` — Configuración de Firebase para la app; contiene project_id, api_key y mobileSDK id.
- `app/proguard-rules.pro` — Reglas de ofuscación para builds de release con ProGuard/R8.

-- Recursos (carpetas principales) --
- `app/src/main/res/drawable/` — Imágenes y vectores usados en la UI (logos, placeholders).
- `app/src/main/res/mipmap-*/` — Iconos de launcher en distintas densidades (hdpi, xhdpi, etc.).
- `app/src/main/res/values/` — Valores XML: colores, strings y temas usados por la app.
- `app/src/main/res/xml/file_paths.xml` — Configuración de `FileProvider` para compartir imágenes de la cámara.

-- Manifiesto y entrada --
- `app/src/main/AndroidManifest.xml` — Declara actividades, permisos y providers (FileProvider) de la app.
- `app/src/main/res/values/strings.xml` — Contiene `app_name` y otros strings estáticos.

-- Código fuente (paquete principal: `cl.duoc.milsabores`) --
- `MilSaboresApplication.kt` — Clase `Application` que inicializa servicios globales (ej. Firebase) al inicio.
- `MainActivity.kt` — Activity principal que monta el `NavHost` y establece el tema de la app.

-- core --
- `core/AppLogger.kt` — Utilidad central de logging para mensajes estructurados durante el desarrollo.
- `core/Result.kt` — Clase sellada para representar estados de resultado (Success, Error, Loading).

-- data/ (repositorios y acceso a datos) --
- `data/repository/ProductRepository.kt` — Fuente de productos; orquesta fetch remoto y local según conexión.
- `data/repository/AuthRepository.kt` — Encapsula operaciones de autenticación (login, registro, logout).
- `data/remote/RetrofitClient.kt` — Configura Retrofit/OkHttp para consumir APIs REST externas.
- `data/remote/ProductApiService.kt` — Interfaz Retrofit que define endpoints para obtener productos.
- `data/remote/AuthApiService.kt` — Interfaz Retrofit para endpoints de autenticación (si se usa backend REST).
- `data/remote/dto/ProductoDto.kt` — DTO que mapea la respuesta de producto desde el backend.
- `data/remote/dto/CrearUsuarioRequest.kt` — DTO para enviar datos de creación de usuario a la API.
- `data/remote/dto/LoginRequest.kt` — DTO con credenciales para login remoto.
- `data/remote/dto/UsuarioResponseDto.kt` — DTO que representa la respuesta de usuario desde la API.

-- data/local (Room / almacenamiento local) --
- `data/local/AppDatabase.kt` — Configura la base de datos Room y expone DAOs.
- `data/local/dao/PedidoDao.kt` — DAO para operaciones sobre entidades `Pedido` en la base local.
- `data/local/dao/CarritoDao.kt` — DAO para CRUD de items del carrito en SQLite.
- `data/local/entity/PedidoEntity.kt` — Entidad Room que representa un pedido almacenado localmente.
- `data/local/entity/CarritoItemEntity.kt` — Entidad Room que representa un item en el carrito.
- `data/local/CarritoLocalStorage.kt` — Implementación local del almacenamiento del carrito (CRUD simple).
- `data/local/PedidosLocalStorageSQLite.kt` — Maneja persistencia de pedidos y sincronización con Firebase.
- `data/local/ReminderDAO.kt` — DAO para recordatorios locales (si se implementan alarmas/recordatorios).
- `data/local/RecordatorioEntity.kt` — Entidad que guarda recordatorios programados en la DB.
- `data/local/ProfilePhotoManager.kt` — Gestiona guardado y carga de fotos de perfil en internal storage.
- `data/local/Prefs.kt` — Abstracción para acceder a preferencias (modo oscuro, settings de usuario).

-- data/carrito --
- `data/carrito/CarritoRepository.kt` — Lógica del carrito (añadir, actualizar cantidad, eliminar, totales).
- `data/carrito/CarritoItemCard.kt` — Componente de datos/UI para representar un item del carrito.

-- data/media --
- `data/media/MediaRepository.kt` — Provee URIs con `FileProvider` para captura y manejo de imágenes.

-- repository (capas de alto nivel) --
- `repository/CarritoRepository.kt` — Repositorio que expone API para la UI usando `data` local/remote.
- `repository/PedidosRepository.kt` — Orquesta creación y consulta de pedidos (local + Firebase).
- `repository/auth/RecordatorioRepository.kt` — Repositorio para manejar recordatorios (si aplica).

-- model (clases de dominio) --
- `model/Producto.kt` — Data class que representa un producto con título, precio, categoría e imagen.
- `model/Pedido.kt` — Data class que contiene la estructura completa de un pedido.
- `model/CarritoItem.kt` — Data class para items del carrito con subtotal calculado.
- `model/Recordatorio.kt` — Modelo para recordatorios programados dentro de la app.
- `model/user.kt` — Modelo de usuario con campos básicos (uid, email, displayName).
- `model/mappers/RecordatorioMappers.kt` — Mappers para convertir entre entidades y modelos de UI.

-- ui (interfaz y pantallas) --
- `ui/app/Routes.kt` — Define rutas/nombres de destino para la navegación entre pantallas.
- `ui/app/AppNavHost.kt` — Contiene el `NavHost` animado que gestiona la navegación Compose.

-- ui/home --
- `ui/home/HomeScreen.kt` — Pantalla de inicio con botones de acceso (login, registro, invitado) y banner.
- `ui/home/components/AnimatedLogo.kt` — Logo animado usado en el home para mejorar la entrada visual.

-- ui/login --
- `ui/login/LoginScreen.kt` — UI de inicio de sesión con validaciones visuales en tiempo real.
- `ui/login/LoginViewModel.kt` — Lógica de validación y llamadas a `AuthRepository` para login.

-- ui/register --
- `ui/register/RegistrarseScreen.kt` — Pantalla de registro con formulario de nombre, email y password.
- `ui/register/RegistrarseViewModel.kt` — Lógica de registro, validaciones y manejo de estado.

-- ui/recover --
- `ui/recover/RecuperarPasswordScreen.kt` — Pantalla para solicitar recuperación de contraseña por email.
- `ui/recover/RecuperarPasswordViewModel.kt` — Ejecuta la lógica para enviar solicitud de recuperación.

-- ui/principal --
- `ui/principal/PrincipalScreen.kt` — Pantalla principal con grid/lista de productos y bottom navigation.
- `ui/principal/PrincipalViewModel.kt` — Orquesta carga de productos, filtros y manejo del carrito en UI.
- `ui/principal/components/UiProductsCard.kt` — Componente UI para mostrar cada producto en la lista.

-- ui/components (UI reutilizable) --
- `ui/components/SkeletonLoader.kt` — Skeletons con shimmer para mostrar durante carga de contenidos.
- `ui/components/AnimatedComponents.kt` — Pequeños componentes con animaciones reutilizables.
- `ui/components/GradientComponents.kt` — Helpers para aplicar gradientes y estilos visuales.
- `ui/components/ResponsiveComponents.kt` — Componentes que adaptan su layout según tamaño de pantalla.

-- ui/carrito --
- `ui/carrito/CarritoScreen.kt` — Pantalla del carrito con listado de items, totales y acciones de compra.
- `ui/carrito/CarritoViewModel.kt` — Maneja el estado del carrito y operaciones (incrementar, finalizar).

-- ui/pedidos --
- `ui/pedidos/PedidosScreen.kt` — Lista de pedidos guardados con estados visuales y skeleton loaders.
- `ui/pedidos/DetallePedidoScreen.kt` — Pantalla de detalle de pedido con timeline de estados.
- `ui/pedidos/PedidosViewModel.kt` — Carga y gestión de pedidos (local + sincronización remota).
- `ui/mapper/EstadoPedidoUi.kt` — Mapea estados de pedido a colores y etiquetas de UI.

-- ui/profile --
- `ui/profile/ProfileScreen.kt` — Pantalla de perfil con foto, datos y botones para cambiar foto o cerrar sesión.
- `ui/profile/ProfileViewModel.kt` — Lógica para subir/eliminar foto y actualizar datos de usuario.

-- ui/settings --
- `ui/settings/SettingsScreen.kt` — Pantalla de configuración (modo oscuro, versión, ayuda).

-- ui/recordatorio --
- `ui/recordatorio/RecordatorioScreen.kt` — UI para ver y gestionar recordatorios programados.
- `ui/recordatorio/RecordatorioViewModel.kt` — Lógica para crear y manejar recordatorios.

-- ui/utils y vmfactory --
- `ui/utils/Formatters.kt` — Utilidades para formateo (p.ej. moneda CLP) y helpers UI.
- `ui/utils/ResponsiveUtils.kt` — Funciones para cálculo de dimensiones responsive.
- `ui/vmfactory/ProfileVMFactory.kt` — Factory para crear ViewModels con dependencias necesarias.
- `ui/vmfactory/RecordatorioVMFactory.kt` — Factory para crear el ViewModel de recordatorios.

-- notifications & service --
- `notifications/NotificationHelper.kt` — Crea y administra canales y notificaciones locales.
- `service/PedidosObserverService.kt` — Servicio que escucha cambios en Firestore y notifica al usuario.

-- utils (varios) --
- `utils/PermissionHelper.kt` — Helpers para chequear y solicitar permisos runtime de Android.
- `utils/CLP.kt` — Extensiones para formatear números a moneda chilena (CLP).

-- model mappers y DTOs --
- `model/mappers/RecordatorioMappers.kt` — Funciones para convertir entre entidad y modelo de recordatorio.

Si quieres que amplíe alguna descripción o genere diagramas (UML/plantUML), dímelo y lo agrego.

