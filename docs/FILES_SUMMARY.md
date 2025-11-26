# Resumen completo de archivos — Pastelería Mil Sabores

Última actualización: 2025-11-24

Este documento lista los archivos y carpetas más relevantes del proyecto con una descripción pedagógica (máx. 2 líneas cada una).

-- Raíz del proyecto --

- `build.gradle.kts` — Script Gradle raíz (Kotlin DSL) que configura buildscript, repositorios y plugins del proyecto.
- `settings.gradle.kts` — Define los módulos del proyecto (por ejemplo `:app`) y su estructura.
- `gradle.properties` — Propiedades y flags globales de Gradle; ajusta memoria y opciones del compilador.
- `gradlew` / `gradlew.bat` — Wrappers de Gradle para ejecutar builds sin depender de una instalación global.
- `local.properties` — Contiene la ruta al SDK Android local; no debe versionarse.
- `README.md` — Documento principal con resumen, comandos y enlaces a la documentación técnica.

-- docs/ --

- `docs/ARCHITECTURE.md` — Documentación extensa de arquitectura, decisiones técnicas y guías de despliegue.
- `docs/FILES_SUMMARY.md` — (Este archivo) Listado completo de archivos y descripciones.
- `docs/diagrams/architecture.puml` — Diagrama PlantUML que muestra las capas y relaciones principales de la app.
- `docs/diagrams/order_flow.puml` — Diagrama PlantUML que describe el flujo de creación y observación de pedidos.

-- app/ (módulo Android) --

- `app/build.gradle.kts` — Script de build específico del módulo app con dependencias (Compose, Firebase, Room, Retrofit).
- `app/google-services.json` — Configuración de Firebase para desarrollo (Auth, Firestore, Analytics).
- `app/proguard-rules.pro` — Reglas de ofuscación para builds de release con R8/ProGuard.
- `app/src/main/AndroidManifest.xml` — Manifiesto Android: declara `MainActivity`, permisos, services y providers.

-- app/src/main/java/cl/duoc/milsabores/ (paquete principal) --

- `MilSaboresApplication.kt` — Clase Application que inicializa servicios globales (ej. Firebase) al arrancar la app.
- `MainActivity.kt` — Activity que hospeda la UI Compose y el NavHost de la aplicación.

-- core/ --

- `core/AppLogger.kt` — Utilidad para logging consistente y estructurado durante el desarrollo.
- `core/Result.kt` — Clase sellada que representa estados de operación: Success, Error y Loading.

-- data/remote/ --

- `data/remote/RetrofitClient.kt` — Configura Retrofit y OkHttp (timeout, interceptors, logging).
- `data/remote/ProductApiService.kt` — Interfaz Retrofit con endpoints para obtener productos.
- `data/remote/AuthApiService.kt` — (Si existe) Interfaz para endpoints de autenticación remota.
- `data/remote/dto/ProductoDto.kt` — DTO que mapea la respuesta JSON de producto desde el backend.
- `data/remote/dto/CrearUsuarioRequest.kt` — DTO para enviar datos al crear un usuario remoto.
- `data/remote/dto/LoginRequest.kt` — DTO con credenciales para autenticar a usuarios.
- `data/remote/dto/UsuarioResponseDto.kt` — DTO que representa la respuesta del backend para datos de usuario.

-- data/local/ --

- `data/local/AppDatabase.kt` — Configura Room y expone DAOs para acceso a la base local.
- `data/local/dao/CarritoDao.kt` — DAO con operaciones CRUD para el carrito en SQLite.
- `data/local/dao/PedidoDao.kt` — DAO con operaciones CRUD y consultas para pedidos.
- `data/local/entity/CarritoItemEntity.kt` — Entidad Room que representa un item almacenado en el carrito.
- `data/local/entity/PedidoEntity.kt` — Entidad Room que representa un pedido persistido localmente.
- `data/local/CarritoLocalStorage.kt` — Implementación local para gestionar el carrito (add/remove/update).
- `data/local/PedidosLocalStorageSQLite.kt` — Implementación de persistencia de pedidos y sincronización parcial con remoto.
- `data/local/RecordatorioEntity.kt` — Entidad que guarda recordatorios programados en la DB local.
- `data/local/ReminderDAO.kt` — DAO para CRUD de recordatorios.
- `data/local/ProfilePhotoManager.kt` — Gestiona guardado y recuperación de fotos de perfil en almacenamiento interno.
- `data/local/Prefs.kt` — Abstracción sencilla para acceder a SharedPreferences y flags del usuario.

-- data/carrito/ --

- `data/carrito/CarritoRepository.kt` — Lógica de negocio del carrito: totales, agregados y persistencia local.
- `data/carrito/CarritoItemCard.kt` — (Componente/holder) Representa datos/UI para cada item del carrito.

-- data/media/ --

- `data/media/MediaRepository.kt` — Provee URIs y helpers para captura y manejo de imágenes con FileProvider.

-- repository/ --

- `repository/CarritoRepository.kt` — Repositorio de alto nivel que combina fuentes locales y lógicas para la UI.
- `repository/PedidosRepository.kt` — Gestiona creación, consulta y actualización de pedidos (sincroniza local y remoto).
- `repository/auth/RecordatorioRepository.kt` — Repositorio para manejar recordatorios y su persistencia.

-- model/ --

