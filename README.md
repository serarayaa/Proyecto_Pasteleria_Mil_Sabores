# 🍰 Pastelería Mil Sabores - Aplicación Móvil Android

[![Kotlin](https://img.shields.io/badge/Kotlin-2.0.21-purple.svg)](https://kotlinlang.org/)
[![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-BOM%202024.09.00-green.svg)](https://developer.android.com/jetpack/compose)
[![Firebase](https://img.shields.io/badge/Firebase-BOM%2033.2.0-orange.svg)](https://firebase.google.com/)
[![Material3](https://img.shields.io/badge/Material3-1.x-blue.svg)](https://m3.material.io/)
[![Room](https://img.shields.io/badge/SQLite-Room%202.8.1-blue.svg)](https://developer.android.com/training/data-storage/room)
[![Gradle](https://img.shields.io/badge/Android%20Gradle%20Plugin-8.13.0-green.svg)](https://developer.android.com/build)
[![MinSDK](https://img.shields.io/badge/MinSDK-24-brightgreen.svg)](https://developer.android.com/)
[![TargetSDK](https://img.shields.io/badge/TargetSDK-36-brightgreen.svg)](https://developer.android.com/)

Aplicación móvil nativa Android **de nivel comercial** para la gestión completa de pedidos de una pastelería, desarrollada con **Jetpack Compose**, **Firebase**, **Room (SQLite)**, **MVVM**, **Coroutines** y **Material Design 3**.

**Versión funcional de la app:** 3.0.x  
**Última actualización del código:** 22 de noviembre de 2025  
**Estado:** ✅ **Compila y ejecuta correctamente en Android Studio (sin errores de build)**

---

## ✨ Novedades recientes

### 🌙 Modo Oscuro mejorado
- Negro profundo para fondos (`#0D0D0D`).
- Colores vibrantes para acentos (rosa/naranja).
- Mejor contraste para accesibilidad (WCAG).

### 💀 Skeleton Loaders
- Skeletons para:
  - Lista de productos.
  - Lista de pedidos.
- Reemplazan spinners genéricos → UX más profesional.

### 🗄️ Room Database + SQLite
- Base de datos `AppDatabase` con:
  - `CarritoDao` / `CarritoItemEntity`
  - `PedidoDao` / `PedidoEntity`
- Carrito y pedidos **persisten aunque se cierre la app**.
- Integración con repositorios (`CarritoRepository`, `PedidosLocalStorageSQLite`, etc.).

### 🔔 Notificaciones Android 13+
- Canal de notificaciones `orders`.
- `NotificationHelper` actualizado:
  - Verifica permiso `POST_NOTIFICATIONS`.
  - Evita `SecurityException` en Android 13+.

### 🔐 Login real con Firebase + Backend
- Login con **FirebaseAuth**.
- Llamada opcional a backend vía `AuthRepository` y `AuthApiService` (Retrofit).
- Modo invitado soportado desde `LoginViewModel`.

---

## 📱 Características principales

### 🔐 Autenticación y usuarios
- Registro de nuevos usuarios con validaciones.
- Inicio de sesión:
  - Firebase Authentication (correo/contraseña).
  - Modo invitado.
- Recuperar contraseña vía email.
- Manejo de estados con `LoginUiState` + `LoginViewModel`.

### 🛍️ Catálogo y productos
- Pantalla principal con catálogo de productos:
  - Grid adaptable con Jetpack Compose.
  - Cards visuales de producto.
- Skeleton loaders mientras se cargan datos.
- Preparado para integrar productos desde backend (Retrofit).

### 🛒 Carrito de compras
- Agregar, incrementar, decrementar y eliminar productos.
- Cálculo automático de subtotal y total.
- Observaciones para el pedido.
- Persistencia con Room:
  - `CarritoItemEntity`, `CarritoDao`, `CarritoLocalStorage`.
- Lógica de negocio concentrada en `CarritoViewModel` y `CarritoRepository`.

### 📦 Historial de pedidos
- Guardado de pedidos en:
  - Room / SQLite (`PedidoEntity`, `PedidoDao`, `PedidosLocalStorageSQLite`).
  - Integración lista para backend / Firestore.
- Pantalla de lista de pedidos con:
  - Skeleton loaders.
  - Estados visuales (`Pendiente`, `En preparación`, `Listo`, `Entregado`).
- Detalle del pedido:
  - Productos incluidos.
  - Total.
  - Observaciones.
  - Estado actual.

### 👤 Perfil de usuario
- Datos básicos del usuario: nombre, email, UID.
- Foto de perfil:
  - Captura con cámara usando `FileProvider`.
  - Guardado en almacenamiento interno (`ProfilePhotoManager`).
- Modo invitado con vista restringida.

### ⚙️ Configuración
- Modo claro / oscuro persistente (`Prefs` / DataStore-Ready).
- Información de versión.
- Atajos a pantallas clave.

---

## 🏗️ Arquitectura

La app sigue el patrón **MVVM**, con capas separadas para UI, ViewModels, repositorios y datos.

### Diagrama simplificado

```text
UI (Compose Screens)  →  ViewModel  →  Repository  →  Data Sources
(Home, Login,         (LoginVM,       (AuthRepo,      (Firebase Auth,
 Carrito, Pedidos,     CarritoVM,      CarritoRepo,    Room / SQLite,
 Perfil, etc.)         PedidosVM,      PedidosRepo,    Retrofit API)
                       ProfileVM, …)   Recordatorio…)
