# 🍰 Pastelería Mil Sabores - Aplicación Móvil Android

[![Kotlin](https://img.shields.io/badge/Kotlin-1.9.0-purple.svg)](https://kotlinlang.org/)
[![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-1.5.4-green.svg)](https://developer.android.com/jetpack/compose)
[![Firebase](https://img.shields.io/badge/Firebase-Latest-orange.svg)](https://firebase.google.com/)
[![License](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)

Aplicación móvil nativa Android para la gestión de pedidos de una pastelería, desarrollada con **Jetpack Compose**, **Firebase** y arquitectura **MVVM**.

---

## 📱 Características Principales

- ✅ Autenticación de usuarios con Firebase Auth
- ✅ Catálogo de productos con filtros por categoría
- ✅ Carrito de compras con gestión local
- ✅ Historial de pedidos persistente (SharedPreferences)
- ✅ Captura de foto de perfil con cámara
- ✅ Modo oscuro con persistencia de preferencias
- ✅ Notificaciones push para pedidos
- ✅ UI responsive y adaptable a diferentes tamaños de pantalla
- ✅ Animaciones fluidas y feedback visual

---

## 🏗️ Arquitectura del Proyecto

```
app/src/main/java/
├── cl.duoc.milsabores/
│   ├── MainActivity.kt              # Punto de entrada, inicializa tema y navegación
│   └── MilSaboresApplication.kt     # Inicializa Firebase y servicios globales
│
├── core/
│   ├── AppLogger.kt                 # Sistema de logging centralizado para debug/producción
│   └── Result.kt                    # Clase sellada para manejo de resultados (Success/Error/Loading)
│
├── data/
│   ├── local/
│   │   ├── PedidosLocalStorage.kt   # Guarda/carga pedidos en SharedPreferences con JSON (Gson)
│   │   └── Prefs.kt                 # Gestión de preferencias de usuario (modo oscuro, etc.)
│   └── media/
│       ├── MediaRepository.kt       # Maneja captura de fotos y acceso a galería
│       └── ProfilePhotoManager.kt   # Guarda/carga foto de perfil en almacenamiento interno
│
├── model/
│   ├── CarritoItem.kt              # Modelo de ítem en el carrito (id, nombre, precio, cantidad)
│   └── Pedido.kt                   # Modelo de pedido con productos, total, estado y observaciones
│
├── repository/
│   ├── auth/
│   │   └── AuthRepository.kt       # Operaciones de autenticación con Firebase (login, register, logout)
│   ├── carrito/
│   │   └── CarritoRepository.kt    # Singleton para gestión global del carrito (agregar, actualizar, eliminar)
│   └── pedidos/
│       └── PedidosRepository.kt    # CRUD de pedidos con Firebase Firestore
│
├── service/
│   ├── NotificationHelper.kt       # Crea y muestra notificaciones locales con canales
│   └── PedidosObserverService.kt   # Observa cambios en pedidos de Firebase para notificar
│
├── ui/
│   ├── app/
│   │   ├── AppNavHost.kt           # Configuración de navegación principal con animaciones
│   │   └── Routes.kt               # Definición de rutas de navegación
│   │
│   ├── carrito/
│   │   ├── CarritoScreen.kt        # Pantalla del carrito con resumen, observaciones y finalización
│   │   ├── CarritoItemCard.kt      # Componente de tarjeta de producto en carrito
│   │   └── CarritoViewModel.kt     # Lógica de negocio del carrito (estados, finalizar compra)
│   │
│   ├── home/
│   │   └── HomeScreen.kt           # Pantalla de bienvenida con botones de login/registro
│   │
│   ├── login/
│   │   ├── LoginScreen.kt          # Formulario de login con validaciones en tiempo real
│   │   └── LoginViewModel.kt       # Gestión de autenticación y validación de credenciales
│   │
│   ├── mapper/
│   │   └── EstadoPedidoUi.kt       # Extensión para mapear estados de pedido a colores del tema
│   │
│   ├── model/
│   │   ├── Producto.kt             # Modelo UI de producto (id, título, precio, categoría, imagen)
│   │   ├── ProductosDemo.kt        # Datos de demostración del catálogo de productos
│   │   └── User.kt                 # Modelo de usuario (uid, email, displayName)
│   │
│   ├── pedidos/
│   │   ├── PedidosScreen.kt        # Lista de pedidos del usuario con estados visuales
│   │   ├── DetallePedidoScreen.kt  # Detalle completo de pedido con timeline de estados y miniaturas
│   │   └── PedidosViewModel.kt     # Lógica para cargar, filtrar y gestionar pedidos
│   │
│   ├── principal/
│   │   ├── PrincipalScreen.kt      # Pantalla principal con navegación inferior (Home, Favs, Cart, etc.)
│   │   ├── PrincipalViewModel.kt   # Gestión de productos, categorías, filtros y carrito
│   │   └── components/
│   │       └── UiProductsCard.kt   # Tarjeta de producto con animaciones (pulse, feedback agregado)
│   │
│   ├── profile/
│   │   ├── ProfileScreen.kt        # Pantalla de perfil con foto, datos de usuario y botones
│   │   └── ProfileViewModel.kt     # Gestión de foto de perfil y datos del usuario
│   │
│   ├── recover/
│   │   ├── RecuperarPasswordScreen.kt  # Formulario para recuperar contraseña vía email
│   │   └── RecuperarPasswordViewModel.kt # Lógica de envío de email de recuperación
│   │
│   ├── register/
│   │   ├── RegistrarseScreen.kt    # Formulario de registro con validaciones completas
│   │   └── RegistrarseViewModel.kt # Gestión de creación de cuenta y validaciones
│   │
│   ├── settings/
│   │   └── SettingsScreen.kt       # Configuración de la app (modo oscuro, versión, ayuda)
│   │
│   ├── theme/
│   │   ├── Color.kt                # Paleta de colores personalizada (StrawberryRed, ChocolateBrown, etc.)
│   │   ├── Theme.kt                # Configuración de Material3 con temas claro/oscuro
│   │   └── Type.kt                 # Tipografías personalizadas del proyecto
│   │
│   └── util/
│       └── FormatUtils.kt          # Funciones de formato (clp para pesos chilenos)
│
└── utils/
    └── PermissionHelper.kt         # Helper para verificar y solicitar permisos (cámara, notificaciones)
```

---

## 🎯 Cumplimiento de la Rúbrica

### **IL 2.1 - Diseño de Interfaces Móviles**

#### **IE 2.1.1: Interfaz visual coherente y navegación funcional**
- ✅ **Jerarquía visual clara**: Uso de Material3 con tipografías diferenciadas (headlineSmall, bodyMedium, labelSmall)
- ✅ **Distribución adecuada**: Cards con sombras, espaciados consistentes (16dp, 12dp, 8dp), grids adaptables
- ✅ **Navegación funcional**: AppNavHost con AnimatedNavHost, navegación inferior con 5 secciones (Home, Favs, Cart, Pedidos, Más)
- ✅ **Coherencia visual**: Paleta de colores unificada (StrawberryRed, ChocolateBrown, GradientPink), gradientes en todas las pantallas
- 📂 **Archivos clave**: `PrincipalScreen.kt`, `AppNavHost.kt`, `theme/Color.kt`, `theme/Theme.kt`

#### **IE 2.1.2: Formularios completos con validaciones visuales**
- ✅ **Validación por campo**: Email (pattern), contraseña (mínimo 6 caracteres) con feedback en tiempo real
- ✅ **Retroalimentación clara**: Iconos de estado (CheckCircle, Error), textos de error bajo cada campo
- ✅ **Íconos adecuados**: Email (Outlined.Email), Lock (Outlined.Lock), Visibility para mostrar/ocultar contraseña
- ✅ **Estados visuales**: isError en OutlinedTextField, colores diferenciados (error rojo, success verde)
- 📂 **Archivos clave**: `LoginScreen.kt`, `RegistrarseScreen.kt`, `LoginViewModel.kt`

---

### **IL 2.2 - Funcionalidades Visuales y Gestión de Estado**

#### **IE 2.2.1: Lógica de validación centralizada y desacoplada**
- ✅ **ViewModels separados**: LoginViewModel, CarritoViewModel, PrincipalViewModel, PedidosViewModel
- ✅ **Estados reactivos**: StateFlow con data classes (LoginUiState, CarritoUiState, PrincipalUiState)
- ✅ **Validación desacoplada**: Funciones privadas `validar()` en ViewModels, sin lógica en Composables
- ✅ **Respuesta a cambios**: collectAsState() en UI, actualización automática al cambiar estado
- 📂 **Archivos clave**: `*ViewModel.kt`, `core/Result.kt`, estados en cada módulo UI

#### **IE 2.2.2: Animaciones visuales funcionales**
- ✅ **Transiciones de navegación**: slideInHorizontally, slideOutHorizontally en AppNavHost
- ✅ **Animaciones de contenido**: AnimatedContent, AnimatedVisibility con fadeIn/fadeOut, slideInVertically
- ✅ **Feedback de interacción**: animateFloatAsState para escala al presionar, pulso en productos nuevos
- ✅ **Fluidez visual**: animateContentSize en listas, spring animations con dampingRatio personalizado
- 📂 **Archivos clave**: `UiProductsCard.kt`, `AppNavHost.kt`, `PrincipalScreen.kt`

---

### **IL 2.3 - Almacenamiento Local y Organización**

#### **IE 2.3.1: Almacenamiento local persistente**
- ✅ **SharedPreferences con Gson**: PedidosLocalStorage guarda lista de pedidos como JSON
- ✅ **Preferencias de usuario**: Prefs.kt guarda modo oscuro (darkMode: Boolean)
- ✅ **Almacenamiento de archivos**: ProfilePhotoManager guarda foto en almacenamiento interno
- ✅ **Persistencia entre sesiones**: Los pedidos y preferencias se mantienen al cerrar la app
- 📂 **Archivos clave**: `PedidosLocalStorage.kt`, `Prefs.kt`, `ProfilePhotoManager.kt`

#### **IE 2.3.2: Patrones arquitectónicos y herramientas colaborativas**
- ✅ **MVVM**: Separación clara entre UI (Screens), ViewModel (lógica) y Repository (datos)
- ✅ **Repository Pattern**: AuthRepository, CarritoRepository, PedidosRepository, MediaRepository
- ✅ **Singleton**: CarritoRepository.getInstance() para estado global del carrito
- ✅ **Inyección manual**: ViewModelProvider.Factory para ViewModels con dependencias
- ✅ **Git/GitHub**: Control de versiones con commits descriptivos, branches para features
- 📂 **Archivos clave**: `repository/*`, `*ViewModel.kt`, estructura de carpetas modular

---

### **IL 2.4 - Recursos Nativos del Dispositivo**

#### **IE 2.4.1: Acceso a cámara**
- ✅ **Permisos solicitados**: CAMERA en AndroidManifest, RequestPermission con ActivityResultContracts
- ✅ **Captura de foto**: TakePicture launcher con URI de destino en almacenamiento interno
- ✅ **Integración segura**: PermissionHelper verifica permisos antes de acceder a la cámara
- ✅ **Feedback visual**: Toast al guardar foto, estados de loading/error en ProfileViewModel
- 📂 **Archivos clave**: `ProfileScreen.kt`, `MediaRepository.kt`, `ProfilePhotoManager.kt`, `PermissionHelper.kt`

#### **IE 2.4.2: Notificaciones push**
- ✅ **Canal de notificaciones**: NotificationChannel creado en MainActivity.onCreate()
- ✅ **Permiso POST_NOTIFICATIONS**: Solicitado en Android 13+ con RequestPermission
- ✅ **NotificationHelper**: Crea notificaciones con título, mensaje, ícono y vibración
- ✅ **Observer de pedidos**: PedidosObserverService escucha cambios en Firestore y notifica
- 📂 **Archivos clave**: `NotificationHelper.kt`, `PedidosObserverService.kt`, `MainActivity.kt`

---

## 🚀 Instalación y Configuración

### Requisitos Previos
- **Android Studio** Hedgehog | 2023.1.1 o superior
- **JDK** 17 o superior
- **Android SDK** mínimo API 26 (Android 8.0)
- **Firebase Project** configurado con Authentication y Firestore

### Pasos de Instalación

1. **Clonar el repositorio**
```bash
git clone https://github.com/tuusuario/Proyecto_Pasteleria_Mil_Sabores.git
cd Proyecto_Pasteleria_Mil_Sabores
```

2. **Configurar Firebase**
   - Crear proyecto en [Firebase Console](https://console.firebase.google.com/)
   - Descargar `google-services.json` y colocarlo en `app/`
   - Habilitar **Authentication** (Email/Password)
   - Crear base de datos **Firestore** en modo producción

3. **Sincronizar dependencias**
```bash
# En Android Studio: File > Sync Project with Gradle Files
# O desde terminal:
./gradlew build
```

4. **Ejecutar la aplicación**
   - Conectar dispositivo Android o iniciar emulador
   - Click en **Run** (Shift+F10) en Android Studio

---

## 📦 Dependencias Principales

```kotlin
// Jetpack Compose
implementation("androidx.compose.ui:ui:1.5.4")
implementation("androidx.compose.material3:material3:1.1.2")
implementation("androidx.navigation:navigation-compose:2.7.5")

// Firebase
implementation("com.google.firebase:firebase-auth:22.3.0")
implementation("com.google.firebase:firebase-firestore:24.10.0")
implementation(platform("com.google.firebase:firebase-bom:32.7.0"))

// Coroutines & Flow
implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")

// Image Loading
implementation("io.coil-kt:coil-compose:2.5.0")

// Local Storage
implementation("com.google.code.gson:gson:2.10.1")

// Accompanist (Navegación con animaciones)
implementation("com.google.accompanist:accompanist-navigation-animation:0.32.0")
```

---

## 🎨 Paleta de Colores

```kotlin
val StrawberryRed = Color(0xFFE53935)      // Principal - Botones, acentos
val ChocolateBrown = Color(0xFF3E2723)     // Texto principal
val CaramelGold = Color(0xFFFFB300)        // Acentos dorados
val VanillaWhite = Color(0xFFFFF8E1)       // Fondos claros
val PastelPink = Color(0xFFFCE4EC)         // Acentos suaves
val GradientPink = Color(0xFFF8BBD0)       // Gradientes
val GradientOrange = Color(0xFFFFE0B2)     // Gradientes
val MintGreen = Color(0xFF66BB6A)          // Success states
```

---

## 🧪 Testing

### Ejecutar Tests Unitarios
```bash
./gradlew test
```

### Ejecutar Tests de Instrumentación
```bash
./gradlew connectedAndroidTest
```

---

## 📸 Screenshots

| Pantalla | Descripción |
|----------|-------------|
| Home | Pantalla de bienvenida con login/registro |
| Catálogo | Grid de productos con filtros por categoría |
| Carrito | Resumen de compra con observaciones |
| Pedidos | Historial con estados visuales (Pendiente, En Preparación, Listo, Entregado) |
| Perfil | Foto de perfil con cámara y datos del usuario |

---

## 🔐 Seguridad y Buenas Prácticas

- ✅ **ProGuard habilitado** para ofuscar código en release
- ✅ **Validación de inputs** en todos los formularios
- ✅ **Manejo de errores** con try-catch y Result sealed class
- ✅ **Logging condicional** (solo en debug con AppLogger)
- ✅ **Permisos mínimos** solicitados solo cuando son necesarios
- ✅ **Almacenamiento seguro** de fotos en internal storage

---

## 🛠️ Solución de Problemas Comunes

### Error: "google-services.json not found"
**Solución**: Descargar el archivo desde Firebase Console y colocarlo en `app/`

### Error: "Firebase Auth failed"
**Solución**: Verificar que el usuario esté registrado o usar el botón "Ingresar como invitado"

### Modo oscuro no persiste
**Solución**: Verificar que `Prefs.kt` tenga permisos de escritura en SharedPreferences

### Foto de perfil no se guarda
**Solución**: 
1. Verificar permiso CAMERA en AndroidManifest
2. Aceptar permiso de cámara en tiempo de ejecución
3. Revisar logs con tag "ProfilePhotoManager"

---

## 📝 Roadmap Futuro

- [ ] Implementar pago con Mercado Pago / Transbank
- [ ] Agregar favoritos persistentes
- [ ] Chat en vivo con la pastelería
- [ ] Seguimiento en tiempo real del pedido (GPS)
- [ ] Personalización de productos (sabores, decoraciones)
- [ ] Sistema de puntos y descuentos
- [ ] Compartir productos en redes sociales

---

## 👥 Contribuciones

Las contribuciones son bienvenidas. Por favor:

1. Fork el proyecto
2. Crea una rama para tu feature (`git checkout -b feature/AmazingFeature`)
3. Commit tus cambios (`git commit -m 'Add: AmazingFeature'`)
4. Push a la rama (`git push origin feature/AmazingFeature`)
5. Abre un Pull Request

---

## 📄 Licencia

Este proyecto está bajo la Licencia MIT. Ver archivo `LICENSE` para más detalles.

---

## 📧 Contacto

**Equipo Mil Sabores**
- Email: contacto@milsabores.cl
- GitHub: [@milsabores-team](https://github.com/milsabores-team)

---

## 🙏 Agradecimientos

- **Firebase** por los servicios backend
- **Material Design** por las guías de diseño
- **Jetpack Compose** por la UI declarativa
- **Coil** por la carga eficiente de imágenes
- Comunidad de **Stack Overflow** y **Reddit/AndroidDev**

---

**Desarrollado con ❤️ y mucho ☕ por el equipo de Mil Sabores**

*Versión: 2.2.0 | Última actualización: Octubre 2025*