- `model/Producto.kt` — Data class que modela un producto (id, nombre, precio, imagen, categoría).
- `model/Pedido.kt` — Data class que describe la estructura completa de un pedido y sus metadatos.
- `model/CarritoItem.kt` — Data class para items del carrito con cálculo de subtotal.
- `model/Recordatorio.kt` — Modelo para los recordatorios programados por el usuario.
- `model/user.kt` — Modelo de usuario con uid, email y displayName.
- `model/mappers/ProductoMapper.kt` — Funciones para convertir DTOs a modelos de dominio.
- `model/mappers/RecordatorioMappers.kt` — Mapea entidades de DB a modelos usados en la UI.

-- ui/app/ --

- `ui/app/Routes.kt` — Definición central de rutas/nombres para la navegación entre pantallas.
- `ui/app/AppNavHost.kt` — NavHost principal que orquesta la navegación Compose entre destinos.

-- ui/home/ --

- `ui/home/HomeScreen.kt` — Pantalla de bienvenida con opciones de acceso (login/registro/invitado).
- `ui/home/components/AnimatedLogo.kt` — Componente animado reutilizable para el logo en la pantalla de inicio.

-- ui/login/ --

- `ui/login/LoginScreen.kt` — Pantalla de login con formulario y validaciones visuales.
- `ui/login/LoginViewModel.kt` — Lógica de autenticación y manejo del estado del login.

-- ui/register/ --

- `ui/register/RegistrarseScreen.kt` — Pantalla para registrar nuevos usuarios con validaciones.
- `ui/register/RegistrarseViewModel.kt` — Lógica de registro y llamada a repositorios de auth.

-- ui/recover/ --

- `ui/recover/RecuperarPasswordScreen.kt` — UI para solicitar recuperación de contraseña por email.
- `ui/recover/RecuperarPasswordViewModel.kt` — Gestiona la lógica para enviar la solicitud a Firebase o backend.

-- ui/principal/ --

- `ui/principal/PrincipalScreen.kt` — Pantalla principal con grid/lista de productos y navegación inferior.
- `ui/principal/PrincipalViewModel.kt` — Orquestador que carga productos, aplica filtros y administra el carrito.
- `ui/principal/components/UiProductsCard.kt` — Componente que muestra la tarjeta de cada producto.

-- ui/components/ --

- `ui/components/SkeletonLoader.kt` — Skeletons (placeholders) con efecto shimmer durante la carga.
- `ui/components/AnimatedComponents.kt` — Componentes visuales con animaciones reutilizables.
- `ui/components/GradientComponents.kt` — Helpers para aplicar gradientes en tarjetas y botones.
- `ui/components/ResponsiveComponents.kt` — Componentes adaptativos para distintos tamaños de pantalla.
- `ui/components/GradientComponents.kt` — (repetido) Helpers de gradiente y estilos.

-- ui/carrito/ --

- `ui/carrito/CarritoScreen.kt` — Pantalla que muestra items del carrito, totales y acciones para finalizar compra.
- `ui/carrito/CarritoViewModel.kt` — Handle del estado del carrito y acciones (incrementar, eliminar, finalizar).

-- ui/pedidos/ --

- `ui/pedidos/PedidosScreen.kt` — Lista de pedidos con filtros y estados visuales.
- `ui/pedidos/DetallePedidoScreen.kt` — Detalle de un pedido con timeline de estados y acciones.
- `ui/pedidos/PedidosViewModel.kt` — Lógica para cargar y sincronizar pedidos con la fuente remota.
- `ui/mapper/EstadoPedidoUi.kt` — Mapea estados de pedido a colores y etiquetas de UI.

-- ui/profile/ --

- `ui/profile/ProfileScreen.kt` — Muestra y permite editar datos del perfil y foto del usuario.
- `ui/profile/ProfileViewModel.kt` — Lógica para subir, refrescar o eliminar la foto de perfil.

-- ui/settings/ --

- `ui/settings/SettingsScreen.kt` — Pantalla de configuración con flags (modo oscuro, ayuda y versiones).

-- ui/recordatorio/ --

- `ui/recordatorio/RecordatorioScreen.kt` — UI para crear y listar recordatorios programados en la app.
- `ui/recordatorio/RecordatorioViewModel.kt` — Lógica para crear, observar y eliminar recordatorios.

-- ui/utils y vmfactory --

- `ui/utils/Formatters.kt` — Funciones que formatean números y cadenas (p.ej. moneda CLP).
- `ui/utils/ResponsiveUtils.kt` — Helpers para calcular tamaños y márgenes responsivos.
- `ui/vmfactory/ProfileVMFactory.kt` — Factory para crear `ProfileViewModel` con dependencias.
- `ui/vmfactory/RecordatorioVMFactory.kt` — Factory para crear `RecordatorioViewModel` con dependencias.

-- notifications & service --

- `notifications/NotificationHelper.kt` — Crea canales y envía notificaciones locales al usuario.
- `service/PedidosObserverService.kt` — Servicio que escucha Firestore y genera notificaciones de cambios.

-- utils --

- `utils/PermissionHelper.kt` — Helpers para solicitar y verificar permisos runtime (cámara, almacenamiento).
- `utils/CLP.kt` — Extensión para formatear números a CLP (moneda chilena).


---

> Nota: este archivo resume los elementos más relevantes detectados en el repositorio. Si quieres que transforme esto en una tabla paginada en el README o que incluya archivos adicionales del build/intermediates, dímelo y lo agrego.

