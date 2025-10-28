# 🎂 Pastelería Mil Sabores - App Móvil

Aplicación móvil Android desarrollada con **Jetpack Compose** y **Firebase** para la gestión de pedidos de una pastelería.

## 📱 Características Principales

### ✨ Funcionalidades Implementadas

- **🔐 Autenticación de Usuarios**
  - Login con Firebase Authentication
  - Registro de nuevos usuarios
  - Recuperación de contraseña
  - Validación de formularios en tiempo real

- **🛒 Gestión de Carrito**
  - Agregar/eliminar productos
  - Actualizar cantidades
  - Cálculo automático de totales
  - Observaciones personalizadas

- **📦 Sistema de Pedidos**
  - Creación de pedidos
  - Seguimiento en tiempo real
  - Estados: Pendiente, En Preparación, Listo, Entregado
  - Historial de pedidos

- **🔔 Notificaciones Push**
  - Notificación al crear pedido
  - Alertas de cambio de estado
  - Canales de notificación personalizados

- **📝 Recordatorios**
  - Base de datos local con Room
  - CRUD completo de recordatorios
  - Persistencia offline

- **📸 Perfil de Usuario**
  - Captura de foto con cámara
  - Guardado en galería
  - Gestión de permisos

- **🎨 Diseño UI/UX**
  - Material Design 3
  - Animaciones suaves
  - Tema personalizado con colores de pastelería
  - Navegación intuitiva con Bottom Navigation

## 🏗️ Arquitectura

### Patrón MVVM (Model-View-ViewModel)

```
app/
├── data/              # Capa de datos
│   ├── local/        # Base de datos Room
│   └── media/        # Repositorio de medios
├── model/            # Modelos de datos
├── repository/       # Repositorios
│   └── auth/        # Autenticación
├── service/          # Servicios de background
├── notifications/    # Sistema de notificaciones
├── ui/              # Interfaz de usuario
│   ├── app/         # Navegación
│   ├── home/        # Pantalla principal
│   ├── login/       # Autenticación
│   ├── principal/   # Pantalla principal
│   ├── carrito/     # Carrito de compras
│   ├── pedidos/     # Gestión de pedidos
│   ├── profile/     # Perfil de usuario
│   └── recordatorio/ # Recordatorios
└── viewmodel/       # ViewModels
```

## 🛠️ Tecnologías Utilizadas

### Core
- **Kotlin** - Lenguaje de programación
- **Jetpack Compose** - UI moderna y declarativa
- **Material Design 3** - Sistema de diseño

### Firebase
- **Firebase Authentication** - Autenticación de usuarios
- **Cloud Firestore** - Base de datos en tiempo real
- **Firebase Cloud Messaging** - Notificaciones push

### Persistencia Local
- **Room Database** - Base de datos local SQLite
- **DataStore** - Almacenamiento de preferencias

### Arquitectura
- **MVVM** - Patrón de arquitectura
- **StateFlow** - Gestión de estado reactivo
- **Coroutines** - Programación asíncrona
- **Navigation Compose** - Navegación entre pantallas

### Inyección de Dependencias
- **ViewModelFactory** - Creación de ViewModels con dependencias

### Multimedia
- **Coil** - Carga de imágenes
- **Camera API** - Captura de fotos

## 📋 Requisitos Previos

- Android Studio Hedgehog | 2023.1.1 o superior
- JDK 17
- SDK Android 24 (mínimo) - SDK 34 (target)
- Gradle 8.0+
- Cuenta de Firebase configurada

## 🚀 Instalación y Configuración

### 1. Clonar el repositorio

```bash
git clone https://github.com/TU_USUARIO/Proyecto_Pasteleria_Mil_Sabores.git
cd Proyecto_Pasteleria_Mil_Sabores
```

### 2. Configurar Firebase

