# Resumen completo de archivos — Pastelería Mil Sabores

Última actualización: 2024-07-26

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

- `MainActivity.kt` — Activity que hospeda la UI Compose y el NavHost de la aplicación.
- `MilSaboresApplication.kt` — Clase Application que inicializa servicios globales (ej. Firebase) al arrancar la app.

-- core/ --

- `core/AppLogger.kt` — Utilidad para logging consistente y estructurado durante el desarrollo.
- `core/Result.kt` — Clase sellada que representa estados de operación: Success, Error y Loading.

-- data/ --

- `data/remote/` — Contiene la configuración de Retrofit y las interfaces para las APIs remotas.
- `data/remote/RetrofitClient.kt` — Configura Retrofit y OkHttp (timeout, interceptors, logging).
- `data/remote/ProductApiService.kt` — Interfaz Retrofit con endpoints para obtener productos.
- `data/remote/AuthApiService.kt` — Interfaz para endpoints de autenticación remota.
- `data/remote/dto/*` — Data Transfer Objects (DTOs) para mapear respuestas y requests JSON.
- `data/local/` — Clases relacionadas con la base de datos local (Room).
- `data/local/AppDatabase.kt` — Configura Room y expone DAOs para acceso a la base local.
- `data/local/dao/*` — Data Access Objects (DAOs) con operaciones CRUD para las entidades.
- `data/local/entity/*` — Entidades Room que representan las tablas de la base de datos.
- `data/local/Prefs.kt` — Abstracción sencilla para acceder a SharedPreferences.
- `data/media/` — Clases para manejar archivos multimedia.
- `data/media/MediaRepository.kt` — Provee URIs y helpers para captura y manejo de imágenes.
- `data/carrito/` — Clases específicas para el carrito de compras.
- `data/carrito/CarritoItemCard.kt` — Componente/holder para cada item del carrito.
- `data/repository/` — Implementaciones de repositorios que acceden a los datos.
- `data/repository/AuthRepository.kt` — Implementación del repositorio de autenticación.
- `data/repository/ProductRepository.kt` — Implementación del repositorio de productos.
- `data/repository/TimeRepository.kt` — Implementación del repositorio de tiempo.
- `data/repository/auth/IAuthProvider.kt` — Interfaz que define el contrato para proveedores de autenticación.
- `data/repository/pedidos/IPedidosStorage.kt` — Interfaz que define el contrato para el almacenamiento de pedidos.

-- repository/ --

- `repository/CarritoRepository.kt` — Repositorio de alto nivel que combina fuentes locales y lógicas para la UI.
- `repository/PedidosRepository.kt` — Gestiona creación, consulta y actualización de pedidos.
- `repository/auth/` — Repositorio para la autenticación.

-- model/ --

- `model/Producto.kt` — Data class que modela un producto.
- `model/Pedido.kt` — Data class que describe la estructura completa de un pedido.
- `model/CarritoItem.kt` — Data class para items del carrito.
- `model/Recordatorio.kt` — Modelo para los recordatorios.
- `model/user.kt` — Modelo de usuario.
- `model/mappers/*` — Funciones para convertir DTOs a modelos de dominio.

-- ui/ --

- `ui/app/` — Componentes y lógica de navegación principal.
- `ui/home/` — Pantalla de bienvenida.
- `ui/login/` — Pantalla de inicio de sesión y su ViewModel.
- `ui/register/` — Pantalla de registro y su ViewModel.
- `ui/recover/` — Pantalla y ViewModel para recuperar contraseña.
- `ui/principal/` — Pantalla principal de la aplicación (catálogo de productos).
- `ui/components/` — Componentes de UI reutilizables.
- `ui/carrito/` — Pantalla y ViewModel para el carrito de compras.
- `ui/pedidos/` — Pantallas y ViewModels para listar y ver detalles de pedidos.
- `ui/profile/` — Pantalla y ViewModel para el perfil de usuario.
- `ui/settings/` — Pantalla de configuraciones.
- `ui/recordatorio/` — Pantalla y ViewModel para los recordatorios.
- `ui/utils/` — Clases de utilidad para la UI.
- `ui/mapper/` — Clases para mapear datos a modelos específicos de la UI.

-- notifications & service --

- `notifications/NotificationHelper.kt` — Crea canales y envía notificaciones locales.
- `service/PedidosObserverService.kt` — Servicio que escucha Firestore y genera notificaciones.

-- utils --

- `utils/PermissionHelper.kt` — Helpers para solicitar y verificar permisos.
- `utils/CLP.kt` — Extensión para formatear números a CLP.