1. Crea un proyecto en [Firebase Console](https://console.firebase.google.com/)
2. Agrega una aplicación Android con el package name: `cl.duoc.milsabores`
3. Descarga el archivo `google-services.json`
4. Colócalo en `app/google-services.json`

### 3. Configurar variables locales

Crea o verifica el archivo `local.properties` en la raíz del proyecto:

```properties
sdk.dir=C\:\\Users\\TU_USUARIO\\AppData\\Local\\Android\\Sdk
```

### 4. Compilar y ejecutar

```bash
# Limpiar proyecto
./gradlew clean

# Compilar
./gradlew build

# Ejecutar en dispositivo/emulador
./gradlew installDebug
```

O desde Android Studio:
- Build → Clean Project
- Build → Rebuild Project
- Run → Run 'app'

## 📦 Dependencias Principales

```kotlin
// Compose
implementation("androidx.compose.ui:ui:1.5.4")
implementation("androidx.compose.material3:material3:1.1.2")

// Firebase
implementation(platform("com.google.firebase:firebase-bom:32.7.0"))
implementation("com.google.firebase:firebase-auth-ktx")
implementation("com.google.firebase:firebase-firestore-ktx")

// Room
implementation("androidx.room:room-runtime:2.6.1")
ksp("androidx.room:room-compiler:2.6.1")

// Coroutines
implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")

// Navigation
implementation("androidx.navigation:navigation-compose:2.7.6")

// Coil (imágenes)
implementation("io.coil-kt:coil-compose:2.5.0")
```

## 🎨 Tema de Colores

El proyecto utiliza una paleta de colores inspirada en una pastelería:

- **PastelPink** `#FFB6C1` - Rosa pastel
- **PastelPeach** `#FFDAB9` - Durazno pastel
- **ChocolateBrown** `#8B4513` - Café chocolate
- **StrawberryRed** `#FF6B9D` - Rojo fresa
- **CaramelGold** `#FFD54F` - Dorado caramelo
- **VanillaWhite** `#FFFAF0` - Blanco vainilla

## 📱 Pantallas

1. **Home** - Pantalla de bienvenida con animaciones
2. **Login** - Inicio de sesión con validaciones
3. **Registro** - Creación de cuenta nueva
4. **Principal** - Catálogo de productos con filtros
5. **Carrito** - Gestión del carrito de compras
6. **Pedidos** - Listado y seguimiento de pedidos
7. **Perfil** - Gestión de perfil y foto
8. **Recordatorios** - CRUD de recordatorios

## 🔒 Permisos Requeridos

```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.POST_NOTIFICATIONS" />
<uses-permission android:name="android.permission.CAMERA" />
<uses-permission android:name="android.permission.READ_MEDIA_IMAGES" />
<uses-permission android:name="android.permission.VIBRATE" />
```

## 📊 Base de Datos

### Firebase Firestore

```
usuarios/
  └── {userId}
      ├── email: String
      ├── nombre: String
      └── fechaRegistro: Timestamp

pedidos/
  └── {pedidoId}
      ├── userId: String
      ├── productos: Array
      ├── total: Number
      ├── estado: String
      ├── fecha: Timestamp
      └── observaciones: String

productos/
  └── {productoId}
      ├── nombre: String
      ├── precio: Number
      ├── categoria: String
      ├── imagen: String
      └── disponible: Boolean
```

### Room Database Local

```sql
CREATE TABLE recordatorios (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    titulo TEXT NOT NULL,
    descripcion TEXT,
    fecha LONG NOT NULL,
    completado INTEGER NOT NULL DEFAULT 0
)
```

## 🧪 Testing

```bash
# Tests unitarios
./gradlew test

# Tests de instrumentación
./gradlew connectedAndroidTest
```

## 📝 Documentación Adicional

- [CUMPLIMIENTO_RUBRICA_COMPLETO.md](CUMPLIMIENTO_RUBRICA_COMPLETO.md) - Verificación de cumplimiento de rúbrica
- [ERRORES_CORREGIDOS.md](ERRORES_CORREGIDOS.md) - Historial de errores corregidos
- [MEJORAS_FINALES.md](MEJORAS_FINALES.md) - Mejoras implementadas
- [PEDIDOS_IMPLEMENTACION.md](PEDIDOS_IMPLEMENTACION.md) - Documentación del sistema de pedidos

## 🤝 Contribuciones

Este es un proyecto académico desarrollado para el curso de Aplicaciones Móviles.

## 👥 Autores

- Desarrollador Principal - [Tu Nombre]
- Institución: DUOC UC

## 📄 Licencia

Este proyecto es de uso académico y educativo.

## 🙏 Agradecimientos

- DUOC UC - Instituto Profesional
- Firebase - Plataforma de desarrollo
- Jetpack Compose - Framework UI moderno
- Material Design - Sistema de diseño

---

⭐ **Proyecto desarrollado con ❤️ para Aplicaciones Móviles** ⭐

